package gr.ru;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;

import gr.ru.Network.*;

import gr.ru.dao.Mesage;
import gr.ru.dao.Notific;
import gr.ru.dao.User;

public class MapConnection extends Connection {
	/*

	//private Long unicId = 0L;
	private int session = 0;
	private Long time = 0L;
//	private User user = null;
	private short status;     
//	private byte ico;        // Иконка
//	private short qad;       // Номер таблицы (Квадрат)
//	private int lat;		// Координаты
//	private int lon;	
	private short accur;
	//private long userId = 0; 	// Номер юзера. Если точно - Номер строки в МУСКЛ
	private User user;
	public boolean bot;
	
	@Override
	public boolean isConnected () {
		if (bot)
			return true;
		return super.isConnected();
	}
	
	public void antispam (String e){
		System.out.println("antispam: " + e);
	}

	
	public void newSession() {
		this.setSession( new Random().nextInt() );		
	}
	
	public void RegNotification (long userID){
		SrvStat reg = new SrvStat();
		reg.typ = gutil.SC_REGACCEPT;
		reg.sts = session;													
		reg.dat = userID;	
		sendTCP(reg);
	}
	
	
//	// Не понятно, какой ИД сообщения хранить в МУСКЛ, какой отправлять клиенту и т.д.
//	// Отправка сообщения из МУСКЛ, отрабатывает при регистрации
//	public boolean sendMesagaFromDB(Mesage msg) {
//		MsgToClient msgToClient = new MsgToClient();
//		msgToClient.typ = gutil.TYPE_MESSAGE;
//		msgToClient.from = msg.getAutorId();
//		msgToClient.to = this.getUser().getId();	
//		msgToClient.unicId = 0;			/// ???
//		msgToClient.msg = msg.getMesaga();			
//		if (sendTCP(msgToClient) >0){
//			return true;
//		}else {
//			return false;
//		}
//	}
	
	public boolean sendMesaga(Mesage msg) {
		MsgToClient msgToClient = new MsgToClient();
		msgToClient.typ = gutil.TYPE_MESSAGE;
		msgToClient.unicId = msg.getLocalRowId();
		msgToClient.from = msg.getAutorId();
		msgToClient.to = msg.getUserRecipient().getId();
		msgToClient.msg = msg.getMesaga();
		msgToClient.time = msg.getTime();	
		if (sendTCP(msgToClient) >0){
			return true;
		}else {
			return false;
		}
	}



	public boolean sendNotification(Notific notifORM) {
		SrvStat status = new SrvStat();
		//status.typ = gutil.SC_MSGSTATUS;
		status.typ = notifORM.getStatus();
		status.sts = notifORM.getTime();							
		status.dat = notifORM.getLocalRowId();		
		if (sendTCP(status) >0){
			return true;
		}else {
			return false;
		}
	}

//	// Пересылка сообщения когда оба в онлайне
//	public boolean sendMesaga111(MsgFromClient msgTelega ) {
//		MsgToClient msgToClient = new MsgToClient();
//		msgToClient.typ = gutil.TYPE_MESSAGE;
//		msgToClient.unicId = msgTelega.rowid;
//		msgToClient.from = msgTelega.from;
//		msgToClient.to = msgTelega.to;
//		msgToClient.msg = msgTelega.msg;				
//		if (sendTCP(msgToClient) >0){
//			return true;
//		}else {
//			return false;
//		}
//	}
	
	public boolean sendUserInfo (User user){
		UserInfo userInfo = new UserInfo();
		userInfo.typ = gutil.TYPE_GETUSERINFO;
		userInfo.uid = user.getId();
		userInfo.name = user.getName();
		userInfo.lat = user.getLat();
		userInfo.lon = user.getLon();
		userInfo.stat = user.getStatus();
// TODO: Гугл может не определить локацию, тогда клиент получит НУЛ (так ли?) и андройд выдаст ошибку. 
		userInfo.country = user.getCountry();
		userInfo.local = user.getCity();
		
		if (sendTCP(userInfo) >0){
			return true;
		}else {
			return false;
		}
	}
	
//
//	// Сообщение доставлено адресату
//	public boolean Mesagadelivered(long rowid) {
//		SrvStat status = new SrvStat();
//		status.typ = gutil.SC_MSGDELIVERED;
//		status.sts = rowid;													
//		status.dat = 0;	
//		if (sendTCP(status) >0){
//			return true;
//		}else {
//			return false;
//		}
//	}
//
//	// Сообщение сохранено на сервере
//	public boolean MesagaSavedOnServer(long rowid ) {
//		SrvStat status = new SrvStat();
//		status.typ = gutil.SC_MSGRECIVED;
//		status.sts = rowid;													
//		status.dat = 0;
//		if (sendTCP(status) >0){
//			return true;
//		}else {
//			return false;
//		}
//	}	



//	public MapPoint getPoint() {
//
//		MapPoint point = new MapPoint();
//		point.uid = this.getUser().getId();
//		point.clu = false;
//		point.ico = this.ico;
//		point.lat = this.lat;
//		point.lon = this.lon;
//		point.accur = this.accur;
//		return point;
//	}


	public void SendPoints(User[]  users) {
		MapPoint[] pointsArr = new MapPoint[users.length];	
		for (int i=0; i<users.length; i++ ){
			pointsArr[i] 		= new MapPoint();
			pointsArr[i].clu 	= users[i].isCluster();
			pointsArr[i].ico 	= users[i].getIcon();
			pointsArr[i].lat 	= users[i].getLat();
			pointsArr[i].lon 	= users[i].getLon();
			pointsArr[i].uid 	= users[i].getId();	
		}
		
		
		
		PointsArr pArray = new PointsArr(); 
		pArray.typ = gutil.TYPE_MAPPOINT;
		pArray.points = pointsArr;
		sendTCP(pArray);		
	}
	
	

	@Override
	public int sendTCP(Object object) {	
		if (object instanceof TelServerClient || object instanceof FrameworkMessage){
			if (bot){
				System.out.println("(bot) sendTCP: " + object.getClass().getSimpleName());
				return 1;
			}
			else{
				return super.sendTCP(object);
			}
		}else{
			System.out.println("ERR!! Trying to sendTCP NON TelServerClient object - " + object.getClass().getName() );
			return 0;
		}

		
	}

	@Override
	public int sendUDP(Object object) {	
		if (object instanceof TelServerClient){
			if (bot){
				System.out.println("(bot) sendTCP: " + object.getClass().getSimpleName());
				return 0;
			}
			else{
				return super.sendUDP(object);
			}
		}else{
			System.out.println("ERR! Trying to sendUDP NON TelServerClient object");
			return 0;
		}
			
	}

	

	public int getSession() {
		return session;
	}

	public void setSession(int session) {
		this.session = session;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}




	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

*/

}







