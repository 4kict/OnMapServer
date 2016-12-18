package gr.ru.processData;

import gr.ru.HashMapDB;
import gr.ru.dao.MesagaDAO;
import gr.ru.dao.Mesage;
import gr.ru.dao.Notific;
import gr.ru.dao.User;
import gr.ru.gutil;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client.MsgToUser;
import gr.ru.netty.protokol.Packs2Client.ServerStat;
import gr.ru.netty.protokol.Packs2Server.MsgFromUser;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForwardedMsg implements HandleTelegramm {
    private static final Logger LOG = LogManager.getLogger(ForwardedMsg.class);

    private MesagaDAO mesagaDAO;
    private HashMapDB hashMapDB;
    final int TYPE_PHOTO = 1;
    final int TYPE_VIDEO = 2;


    @Override
    public void handle(ChannelHandlerContext ctxChanel, Packet packet) {

        // Преобразование и проверка что данные верны
        MsgFromUser msgTelega = validTele(packet);
        if (msgTelega == null || msgTelega.from != ctxChanel.channel().attr(NettyServer.USER).get().getId()) {
            LOG.error("Validation of Mesaga - ERR!!!");
            return;
        }


        // Создаем оповещение для автора
        Notific notifORM = new Notific();
        notifORM.setLocalRowId(msgTelega.rowid);
        notifORM.setTime(System.currentTimeMillis());                    // Любой нотификейшн хранит время появления сообщения на сервере (надо для автора собщения)
        notifORM.setUserRecipient(ctxChanel.channel().attr(NettyServer.USER).get());
        notifORM.setStatus(gutil.MSG_ONSERVER);                            // ставим статус оповещению НА СЕРВЕРЕ
        // Сохраняем в Юзере (в авторе)
        ctxChanel.channel().attr(NettyServer.USER).get().getUnRecivedNotif().add(notifORM);
        // Перегоняем оповещение в нужный формат и отправляем автору сообщения
        ServerStat serverStat = notifORM.fillNettyPack((ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT));
        ctxChanel.writeAndFlush(serverStat);        // Отправляем оповещение (БЕЗ слушателя)


        // Создаем новое сообщение из входящей телеграммы
        final Mesage msgORM = new Mesage();
        msgORM.setAutorId(msgTelega.from);
        msgORM.setLocalRowId(msgTelega.rowid);
        msgORM.setMesaga(msgTelega.msg);
        msgORM.setMsgType(msgTelega.msgtyp);
        msgORM.setTime(notifORM.getTime());
        // Поиск Юзера получателя сообщение
        User recipientUser = hashMapDB.getUser(msgTelega.to);        // Возможно НУЛЛ, будет проверено на этапе попытки отправить ему сообщение
        msgORM.setUserRecipient(recipientUser);

        // Поиск объекта User получателя сообщение
        //User autorUser = hashMapDB.getUser( mapConnect.getUserId()  ); // Понадобится толкьо если оповещение не дойдет, и в таком случае лучше брать из МУСКЛ


//		// Нотификейшны отправляем с привязанным слушателем (потенциально, оповещение может не дойти, а сервер-слушатель будет уверен что дошло)
//		SendNotifFutureListener sendNotifFuture = new SendNotifFutureListener();
//		sendNotifFuture.setnotifORM(notifORM);
//		ctxChanel.writeAndFlush(serverStat).addListener(sendNotifFuture );


        //Получатель найден и вроде бы в сети
        if (recipientUser != null && recipientUser.getMapChanel() != null && recipientUser.getMapChanel().isActive()) {

            recipientUser.getUnRecivedMsg().add(msgORM);        // Сохраняем сообщение в получателе
            // Создаем и тут же заполняем новый пакет
            MsgToUser msgNetty = msgORM.fillNettyPack((MsgToUser) PacketFactory.produce(PacketFactory.MSG_TO_USER));
            recipientUser.getMapChanel().writeAndFlush(msgNetty);        // Отправляем сообщение (БЕЗ слушателя)

        }
        // Получатель НЕ в сети
        else {
            mesagaDAO.saveMesaga(msgORM, msgTelega.to);                    // сохраняем сообщение на сервере
        }


    }


    @Override
    public MsgFromUser validTele(Packet packet) {

        MsgFromUser msgTelega = (MsgFromUser) packet;
        // TODO: обработать текст сообщения и проверить условия проверки ))
        if (msgTelega.from == 0 || msgTelega.rowid == 0 || msgTelega.to == 0 || msgTelega.msgtyp == 0 ||
                msgTelega.msg == null || msgTelega.msg.equals("")) {
            return null;
        } else {
            return msgTelega;
        }

    }


    public MesagaDAO getMesagaDAO() {
        return mesagaDAO;
    }

    public void setMesagaDAO(MesagaDAO mesagaDAO) {
        this.mesagaDAO = mesagaDAO;
    }

    public HashMapDB getHashMapDB() {
        return hashMapDB;
    }

    public void setHashMapDB(HashMapDB hashMapDB) {
        this.hashMapDB = hashMapDB;
    }


}
