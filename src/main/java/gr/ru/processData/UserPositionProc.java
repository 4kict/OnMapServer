package gr.ru.processData;

import gr.ru.gutil;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.Packs2Server.UserPosition;
import io.netty.channel.ChannelHandlerContext;

public class UserPositionProc implements HandleTelegramm {

	private UserDAO userDao;
	

	@Override
	public void handle(ChannelHandlerContext ctxChanel, Packet packet) {
		//System.out.println("UserPositionProc handle");
		// Преобразование и проверка 
		UserPosition userPos = validTele (packet) ;
		User user = ctxChanel.channel().attr(NettyServer.USER).get();
		if (userPos==null || user==null) {
			System.out.println("Validation of UserPos - ERR");
			return;
		}
		
		//System.out.println("UserPositionProc validTele - OK");
		
		user.setLat(userPos.lat);
		user.setLon(userPos.lon);
		user.setQad(userPos.qad);

		// Если юзер давно не сохранялся в МУСЛ, сохраняем
		if (  user.getLastPresist() < System.currentTimeMillis()-gutil.SETUP_PRESIST_TIMEOUT  ){
			//System.out.println("PRESIST User: "+user);
			userDao.saveOrUpdate(user);			
		}


	}

	@Override
	public UserPosition validTele(Packet packet) {

		UserPosition userPos = (UserPosition) packet;
		if (userPos.lat==0 || userPos.lon==0 || userPos.qad==0 ){
			return null;
		}else{
			return userPos;
		}
		
	}

	public UserDAO getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}
	
	
	

}
