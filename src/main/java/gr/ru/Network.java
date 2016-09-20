
package gr.ru;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

//This class is a convenient place to keep things common to both the client and server.
public class Network {
  static public final int portTCP = 56888;
  static public final int portUDP = 56999;

  /*
  // This registers objects that are going to be sent over the network.
  static public void register(EndPoint endPoint) {
      Kryo kryo = endPoint.getKryo();
      kryo.register(TelClientServer.class);
      kryo.register(TelServerClient.class);
      kryo.register(RegData.class);
      kryo.register(SrvStat.class);
      kryo.register(Rqst.class);
      kryo.register(UserPos.class);
      kryo.register(PointsArr.class);
      kryo.register(MapPoint.class);
      kryo.register(MapPoint[].class);
      kryo.register(Comand.class);
      kryo.register(MsgFromClient.class);
      kryo.register(MsgToClient.class);
      kryo.register(ReqUserInf.class);
      kryo.register(UserInfo.class);
      kryo.register(byte.class);
      kryo.register(byte[].class);
      

  }


  //***********************************
  //	Клиент -> Сервер
  //***********************************
  
  public static class TelClientServer {
      public int ses;
      public int typ;
  }
  
  // При регистрации Юзер посылает свои уникальные номера, на их базе и имени будет сгенерирован его уникальный хэш номер - общедоступный идентификкатор .
  // В ответ на успешную регистрацию, юзер получит в ответ


  //tmDevice
  //androidId
  //tmSerial

  public static class RegData extends TelClientServer {
      public int an_id;        	// хэш от уникального ID андройда
      public int dev_id;        // хэш от уникального ID устройства
      public int key_id;        // уникальный (случайный) ID
      //public int ses;        	// Сессия - скорее всего ноль, иначе - юзер меняет рег данные
      public long u_id;        	// Уникальный ID выданный сервером. - скорее всего ноль, иначе - юзер меняет рег данные

      public String name;     // Логин

      public short status;
      public byte ico;        // �?конка
      public short qad;       // Номер квадрата
      public int lat;         // Координаты
      public int lon;
      public short accur;
      //     public String s;
      //     public String v;
      //     public String f;
      //     public String vk;
  }


  // Запрос точек на карте Клиент -> Сервер
  public static class Rqst extends TelClientServer{
      //public int ses;
      public int nx;
      public int ny;
      public int sx;
      public int sy;
      public int zom;
  }

  // Позиция юзера Клиент -> Сервер
  public static class UserPos extends TelClientServer{
      //public int ses;
      public short qad;
      public int lat;
      public int lon;
      public short accur;
  }

  public static class MsgFromClient extends TelClientServer{
      public long rowid;
      public long from;
      public long to;
      public int msgtyp;
      public String msg;
      public byte[] foto;
      
  }


  // Команда
  public static class Comand extends TelClientServer{
      public int cmd;       // О какой команде речь
      public long dat;       // Данные команды
  }


  public static class ReqUserInf extends TelClientServer{
      public short qad;
      public long uid;
  }

  // public static class GetUserInfo
  // {
  //   public long udl;
  //   public long udm;
  //   public long uudl;
  //   public long uudm;
  // }
  //


  //***********************************
  //	Сервер -> Клиент
  //***********************************

//	// Точка на карте Сервер -> Клиент
//	public static class MapPoint extends Telegram{
//		public long gid;
//		public boolean clu;
//		public int lat;
//		public int lon;
//		public byte ico;
//		public short accur;
//	}

  public static class  TelServerClient {
      public int typ;
  }
  
  public static class MsgToClient extends TelServerClient{
      public long unicId;
      public long from;
      public long to;
      public String msg;
      public long time;
  }
  
  // Точка на карте Сервер -> Клиент
  public static class MapPoint{
      public long uid;
      public boolean clu;
      public int lat;
      public int lon;
      public byte ico;
      public short accur;
  }

  public static class PointsArr extends TelServerClient{
      public MapPoint[] points;
  }


  public static class FrwrdMsg extends TelServerClient{
      public int sysid;
      public long from;
      public long to;
      public String msg;
  }


  // �?нформация о юзере Сервер -> Клиент
  public static class UserInfo extends TelServerClient{
      public long uid;
      public String name;     // Логин
      public String country;
      public String local;
      public int lat;
      public int lon;
      public int stat;
      //   public String s;
      //   public String v;
      //   public String f;
      //   public String vk;
  }

  // Статус
  public static class SrvStat extends TelServerClient {
      public long sts;       // О каком статусе речь
      public long dat;       // состояние статуса
  }
*/
}





