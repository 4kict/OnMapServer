package gr.ru.processData;

import gr.ru.HashMapDB;
import gr.ru.dao.Notific;
import gr.ru.dao.NotificDAO;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.gutil;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client.ServerStat;
import gr.ru.netty.protokol.Packs2Server.CmdFromUser;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UserCommand implements HandleTelegramm {
    private static final Logger LOG = LogManager.getLogger(UserCommand.class);
    private UserDAO userDao;
    private NotificDAO notificDAO;
    private HashMapDB hashMapDB;


    @Override
    public void handle(ChannelHandlerContext ctxChanel, Packet packet) {

        CmdFromUser command = validTele(packet);
        if (command == null) {
            LOG.error("Validation of Command - ERR");
            return;
        }

        User currentUser = ctxChanel.channel().attr(NettyServer.USER).get();
        if (command.cmd == gutil.COMMAND_NEW_CHAT) {
            LOG.debug("COMMAND_NEW_CHAT");
        } else if (command.cmd == gutil.STATUS_ACTIVE) {
            // Юзер активен
            currentUser.setStatus(gutil.STATUS_ACTIVE);
            LOG.debug("STATUS_ACTIVE:");
            userDao.saveOrUpdate(currentUser);
        } else if (command.cmd == gutil.STATUS_PAUSE) {
            // Юзер в паузе  // НЕ используется
            currentUser.setStatus(gutil.STATUS_PAUSE);
            LOG.debug("STATUS_PAUSE:");
            userDao.saveOrUpdate(currentUser);
        } else if (command.cmd == gutil.STATUS_HIDE) {
            // Юзер желает скрыться с карты
            currentUser.setStatus(gutil.STATUS_HIDE);
            LOG.debug("STATUS_HIDE:");
            userDao.saveOrUpdate(currentUser);
        } else if (command.cmd == gutil.MSG_DELIVERED) {
            //Сообщени идентифицируется по ИД автора и ИД сообщения в системе автора (т.е. RowId)
            LOG.debug("MSG_DELIVERED id=" + command.dat);
            long msgRowID = command.dat;        // Номер доставленного сообщения в системе автора
            long msgAutorID = command.dat2;        // ИД автора сообщения / получателя нотификейшена

            // Удалить сообщение из списка недоставленных
            // TODO Переделать. Не надо удалять, надо помечать как доставленное
            User user = ctxChanel.channel().attr(NettyServer.USER).get();
            user.removeMesagaById(msgAutorID, msgRowID);
            sendOrSaveMsgStatus(msgAutorID, msgRowID, gutil.MSG_DELIVERED );
        } else if (command.cmd == gutil.MSG_READED) {
            LOG.debug("readed id=" + command.dat);
            long msgRowID = command.dat;        // Номер доставленного сообщения в системе автора
            long msgAutorID = command.dat2;        // ИД автора сообщения / получателя нотификейшена
            sendOrSaveMsgStatus(msgAutorID, msgRowID, gutil.MSG_READED );
        }else if (command.cmd == gutil.NOTIF_DELIVERED) {
            /*
            любое уведомление можно идентифицировать по Сообщению (автор + RowId) и Статусу
            автор - получатель уведомления (он же отправитель этого подтверждения), т.е. ссылка на него есть в текушем канале
             */
            LOG.debug("NOTIF_DELIVERED ");
            long notifRowID = command.dat;        // ИД строки в системе автора сообщения
            long notifStatus = command.dat2;     // Статус

            // Удалить уведомление из списка недоставленных
            ctxChanel.channel().attr(NettyServer.USER).get().removeNotificById(notifRowID, notifStatus);

        }  else {
            LOG.debug("Command not identyfired =" + command.cmd);
        }


    }

    private void sendOrSaveMsgStatus(long recipientId, long msgRowID, int status ){

        // Отправить или сохранить Нотификейшн автору
        // Поиск Юзера получателя Нотификейшена - автора сообщения
        User recipientUser = hashMapDB.getUser(recipientId);        // Возможно НУЛЛ
        // Создаем оповещение
        Notific notifORM = new Notific();
        notifORM.setLocalRowId(msgRowID);
        notifORM.setTime(System.currentTimeMillis());                    // походу не нужно оно тут
        notifORM.setUserRecipient(recipientUser);
        notifORM.setStatus(status);                        // ставим статус оповещению НА СЕРВЕРЕ

        if (recipientUser != null && recipientUser.getMapChanel() != null && recipientUser.getMapChanel().isActive()) {
            // Сохраняем в Юзере
            recipientUser.getMapChanel().attr(NettyServer.USER).get().getUnRecivedNotif().add(notifORM);
            // Перегоняем оповещение в нужный формат и отправляем автору сообщения
            ServerStat serverStat = notifORM.fillNettyPack((ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT));
            recipientUser.getMapChanel().writeAndFlush(serverStat);        // Отправляем оповещение (БЕЗ слушателя)
        }
        // Автор сообещния не в сети
        else {
            notificDAO.saveNotific(notifORM, recipientId);
        }
    }


    @Override
    public CmdFromUser validTele(Packet packet) {
        CmdFromUser command = (CmdFromUser) packet;
        return command;

    }


    public UserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDAO userDao) {
        this.userDao = userDao;
    }

    public HashMapDB getHashMapDB() {
        return hashMapDB;
    }

    public void setHashMapDB(HashMapDB hashMapDB) {
        this.hashMapDB = hashMapDB;
    }


    public NotificDAO getNotificDAO() {
        return notificDAO;
    }

    public void setNotificDAO(NotificDAO notificDAO) {
        this.notificDAO = notificDAO;
    }


//	class SendNotifFutureListener implements ChannelFutureListener {
//		Notific notifORM;
//		SendNotifFutureListener(Notific _notifORM){
//			notifORM = _notifORM;
//		}
//		@Override
//		public void operationComplete(ChannelFuture future) throws Exception {
//			System.out.println("Notif operationComplete");
//			// Если Нотифик удалось отправить и он присестивный, удаляем его из МУСКЛ
//			if (future.isSuccess() && notifORM.getId()!=null ){
//				System.out.println("REMOVE Notif");
//				User curentUser = future.channel().attr(NettyServer.USER).get();
//				curentUser.removeNotif(notifORM);
//			}
//			// Если попытки закончены, неудачно и Нотифик не пресист, сохраняем
//			else if (future.isDone() && !future.isSuccess() && notifORM.getId()==null){
//				notificDAO.saveNotific(notifORM);
//			}
//		}
//	}


}
