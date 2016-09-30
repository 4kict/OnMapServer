package gr.ru;

/**
 *
 */
public final class gutil {


	public static final String PARAM_TYPE = "ge.griMap.ServiceActivity.type";
	public static final String PARAM_DATA = "ge.griMap.ServiceActivity.data";
	public static final String BROADCAST_ACTION = "ge.griMap.servicebackbroadcast";

	public static final int SETUP_SIZEOF_PICE 		= 100; // Максимальный размер кусочка файла, в байтах

	// Типы данных которыми могут обмениваться Сервис и Активити
	public static final int TYPE_FLAG 				= 2; // Тип Флаг
	public static final int TYPE_DATATOCLIENT 		= 4; // Тип Данные клиенту
	public static final int TYPE_USERCHARACTER 		= 6; // Тип Данные клиенту
	public static final int TYPE_DATA 				= 98; // Тип Данные клиенту
	
	// Типы данных которыми могут обмениваться Клиент и Сервер
	public static final int TYPE_REGISTRATION 		= 601; // Тип Флаг
	public static final int TYPE_REQUEST 			= 602; // 
	public static final int TYPE_POSITION 			= 603; // 
	public static final int TYPE_COMMAND 			= 604; // 
	public static final int TYPE_MAPPOINT			= 605; // 
	public static final int TYPE_USERINFO 			= 606; //  
    public static final int TYPE_MESSAGE 			= 609; //
    public static final int TYPE_FOTO    			= 610; //
    public static final int TYPE_REQUSERINFO		= 611; //
    public static final int TYPE_GETUSERINFO		= 613; //
    
    
	public static final int SC_REGACCEPT 			= 615; // Статус. Применяется только как подтверждение регистрации, совместно с SERVER_REG_ACCEPT. - ЧУШЬ надо избавиться от SERVER_REG_ACCEPT  
//	public static final int SC_MSGRECIVED 			= 617; // Соощение доставлено и ему присвоен номер
//	public static final int SC_MSGDELIVERED 			= 619; // Соощение доставлено и ему присвоен номер
	public static final int SC_MSGSTATUS			= 621; // Статус сообщения
	
	// Варианты комманд от клиента серверу
    public static final int COMMAND_NEW_CHAT		= 701; //


	
    // Варианты состояний сообщений
    public static final int MSG_SENDED      		= 900; //
    public static final int MSG_ONSERVER     		= 902; // Сохранено на сервере
    public static final int MSG_DELIVERED      		= 904; // ДОставлено получателю
    public static final int MSG_READED      		= 906; // Прочитано получателем
	public static final int NOTIF_DELIVERED    		= 915;


    // Типы сообщений
    public static final int MSG_TYP_TXT     		= 920; // текстовое сообщение
    public static final int MSG_TYP_FOTO      		= 922; // фотка	
	    

	// Флаги которые могут придти от Сервика в Активити
	public static final int FLAG_SERVEROFF 			= 100; //
	public static final int FLAG_CONNECTED 			= 102; //
	public static final int FLAG_DISCONNETCED 		= 104; //
	public static final int FLAG_REGACCEPT 			= 106; //
	public static final int FLAG_CONNETCED_IMPOSSIBLE = 110; //
	// Флаги которые могут придти от Активити в Сервис
	public static final int FLAG_ACTIVITYRESUME 	= 200; //
	public static final int FLAG_ACTIVITYPAUSE 		= 202; //
	// public static enum flags {Gri,Mash};

	// Статусы клиента
	public static final int STATUS_ACTIVE 			= 300;
	public static final int STATUS_PAUSE 			= 302;
	public static final int STATUS_HIDE 			= 303;
	public static final int STATUS_OFFLINE 			= 304;


	// Варианты диалоговых окон
	public static final int DIALOG_POSITION_ERR 	= 500;
	public static final int DIALOG_INTERNET_ERR 	= 502;

	// возможные статусы от сервера клиенту
//	public static final int SERVER_REG_ACCEPT 		= 800;
//	public static final int SERVER_MSG_ACCEPT 		= 802;


	public static final int SETUP_REG_TIMEOUT = 5000;
	public static final int SETUP_UPDATE_TIMEOUT = 2000;
	public static final int SETUP_REQUEST_TIMEOUT = 2000;
	public static final long SETUP_GEODECODE_TIMEOUT = 10*60*1000;		// Как часто можно запрашивать реверсивное геокодирование в Гугле для одного пользователя!!!
	public static final long SETUP_PRESIST_TIMEOUT = 1*20*1000;		// Как часто надо сохранять юзера в МУСКЛ.






	// TODO: negLetters и posLetters не нужны
	// вернет номер квадрата, всегда положительный от 100 до 6030
	// первые два разряда - lon, вторые два - lat
	public static short latLon2Qad(double latitude, double longitude){
		return (short) (getLongQad(longitude)*100+getLatQad(latitude));
	}

	private static final char[] negLetters = { 'A', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M' };
	private static final int[] negDegrees = { -90, -84, -72, -64, -56, -48, -40, -32, -24, -16, -8 };
	private static final char[] posLetters = { 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Z' };
	private static final int[] posDegrees = { 0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 84 };

	private static short getLongQad(double longitude){
		double longZone = 0.0D;
		if (longitude < 0.0D) {
			longZone = (180.0D + longitude) / 6.0D + 1.0D;
		} else {
			longZone = longitude / 6.0D + 31.0D;
		}
		return (short)longZone;
	}

	public static short getLatQad(double latitude){
		int latIndex = -2;
		int lat = (int)latitude;
		if (lat >= 0){
			int len = posLetters.length;
			for (int i = 0; i < len; i++){
				if (lat == posDegrees[i]){
					latIndex = i;
					break;
				}
				if (lat <= posDegrees[i]){
					latIndex = i - 1;
					break;
				}
			}
		}
		else{
			int len = negLetters.length;
			for (int i = 0; i < len; i++){
				if (lat == negDegrees[i])				{
					latIndex = i;
					break;
				}
				if (lat < negDegrees[i]){
					latIndex = i - 1;
					break;
				}
			}
		}
		if (latIndex == -1) {
			latIndex = 0;
		}
		
		
		if (lat >= 0) {
			if (latIndex == -2) {
				latIndex = posLetters.length - 1;
			}
			return (short)latIndex;
		}
		if (latIndex == -2) {
			latIndex = negLetters.length - 1;
		}
		return (short)(latIndex+20);
	}






}







//
//class UUIDgr
//{
// private final String hash;
// private final long least;
// private final long most;
// private final UUIDserializ uuidSer = new UUIDserializ();
//
// UUIDgr(long _l, long _m)
// {
// UUID uuid_tmp = new UUID(_m, _l);
// this.hash = uuid_tmp.toString();
// this.least = uuid_tmp.getLeastSignificantBits();
// this.most = uuid_tmp.getMostSignificantBits();
// this.uuidSer.hash = this.hash;
// this.uuidSer.least = this.least;
// this.uuidSer.most = this.most;
// uuid_tmp = null;
// }
//
// String getHash()
// {
// return this.hash;
// }
//
// long getLeast()
// {
// return this.least;
// }
//
// long getMost()
// {
// return this.most;
// }
//
// UUIDserializ getSerializ()
// {
// return this.uuidSer;
// }
//}
//
//
//
//class UUIDserializ
//{
// public String hash;
// public long least;
// public long most;
//}
