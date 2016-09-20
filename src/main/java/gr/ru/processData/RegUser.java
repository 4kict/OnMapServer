package gr.ru.processData;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import gr.ru.ApplicationContextUtils;
import gr.ru.GeoDecoder;
import gr.ru.HashMapDB; 
import gr.ru.gutil;
import gr.ru.dao.MesagaDAO;
import gr.ru.dao.Mesage;
import gr.ru.dao.Notific;
import gr.ru.dao.NotificDAO;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client.MsgToUser;
import gr.ru.netty.protokol.Packs2Client.ServerStat;
import gr.ru.netty.protokol.Packs2Server.RegData;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class RegUser implements HandleTelegramm {

	private UserDAO userDao;
	private NotificDAO notificDAO;
	private HashMapDB hashMapDB;
	//private GeoDecoder geoDecoder;
	private MesagaDAO mesagaDAO;

	

	@Override
	public void handle(final ChannelHandlerContext ctxChanel, Packet packet) {

		// Преобразование и проверка что данные действительно регистрационные
		RegData regData = validTele (packet) ;
		if (regData==null ){
			System.out.println("Validation of RegData - ERR");
			return;
		}
		
		
		/* !!!ATTENTION!!! 
		 * Надо крайне осторожно сбрасывать таблицу юзеров на сервере (сохранять сквозную нумерацию), т.к. пока нет механизма оповещения о сбросе уникальных ИД
		 * Проблемма: если у юзера в МУСЛК сохранены аппоненты со старыми ИД, а МУСКЛ на сервере сброшен и юзерам будут присвоены новые ИД, очевидно что сообщения пойдут не по адресу  
		 * ХОрошо бы заставить всех юзеров сбросить свои базы аппонентов, но пока не понятно как заставить это сделать тех кто в оффлайне. 
		 * Хорошо бы сохранять оповещения, но что бы их раздавать, надо отличать: - регистрация, - переконнект, - обновление рег данных, - юзер с данными от сброшенной МУСКЛ. 
		 *   
		 */
		
		
		
		User user = null;
		Long hashKey = (long) new String(""+regData.an_id+""+regData.dev_id+""+regData.key_id+""+regData.name ).hashCode();	
		user = userDao.getUserByHashKey(hashKey);		// Ищем Юзера по новому Хэшкоду в МУСКЛ
		
		
		// ********* ПРЕ -РЕГИСТРАЦИЯ ***********
		// пришили регистрационные данные без координат, при этом юзер не записывается в МУСКЛ, но должне получать все сообщения 	
		if (regData.lat == 0 && regData.lon == 0 && regData.qad == 0 && ctxChanel.channel().attr(NettyServer.USER).get()==null  &&  ctxChanel.channel().attr(NettyServer.SESSION).get()==null && regData.sesion==0){
			//System.out.println("PreReg new User "+regData.name);
			if (user!=null){
				user.setChanel(ctxChanel.channel());					// Сохраняем ссылку на Канал в Юзере
				ctxChanel.channel().attr(NettyServer.USER).set( user);	// Записываем в Коннект Юзера
				hashMapDB.add(user); 							// Сохраняем Юзера в Оперативной базе
			}else{
				System.out.println("new regData without position, or first connection.");
				return;
			}
		}

		
		// ********* РЕГИСТРАЦИЯ ***********
		// Если в конекте пока не определен ИД и Сессия, а так же в рег данных нет сессии, значит юзер регается (подключился первый раз)
		// unic_id может присутствовать в случае обновления рег.данных и реконекта
		else if (regData.lat!=0 && regData.lon!=0 && regData.qad!=0 && ctxChanel.channel().attr(NettyServer.SESSION).get()==null  && regData.sesion==0) {
			//System.out.println("Reg new User "+regData.name);
			if (user==null){								// Если не нашли
				user = new User();							// Создем
				user.setHashkey(hashKey);
				user.setName(regData.name);
				user.setLat(regData.lat);
				user.setLon(regData.lon);
				user.setQad(regData.qad);
				user.setIcon((byte) regData.ico);							
			}									
			
			user.setStatus(gutil.STATUS_ACTIVE);			// Устанавличае что Юзер активен
			user.setChanel(ctxChanel.channel());						// Сохраняем ссылку на Коннект в Юзере		
			System.out.println("REGISTRATION: ");
			userDao.saveOrUpdate(user);						// Сохраняем Юзера в МУСКЛ
			//System.out.println("Ufter  REGISTRATION= " + user);
			ctxChanel.channel().attr(NettyServer.SESSION).set(new Random().nextInt() );	 // Устанавливаем Сессию
			ctxChanel.channel().attr(NettyServer.USER).set(user);				// Записываем в Коннект Уникальный ИД
			// Оповещаем Юзера			
			ServerStat serverStat = (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT);
			serverStat.typ = gutil.SC_REGACCEPT;
			serverStat.sts = ctxChanel.channel().attr(NettyServer.SESSION).get();													
			serverStat.dat = user.getId();	
			ctxChanel.writeAndFlush(serverStat);
			
			hashMapDB.add(user); 							// Сохраняем Юзера в Оперативной базе

		}
		
 
		// ********* ОБНОВЛЕНИЕ рег.данных ***********
		// Иначе он обновляет какие-то рег. данные. Пока это может быть только логин или иконка
		// Юзер знает правильный свой ИД и сессию.
		else if (ctxChanel.channel().attr(NettyServer.USER).get()!=null && ctxChanel.channel().attr(NettyServer.USER).get().getId()==regData.u_id 
				&& ctxChanel.channel().attr(NettyServer.SESSION).get()!=null && ctxChanel.channel().attr(NettyServer.SESSION).get() == regData.sesion ) {
			//System.out.println("Update new User "+regData.name);
			if (user==null){								// Если не нашли, все Норм, можно обновлять рег.данные
				System.out.println("delete user= " + ctxChanel.channel().attr(NettyServer.USER).get());
				userDao.delete (ctxChanel.channel().attr(NettyServer.USER).get());	// Удаляем старого юзера из МУСКЛ 
				hashMapDB.removeUser(ctxChanel.channel().attr(NettyServer.USER).get().getId());	// Удаляем из оперативной БД
				user = new User();							// Создем
				user.setHashkey(hashKey);
				user.setName(regData.name);
				user.setLat(regData.lat);
				user.setLon(regData.lon);
				user.setQad(regData.qad);
				user.setIcon((byte) regData.ico);
				user.setStatus(gutil.STATUS_ACTIVE);			// Устанавличае что Юзер активен
				user.setChanel(ctxChanel.channel());					// Сохраняем ссылку на Коннект в Юзере
				System.out.println("UPDATE:");
				userDao.saveOrUpdate(user);						// Сохраняем Юзера в МУСКЛ	
				ctxChanel.channel().attr(NettyServer.SESSION).set(new Random().nextInt() );	 // Устанавливаем Сессию
				ctxChanel.channel().attr(NettyServer.USER).set(user);				// Записываем в Коннект Уникальный ИД
				// Оповещаем Юзера			
				ServerStat serverStat = (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT);
				serverStat.typ = gutil.SC_REGACCEPT;
				serverStat.sts = ctxChanel.channel().attr(NettyServer.SESSION).get();													
				serverStat.dat = user.getId();	
				ctxChanel.writeAndFlush(serverStat);
				hashMapDB.add(user); 							// Сохраняем Юзера в Оперативной базе

			}else {
				System.out.println("User try to delete himself");
				return;
			}	
			
			 
		}

		// ********* НИ регистрация, НИ обновление -> Выход!
		else {
			System.out.println(" ERR!!! Undefined RegData getId=" + ctxChanel.channel().attr(NettyServer.USER).get() + " u_id=" + regData.u_id + " getSession="+ ctxChanel.channel().attr(NettyServer.SESSION).get() + " regData.ses=" + regData.sesion);			
			return;
		} 
		
		
		// ***********************
		// Запуск ГЕОКОДЕРА
		// ***********************
		// Бин Геокодера должен быть prototype (т.е. каждый вызов - новый бин), по этому тут каждый раз создается новый бин 
		//System.out.println("GEOCODER "+user.getLastGeoDecode() +" " + user.getCountry() + " " +  regData.lat + " " +regData.lon );
		if ( (user.getLastGeoDecode() < (System.currentTimeMillis() - gutil.SETUP_GEODECODE_TIMEOUT) || 
				user.getCountry()==null || 
				user.getCity() == null ) && 
				regData.lat!=0 && 
				regData.lon!=0 ) {
			ApplicationContext appCtx = ApplicationContextUtils.getApplicationContext();		// Статический метод получения контекста
			GeoDecoder multiGeoDecoder = (GeoDecoder)appCtx.getBean ("geoDecoder");		// Создаем бин
			multiGeoDecoder.execute(user);  											// Запускаем  ГеоДекодер в отдельном  потоке			
		}				
		
		
		
		// ***********************
		// Пересылка сохраненных сообщений
		// ***********************
		if (user.getUnRecivedMsg().size() > 0){

			Set<Mesage> msgHashSet =  user.getUnRecivedMsg();	
			Iterator<Mesage> iterator = msgHashSet.iterator();
			while (iterator.hasNext()) {								// Перебираем все недополоученные сообщения.
				final Mesage msgRMO = iterator.next();	
				// Создаем и тут же заполняем новый пакет
				MsgToUser msgNetty = msgRMO.fillNettyPack( (MsgToUser) PacketFactory.produce(PacketFactory.MSG_TO_USER) );
				ChannelFuture future = ctxChanel.writeAndFlush(msgNetty);	// отправляем сообщение вновь зареганному юзеру

//				// Посылаем сохраненные сообщения с блокировкой основного потока
//				//future.awaitUninterruptibly();
//				future.syncUninterruptibly();
//				if ( future.isSuccess() ){
//					iterator.remove();
//				}
				
//				// Оповещаем отправителя (Автора сообщения)	
//				final Notific notifORM = new Notific();					// Создаем оповещение
//				notifORM.setLocalRowId( msgRMO.getLocalRowId() );
//				notifORM.setTime(msgRMO.getTime());					// Любой нотификейшн хранит время появления сообщения на сервере (надо для автора собщения)
//				notifORM.setStatus(gutil.MSG_DELIVERED);			// Статус - Сообщение доставлено
//				// Поиск получателя оповещения (Автора)	
//				User userMsgAutor = hashMapDB.getUser(msgRMO.getAutorId() );			// Поиск автора сообщения в онлайне
//				//TODO: .isActive() - не на 100% обещает что юзер в сети
//				if (userMsgAutor!=null && userMsgAutor.getMapChanel()!=null && userMsgAutor.getMapChanel().isActive() ){
//					notifORM.setUserRecipient(userMsgAutor);										
//					
//					ServerStat serverStat = notifORM.fillNettyPack( (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT) );								
//					
//					userMsgAutor.getMapChanel().writeAndFlush(serverStat).addListener(new SendNotifFutureListener(notifORM)) ;
//					
//					
//				}else{		// Если получателя не нашили в Хэше, передаем его ИД в DAO, там найдут его в МУСКЛе 	
//					notificDAO.saveNotific(notifORM, msgRMO.getAutorId()); 	// Сохраняем оповещение с указанием ИД автора, по которому ДАО найдет Юзера
//				}
				
				// Если в будущем удастся сообщение отправить, надо его удалить и сгенерить нотификейшн
				//future.addListener( new SendMsgFutureListener(msgRMO) );

//			     future.addListener(new ChannelFutureListener() {
//			         public void operationComplete(ChannelFuture future) {
//			        	 if ( future.isSuccess() ){
//			        		 userFin.getUnRecivedMsg().remove(msgRMO);
//			        		 //msgRMO .remove();
//			        		 userDao.saveOrUpdate(userFin); 
//			        	 }
//			         }
//			     });
				
/*
				if (chanel.sendMesaga(msgRMO)){						// Отправляем. Если сообщение удалось отправить
					// Оповещаем отправителя (Автора сообщения)	
					Notific notifORM = new Notific();					// Создаем оповещение
					notifORM.setLocalRowId( msgRMO.getLocalRowId() );
					notifORM.setTime(msgRMO.getTime());					// Любой нотификейшн хранит время появления сообщения на сервере (надо для автора собщения)
					notifORM.setStatus(gutil.MSG_DELIVERED);			// Статус - Сообщение доставлено
					// Поиск получателя оповещения (Автора)	
					User userAutor = hashMapDB.getUser(msgRMO.getAutorId() );			// Поиск автора сообщения в онлайне
					if (userAutor!=null && userAutor.getMapChanel()!=null ){
						notifORM.setUserRecipient(userAutor);							// Похоже это бессмысленная операция
						userAutor.getMapChanel().sendNotification(notifORM);			// Отправляем нотифик
					}else{		
						notificDAO.saveNotific(notifORM, msgRMO.getAutorId()); 			// Сохраняем оповещение с указанием ИД автора, по которому ДАО найдет Юзера
					}	
					iterator.remove();		// Удаляем сообщение
					
				}	
				*/			
			}
			
		}
		
		
		
		
		
		// ***********************
		// Пересылка сохраненных уведомлений
		// ***********************

		if (user.getUnRecivedNotif().size() > 0){
			Set<Notific> notifHashSet =  user.getUnRecivedNotif();	
			Iterator<Notific> iterator = notifHashSet.iterator();
			while (iterator.hasNext()) {								// Перебираем все недополоученные уведомления.
				Notific notifORM = iterator.next();	
				// Создаем оповещение в формате Netty
				ServerStat serverStat = notifORM.fillNettyPack( (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT) );				
				// Отправляем оповещение прикрепив слушателя
				ctxChanel.writeAndFlush(serverStat).addListener(new SendNotifFutureListener(notifORM));
							
			}
			System.out.println("RESEND NOTIF:");
			userDao.saveOrUpdate(user); 
		}
		
		
		

//		if (user.getUnRecivedMsg().size() > 0){
//			List<Mesage> listMsg = user.getUnRecivedMsg();			
//			for (int i=0; listMsg.size()>i; i++ ){					// Перебираем все недополоученные сообщения.
//				Mesage mesaga = listMsg.get(i);
//				if (mapConnect.sendMesagaFromDB(mesaga)){	// Отправляем. Если сообщение удалось отправить
//					// оповещаем отправителя					
//					// Поиск коннекта получателя оповещентя	
//					MapConnection recipientСonnect = hashMapDB.getMapConnect( mesaga.getAutorId() );
//					// Коннект найден и онлайн?
//					if (recipientСonnect!=null && recipientСonnect.isConnected() && recipientСonnect.getUser()!=null && recipientСonnect.getUser().getId()!=0 ){		
//						recipientСonnect.sendMesaga(msgTelega, msgCounter);					// Отправляем ему сообщение
//						mapConnect.Mesagadelivered(msgTelega.rowid, msgCounter);		// оповещаем отправителя
//					} else{			
//						mesagaDAO.saveMesaga( msgTelega, msgCounter);					// Иначе сохраняем сообщение
//						mapConnect.MesagaSavedOnServer(msgTelega.rowid, msgCounter);	// оповещаем отправителя
//					}
//					
//					
//					
//					listMsg.set(i, null);							// Обнуляем отправленное сообщение
//				}
//			}				
//			userDao.saveOrUpdate(user);
//		}
		
		

	}



	@Override
	public RegData validTele(Packet packet) {		
		RegData regData = (RegData) packet;		
		if (regData.an_id == 0 || regData.dev_id == 0 || regData.key_id == 0 || regData.status == 0 || regData.ico == 0 || regData.name.equals("")) {			
			return null;
		}
		return regData;
	}



	
	/**
	 * @return the userDao
	 */
	public UserDAO getUserDao() {
		return userDao;
	}

	/**
	 * @param userDao
	 *            the userDao to set
	 */
	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}



	public NotificDAO getNotificDAO() {
		return notificDAO;
	}



	public void setNotificDAO(NotificDAO notificDAO) {
		this.notificDAO = notificDAO;
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
//				}else{		
//					notificDAO.saveNotific(notifORM, sendedMsg.getAutorId()); 	// Сохраняем оповещение с указанием ИД автора, по которому ДАО найдет Юзера
//				}	
//				
//				mesagaDAO.delete(sendedMsg); 	// Удаляем сообщение
//			}	
//			
//		}					
//	}
	
	
	
 
	/*
	 * Слушатель отправки оповещений
	 * 
	 */
	class SendNotifFutureListener implements ChannelFutureListener {
		Notific notifORM;		
		SendNotifFutureListener(Notific _notifORM){
			notifORM = _notifORM;
		}		
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			System.out.println("Notif operationComplete");
			// Если Нотифик удалось отправить и он присестивный, удаляем его из Юзера
			if (future.isSuccess() && notifORM.getId()!=null ){
				System.out.println("REMOVE Notif");
				User curentUser = future.channel().attr(NettyServer.USER).get();
				curentUser.removeNotif(notifORM); 
				//notificDAO.delete(notifORM);				
			}
			// Если попытки закончены, неудачно и Нотифик не пресист, сохраняем
			else if (future.isDone() && !future.isSuccess() && notifORM.getId()==null){
				notificDAO.saveNotific(notifORM);
			}			
		}		
	}


}
