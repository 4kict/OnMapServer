package gr.ru.processData;

import java.io.FileOutputStream;
import java.io.IOException;

import gr.ru.HashMapDB;
import gr.ru.MapConnection;
import gr.ru.gutil;
import gr.ru.dao.MesagaDAO;
import gr.ru.dao.Mesage;
import gr.ru.dao.Notific;
import gr.ru.dao.NotificDAO;
import gr.ru.dao.User;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client.MsgToUser;
import gr.ru.netty.protokol.Packs2Client.ServerStat;
import gr.ru.netty.protokol.Packs2Server.MsgFromUser;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class ForwardedMsg implements HandleTelegramm{
	
	private MesagaDAO mesagaDAO;
	private NotificDAO notificDAO;
	private HashMapDB hashMapDB;
    final int TYPE_PHOTO = 1;
    final int TYPE_VIDEO = 2;
	

	@Override
	public void handle(ChannelHandlerContext ctxChanel, Packet packet) {
		
		// Преобразование и проверка что данные верны
		MsgFromUser msgTelega = validTele (packet) ;		
		if (msgTelega==null || msgTelega.from!=ctxChanel.channel().attr(NettyServer.USER).get().getId() ){
			System.out.println("Validation of Mesaga - ERR!!!");
			return;
		}
		
		// Если пришел файл, надо его сохранить, а имя файла записать в текст сообщения
		if (msgTelega.msgtyp == gutil.MSG_TYP_FOTO){
			if (msgTelega.foto.length>0 ){
				String fotoPath = "foto/foto_" + System.currentTimeMillis() + ".jpg";
				System.out.println("foto.length="+msgTelega.foto.length +" fotoPath="+fotoPath);
				try {
					FileOutputStream fos = new FileOutputStream(fotoPath);
					fos.write(msgTelega.foto);
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Save file ERR!!!");
					return;
				} 
				msgTelega.msg = "[photo]"+fotoPath;
				
			}else{
				System.out.println("Wrong file size ERR!!!");
				return;
			}			
		}else{
			System.out.println("inome simple text="+msgTelega.msg);
		}
		
		
		
		// Создаем новое сообщение из входящей телеграммы
		final Mesage msgORM = new Mesage();			
		msgORM.setAutorId(msgTelega.from);
		msgORM.setLocalRowId(msgTelega.rowid);
		msgORM.setMesaga(msgTelega.msg);
		msgORM.setMsgType(msgTelega.msgtyp);
		msgORM.setTime(System.currentTimeMillis()); 
		// Поиск Юзера получателя сообщение	
		User recipientUser = hashMapDB.getUser( msgTelega.to );		// Возможно НУЛЛ, будет проверено на этапе попытки отправить ему сообщение
		msgORM.setUserRecipient(recipientUser);		
		
		// Поиск объекта User получателя сообщение	 
		//User autorUser = hashMapDB.getUser( mapConnect.getUserId()  ); // Понадобится толкьо если оповещение не дойдет, и в таком случае лучше брать из МУСКЛ
		
		// Создаем оповещение
		Notific notifORM = new Notific();					
		notifORM.setLocalRowId( msgORM.getLocalRowId() );
		notifORM.setTime(msgORM.getTime());					// Любой нотификейшн хранит время появления сообщения на сервере (надо для автора собщения)
		notifORM.setUserRecipient( ctxChanel.channel().attr(NettyServer.USER).get() );		
		notifORM.setStatus(gutil.MSG_ONSERVER);							// ставим статус оповещению НА СЕРВЕРЕ
		// Перегоняем оповещение в нужный формат и отправляем автору сообщения
		ServerStat serverStat = notifORM.fillNettyPack( (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT) );
		// Нотификейшны отправляем с привязанным слушателем (потенциально, оповещение может не дойти, а сервер-слушатель будет уверен что дошло)
		ctxChanel.writeAndFlush(serverStat).addListener(new SendNotifFutureListener(notifORM ));	

		

		//Получатель найден и вроде бы в сети
		if ( recipientUser!=null && recipientUser.getMapChanel()!=null && recipientUser.getMapChanel().isActive() ){

			recipientUser.getUnRecivedMsg().add( msgORM );		// Сохраняем сообщение в получателе			
			// Создаем и тут же заполняем новый пакет
			MsgToUser msgNetty = msgORM.fillNettyPack( (MsgToUser) PacketFactory.produce(PacketFactory.MSG_TO_USER) );
			recipientUser.getMapChanel().writeAndFlush(msgNetty);		// Отправляем сообщение (БЕЗ слушателя)
			
			// отправляем сообщение и оповещение
			//msgORM.getUserRecipient().getMapChanel().writeAndFlush(msgNetty).addListener( new SendMsgFutureListener(msgORM) );	
			//chanel.writeAndFlush(msgNetty).addListener( new SendMsgFutureListener(msgORM) );	// отправляем сообщение

			//msgORM.getUserRecipient().getMapChanel().writeAndFlush(msgNetty).addListener( new SendMsgFutureListener(msgORM) );

		}
		// Получатель НЕ в сети
		else{
			//System.out.println("recipient OFFonline "+msgTelega.msg);
			mesagaDAO.saveMesaga( msgORM, msgTelega.to  );					// сохраняем сообщение на сервере
//			notifORM.setStatus(gutil.MSG_ONSERVER);							// ставим статус оповещению
//			ServerStat serverStat = notifORM.fillNettyPack( (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT) );	
//			// Отправить оповещение // В слушателе результата отправки оповещения, кроме самого оповещения, передаем ID автора, что бы нотифик правильно сохранился в МУСКЛ в случае неудачи
//			ctxChanel.writeAndFlush(serverStat).addListener(new SendNotifFutureListener(notifORM ));
		}
		

		
		
		
		


		
//		// При хорошем раскладе хорошо бы перекинуть все сообщения между клиентами не задействовав МУСКЛ
//		if (this.forwardMsg(recipientСonnect, msgORM)){						// Удалось отправить сообщение
//			msgORM.setStatus(gutil.MSG_DELIVERED);
//			msgORM.setUserRecipient(recipientСonnect.getUser());			
//			if ( !mapConnect.Mesagadelivered(msgORM.getLocalRowId() ) ){	// оповещаем автора что сообщение дошло до получателя				
//				msgORM.setAutorInformed(false);								// Если не удалось оповестить, сохраняем				
//				mesagaDAO.saveMesaga( msgORM );		
//			}
//		}else {		// Не удалось отправить сообщение
//			msgORM.setStatus(gutil.MSG_SENDED);
//			msgORM.setAutorInformed(true);								// Предположим что оповещение дойдет до автора, что бы потом не менять
//			mesagaDAO.saveMesaga( msgORM, msgTelega.to  );				// БЛядство, но придется делать новый метод сохранения сообщения
//			if ( !mapConnect.MesagaSavedOnServer(msgORM.getLocalRowId() ) ){	// оповещаем автора что сообщение сохранено на сервере
//				msgORM.setAutorInformed(false);				
//				mesagaDAO.saveMesaga( msgORM );					
//			}
//		}
//		

		
	}

	
	
//	// Отдельный бизнес-метод отправки сообщения 
//	private boolean forwardMsg( Mesage mesaga){
//		/*
//		if (mesaga.getUserRecipient()==null){		// тут может быть НУЛЛ что нормально, значит получатель не в сети.
//			return false;
//		}
//		MapConnection recipientСonnect = mesaga.getUserRecipient().getMapChanel();
//		if (recipientСonnect!=null && recipientСonnect.isConnected() &&  recipientСonnect.getUser()!=null ){
//			if ( recipientСonnect.sendMesaga(mesaga )){				// Удалось отправить сообщение
//				return true;
//			}
//			return false;
//		}
//		*/
//		return false;
//		
//	}
//	
	
	
	@Override
	public MsgFromUser validTele(Packet packet) {
		
		MsgFromUser msgTelega = (MsgFromUser) packet;
		// TODO: обработать текст сообщения и проверить условия проверки ))
		if (msgTelega.from==0 || msgTelega.rowid==0 || msgTelega.to==0 || msgTelega.msgtyp==0 ||
				((msgTelega.msg==null || msgTelega.msg.equals("")) && (msgTelega.foto==null || msgTelega.foto.length==0)) ){
			return null;
		}
		else{
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



	public NotificDAO getNotificDAO() {
		return notificDAO;
	}



	public void setNotificDAO(NotificDAO notificDAO) {
		this.notificDAO = notificDAO;
	}









//	// Этот класс-слушатель будет выполнен после попытки отправить сообщение. 
//	// Если сообщение отправится, автор получит оповещение или оно будет сохранено
//	class SendMsgFutureListener implements ChannelFutureListener {
//		Mesage sendedMsg;
//		SendMsgFutureListener(Mesage _sendedMsg){
//			sendedMsg = _sendedMsg;
//		}
//
//		@Override
//		public void operationComplete(ChannelFuture future) throws Exception {
//
//			System.out.println("SendMsgFutureListener Complete = " + future.isSuccess() +" "+ future.isDone() + " "+ future.cause()   );
//
//
//			if ( future.isSuccess() ){
//				// Оповещаем отправителя (Автора сообщения)	
//				final Notific notifORM = new Notific();					// Создаем оповещение
//				notifORM.setLocalRowId( sendedMsg.getLocalRowId() );
//				notifORM.setTime(sendedMsg.getTime());					// Любой нотификейшн хранит время появления сообщения на сервере (надо для автора собщения)
//				notifORM.setStatus(gutil.MSG_DELIVERED);			// Статус - Сообщение доставлено
//				// Поиск получателя оповещения (Автора)	
//				User userAutor = hashMapDB.getUser(sendedMsg.getAutorId() );			// Поиск автора сообщения в онлайне
//				if (userAutor!=null && userAutor.getMapChanel()!=null && userAutor.getMapChanel().isActive() ){
//					notifORM.setUserRecipient(userAutor);											
//
//					ServerStat serverStat = notifORM.fillNettyPack( (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT) );								
//
//					userAutor.getMapChanel().writeAndFlush(serverStat).addListener(new SendNotifFutureListener(notifORM)) ;
//
//
//				}else{		
//					notificDAO.saveNotific(notifORM, sendedMsg.getAutorId()); 	// Сохраняем оповещение с указанием ИД автора, по которому ДАО найдет Юзера
//				}	
//				//mesagaDAO.delete(sendedMsg); 	// Удаляем сообщение
//			}	
//			else{
//				// Отправить не удалось, сохраняем сообщение
//				mesagaDAO.saveMesaga(sendedMsg); 
//			}
//
//		}					
//	}


	
	
	
	
	
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
	
	
	
	
//	/*
//	 * Слушатель отправки оповещений
//	 * notifORM будет содержать ссылку на Юзера-получателя, если нотифик взят из МУСКЛ.
//	 * но если нотифик сгенерирован, а Юзер-получатель с каналом взят из Хэша, то ссылки на получателя не будет и Юзера придется искать в МУСКЛ по ID   
//	 *
//	 * Другими словами, если есть ID юзера, значит нотифик есть только в памяти и его надо сохранить в МУСКЛ в случае неудачи
//	 * если ID юзера нет, то нотифик уже в МУСКЛ и его надо удалить в случае удачи.
//	 * 
//	 */
//	class SendNotifFutureListener implements ChannelFutureListener {
//		Notific notifORM;
//		long autorID;
//		SendNotifFutureListener(Notific _notifORM, long _autorID){
//			notifORM = _notifORM;
//			autorID = _autorID;
//		}
//		
//		SendNotifFutureListener(Notific _notifORM){
//			notifORM = _notifORM;
//			autorID = 0;
//		}
//
//
//
//
//		@Override
//		public void operationComplete(ChannelFuture future) throws Exception {
//			// Если попытка отправить нотификейшн завершена удачно и в нотификейшенне есть ссылка на получателя (т.е. нотиф взять из БД), то такой нотиф надо удалить из БД
//			if (future.isSuccess() && notifORM.getUserRecipient()!=null){
//				System.out.println("Delete Nitific from DB");
//				notificDAO.delete(notifORM); 
//			}
//			// Если попытка отправить нотификейшн завершена и неудачно, и этот нотифик только в оперативке, сохраняем
//			else if (future.isDone() && !future.isSuccess() && autorID>0){
//				System.out.println("Save Nitific in DB");
//				notificDAO.saveNotific(notifORM, autorID); 
//			}
//			
//		}
//		
//	}
	
	
	
	
	
	
}
