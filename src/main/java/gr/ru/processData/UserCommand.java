package gr.ru.processData;

import gr.ru.HashMapDB;
import gr.ru.gutil;
import gr.ru.dao.Notific;
import gr.ru.dao.NotificDAO;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client.ServerStat;
import gr.ru.netty.protokol.Packs2Server.CmdFromUser;
import gr.ru.processData.ForwardedMsg.SendNotifFutureListener;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class UserCommand implements HandleTelegramm{
	private UserDAO userDao;
	private NotificDAO notificDAO;
	private HashMapDB hashMapDB;
	


	@Override
	public void handle(ChannelHandlerContext ctxChanel, Packet packet) {
		
		CmdFromUser command = validTele (packet) ;
		if (command==null){
			System.out.println("Validation of Command - ERR");
			return;
		}

		User currentUser = ctxChanel.channel().attr(NettyServer.USER).get();
		switch (command.cmd) {
		case gutil.COMMAND_NEW_CHAT:	
			//System.out.println("COMMAND_NEW_CHAT");
			break;
		case gutil.STATUS_ACTIVE:			// Юзер активен 
			currentUser.setStatus(gutil.STATUS_ACTIVE);
			System.out.println("STATUS_ACTIVE:");
			userDao.saveOrUpdate(currentUser);
			break;
		case gutil.STATUS_PAUSE:			// Юзер в паузе  // НЕ используется
			currentUser.setStatus(gutil.STATUS_PAUSE);
			System.out.println("STATUS_PAUSE:");
			userDao.saveOrUpdate(currentUser);
			break;
		case gutil.STATUS_HIDE:				// Юзер желает скрыться с карты
			currentUser.setStatus(gutil.STATUS_HIDE);
			System.out.println("STATUS_HIDE:");
			userDao.saveOrUpdate(currentUser);
			break;
		case gutil.MSG_DELIVERED:	
			System.out.println("MSG_DELIVERED id=" + command.dat);			
			long msgRowID = command.dat;		// Номер доставленного сообщения в системе автора
			long msgAutorID = command.dat2;		// ИД автора сообщения / получателя нотификейшена
			
			// Удалить сообщение из списка недоставленных
			User user = ctxChanel.channel().attr(NettyServer.USER).get();
			user.removeMesagaById(msgAutorID, msgRowID);			
			
			// Отправить или сохранить Нотификейшн автору				
			// Поиск Юзера получателя Нотификейшена - автора сообщения	
			User recipientUser = hashMapDB.getUser( msgAutorID );		// Возможно НУЛЛ
			// Создаем оповещение
			Notific notifORM = new Notific();					
			notifORM.setLocalRowId( msgRowID);
			notifORM.setTime(System.currentTimeMillis());					// походу не нужно оно тут
			notifORM.setUserRecipient( recipientUser );		
			notifORM.setStatus(gutil.MSG_DELIVERED);						// ставим статус оповещению НА СЕРВЕРЕ			
			
			if ( recipientUser!=null && recipientUser.getMapChanel()!=null && recipientUser.getMapChanel().isActive() ){
				// Перегоняем оповещение в нужный формат и отправляем автору сообщения
				ServerStat serverStat = notifORM.fillNettyPack( (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT) );
				// Нотификейшны отправляем с привязанным слушателем (потенциально, оповещение может не дойти, а сервер-слушатель будет уверен что дошло)
				recipientUser.getMapChanel().writeAndFlush(serverStat).addListener(new SendNotifFutureListener(notifORM ));	
			} 
			// Автор сообещния не в сети
			else {
				notificDAO.saveNotific(notifORM, msgAutorID);
			}

			
			break;	
		default:
			System.out.println("Command not identyfired ="+command.cmd);
			break;
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




	class SendNotifFutureListener implements ChannelFutureListener {
		Notific notifORM;		
		SendNotifFutureListener(Notific _notifORM){
			notifORM = _notifORM;
		}		
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			System.out.println("Notif operationComplete");
			// Если Нотифик удалось отправить и он присестивный, удаляем его из МУСКЛ
			if (future.isSuccess() && notifORM.getId()!=null ){
				System.out.println("REMOVE Notif");
				User curentUser = future.channel().attr(NettyServer.USER).get();
				curentUser.removeNotif(notifORM); 
			}
			// Если попытки закончены, неудачно и Нотифик не пресист, сохраняем
			else if (future.isDone() && !future.isSuccess() && notifORM.getId()==null){
				notificDAO.saveNotific(notifORM);
			}	
		}
	}
	
	
	
	
	
	

}
