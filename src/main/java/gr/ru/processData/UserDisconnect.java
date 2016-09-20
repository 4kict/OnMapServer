package gr.ru.processData;

import gr.ru.HashMapDB;
import gr.ru.gutil;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import io.netty.channel.ChannelHandlerContext;

public class UserDisconnect implements HandleTelegramm{

	private UserDAO userDao;		// 
	private HashMapDB hashMapDB;

	@Override
	public void handle(ChannelHandlerContext ctxChanel, Packet packet) {
		User userToDelete = ctxChanel.channel().attr(NettyServer.USER).get();

		if (userToDelete!=null){	
			userToDelete.setStatus(gutil.STATUS_OFFLINE);
			
			System.out.println("userToDelete=" + userToDelete);			
			System.out.println("userFromDB=" + userDao.getUser(userToDelete.getId()) );		
			
			//System.out.println("Before Delete= " + userToDelete);
			userDao.saveOrUpdate(userToDelete);							// Если нашли, сохраняем его последние данные в МУСКЛ, предварительно сделав его ОФФЛАЙН
			//System.out.println("Ufter  Delete= " + userToDelete);
			hashMapDB.removeUser(userToDelete.getId() );					// Удаляем из Хэша
		}			

	}

	@Override
	public Packet validTele( Packet packet) {
		// TODO Auto-generated method stub
		return null;
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






}
