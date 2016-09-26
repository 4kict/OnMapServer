package gr.ru.netty.protokol;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public final  class Packs2Server {


	// *************************************
	
	public static class CmdFromUser  extends Packet  {
		public int sesion = 0;
		public long dat; // Данные команды
		public long dat2; // Данные команды 2
		public int cmd; // О какой команде речь

		CmdFromUser(short id) {		
			super(id);
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			this.sesion = buffer.readInt();	
			this.dat = buffer.readLong();
			this.dat2 = buffer.readLong();
			this.cmd = buffer.readInt();		
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeInt(sesion);
			buffer.writeLong(dat);
			buffer.writeLong(dat2);
			buffer.writeInt(cmd);

		}

		@Override
		public int getLength() {
			return (Integer.SIZE  
					+ Long.SIZE 
					+ Long.SIZE 
					+ Integer.SIZE  ) / Byte.SIZE;
		}

		@Override
		public String toString() {
			return "CmdFromUser [cmd=" + cmd + ", dat=" + dat +  ", dat2=" + dat2 + ", getLength()=" + getLength() + ", getId()=" + getId()
			+ "]";
		}

	}


	// *************************************
	
	public static  class RequestPoints extends Packet {
		public int sesion = 0;
		public int nx;
		public int ny;
		public int sx;
		public int sy;
		public short zoom;

		protected RequestPoints(short id) {
			super(id);
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			this.sesion = buffer.readInt();	
			this.nx = buffer.readInt();
			this.ny = buffer.readInt();
			this.sx = buffer.readInt();
			this.sy = buffer.readInt();
			this.zoom = buffer.readUnsignedByte();
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeInt(sesion);
			buffer.writeInt(nx);
			buffer.writeInt(ny);
			buffer.writeInt(sx);
			buffer.writeInt(sy);
			buffer.writeByte (zoom);
		}

		@Override
		public int getLength() {
			return (Integer.SIZE + Integer.SIZE + Integer.SIZE + Integer.SIZE + Integer.SIZE + Byte.SIZE ) / Byte.SIZE;
		}

		@Override
		public String toString() {
			return "RequestPoints [nx=" + nx + ", ny=" + ny + ", sx=" + sx + ", sy=" + sy + ", zoom=" + zoom
					+ ", getLength()=" + getLength() + ", getId()=" + getId() + "]";
		}

	}

	

	// *************************************

	public static class RegData extends Packet  {
		public int sesion = 0;
		public String name =""; // Логин
		public long u_id; // Уникальный ID выданный сервером. - скорее всего ноль, иначе - юзер меняет рег данные
		public int an_id; // хэш от уникального ID андройда
		public int dev_id; // хэш от уникального ID устройства
		public int key_id; // уникальный (случайный) ID
		public int lat;     // Координаты
		public int lon;
		//public int ses; // Сессия - скорее всего ноль, иначе - юзер меняет рег данные
		public short status;
		public short ico; // �?конка
		public short qad; // Номер квадрата
		public short accur;

		RegData(short id) {
			super(id);
		}
		@Override
		public void readBuf(ByteBuf buffer) {
			this.sesion = buffer.readInt();	
			short nameLen = buffer.readUnsignedByte();      // длина имени
			byte[] byteArr = new byte[nameLen];             // готовим массив байт
			buffer.readBytes(byteArr);                      // читаем байты
			this.name = new String(byteArr,   Charset.forName("UTF-8")); // Перегоняем массив байт в строку
			this.u_id = buffer.readLong();
			this.an_id = buffer.readInt();
			this.dev_id = buffer.readInt();
			this.key_id = buffer.readInt();
			this.lat = buffer.readInt();
			this.lon = buffer.readInt();
			this.status = buffer.readShort();
			this.qad = buffer.readShort();
			this.accur = buffer.readShort();
			this.ico = buffer.readUnsignedByte();
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeInt(sesion);
			byte[] byteArr = name.getBytes(Charset.forName("UTF-8"));
			buffer.writeByte(byteArr.length)  ;
			buffer.writeBytes(byteArr);
			buffer.writeLong(u_id);
			buffer.writeInt(an_id);
			buffer.writeInt(dev_id);
			buffer.writeInt(key_id);
			buffer.writeInt(lat);
			buffer.writeInt(lon);
			buffer.writeShort(status);
			buffer.writeShort(qad);
			buffer.writeShort(accur);
			buffer.writeByte(ico);
		}

		@Override
		public int getLength() {
			int lenName = (name==null)? 0 : name.getBytes(Charset.forName("UTF-8")).length;
			return (Integer.SIZE   
					+ Byte.SIZE 
					+ (Byte.SIZE*lenName) 
					+ Long.SIZE 
					+ (Integer.SIZE*5) 
					+ (Short.SIZE*3) 
					+ Byte.SIZE )/ Byte.SIZE;
		}
		@Override
		public String toString() {
			return "RegData [name=" + name + ", u_id=" + u_id + ", an_id=" + an_id + ", dev_id=" + dev_id + ", key_id="
					+ key_id + ", lat=" + lat + ", lon=" + lon + ", status=" + status + ", ico=" + ico + ", qad=" + qad
					+ ", accur=" + accur + ", getLength()=" + getLength() + ", getId()=" + getId() + "]";
		}
	}



	// *************************************

	public static class UserPosition extends Packet  {
		public int sesion = 0;
		public int lat;
		public int lon;
		public short qad;
		public short accur;

		UserPosition(short id) {
			super(id);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			this.sesion = buffer.readInt();	
			this.lat = buffer.readInt();
			this.lon = buffer.readInt();
			this.qad = buffer.readUnsignedByte();
			this.accur = buffer.readUnsignedByte();

		}
		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeInt(sesion);
			buffer.writeInt(lat);
			buffer.writeInt(lon);	
			buffer.writeByte(qad);
			buffer.writeByte(accur);

		}
		@Override
		public int getLength() {
			return (Integer.SIZE  + Integer.SIZE + Integer.SIZE + Byte.SIZE + Byte.SIZE ) / Byte.SIZE;
		}

		@Override
		public String toString() {
			return "UserPosition [lat=" + lat + ", lon=" + lon + ", qad=" + qad + ", accur=" + accur + ", getLength()="
					+ getLength() + ", getId()=" + getId() + "]";
		}

	}



	// *************************************

	public static class MsgFromUser extends Packet  {
		public int sesion = 0;
		//public byte[] foto;
		public String msg = "";    
		public long rowid;
		public long from;
		public long to;
		public short msgtyp;

		MsgFromUser(short id) {
			super(id);
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			this.sesion = buffer.readInt();	
//			// Фото
//			int fotoLen = buffer.readInt();      // длина массива байт фотографии
//			if (fotoLen > 0){								// если фото есть
//				foto = new byte[fotoLen]; 		             // готовим массив байт
//				buffer.readBytes(foto);                      // читаем байты
//			}
			// Сообщение
			int msgLen = buffer.readUnsignedShort();      // длина сообщения
			if (msgLen>0){									// если сообщение есть
				byte[] byteArr = new byte[msgLen];             // готовим массив байт
				buffer.readBytes(byteArr);                      // читаем байты
				this.msg = new String(byteArr,   Charset.forName("UTF-8")); // Перегоняем массив байт в строку
			}

			this.rowid = buffer.readLong();
			this.from = buffer.readLong();
			this.to = buffer.readLong();
			this.msgtyp = buffer.readShort();
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeInt(sesion);
//			// Фото
//			if (foto!=null && foto.length>0){
//				buffer.writeInt(foto.length);
//				buffer.writeBytes(foto);
//			}else{
//				buffer.writeInt(0);
//			}

			// Текст
			byte[] byteArr = msg.getBytes(Charset.forName("UTF-8"));	// Переводим сообщение в массив байт
			buffer.writeShort(byteArr.length)  ;							// ЗАписываем кол-во байт
			if (byteArr.length > 0 )				// Пишем сообщение только если оно есть
				buffer.writeBytes(byteArr);
			
			buffer.writeLong(rowid);
			buffer.writeLong(from);
			buffer.writeLong(to);
			buffer.writeShort(msgtyp);
		}

		@Override
		public int getLength() {
			//int fotolen = (foto!=null)? foto.length: 0;
			int lenMsg = (msg==null)? 0 : msg.getBytes(Charset.forName("UTF-8")).length;			
			return (Integer.SIZE  
//					+ Integer.SIZE
//					+ (Byte.SIZE*fotolen)
					+ Short.SIZE
					+ (Byte.SIZE*lenMsg) 
					+ Long.SIZE 
					+ Long.SIZE
					+ Long.SIZE
					+ Short.SIZE)/ Byte.SIZE;
		}

		@Override
		public String toString() {
			return "MsgFromUser [msg=" + msg + ", rowid=" + rowid + ", from=" + from
					+ ", to=" + to + ", msgtyp=" + msgtyp + ", getLength()=" + getLength() + ", getId()=" + getId() + "]";
		}
	}





	// *************************************

	public static class FileFromUser extends Packet  {
		public int sesion = 0;
		public byte[] foto;
		public long rowid;			// ИД сообщения на стороне автора. Будет одинаковым для всех кусочков одного файла
		public long from;			// ИД автора
		public long to;				// ИД получателя

		public short pieceId;		// Идентификатор этого кусочка
		public short piecesCount;	// Общее количество кусочков

		FileFromUser(short id) {
			super(id);
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			this.sesion = buffer.readInt();
			// Фото
			int fotoLen = buffer.readInt();      // длина массива байт фотографии
			if (fotoLen > 0){								// если фото есть
				foto = new byte[fotoLen]; 		             // готовим массив байт
				buffer.readBytes(foto);                      // читаем байты
			}


			this.rowid = buffer.readLong();
			this.from = buffer.readLong();
			this.to = buffer.readLong();
			this.pieceId = buffer.readShort();
			this.piecesCount = buffer.readShort();
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeInt(sesion);
			// Фото
			if (foto!=null && foto.length>0){
				buffer.writeInt(foto.length);
				buffer.writeBytes(foto);
			}else{
				buffer.writeInt(0);
			}

			buffer.writeLong(rowid);
			buffer.writeLong(from);
			buffer.writeLong(to);
			buffer.writeShort(pieceId);
			buffer.writeShort(piecesCount);
		}

		@Override
		public int getLength() {
			int fotolen = (foto!=null)? foto.length: 0;
			return (Integer.SIZE
					+ Integer.SIZE
					+ (Byte.SIZE*fotolen)
					+ Long.SIZE
					+ Long.SIZE
					+ Long.SIZE
					+ Short.SIZE
					+ Short.SIZE)/ Byte.SIZE;
		}


		@Override
		public String toString() {
			int fotolen = (foto!=null)? foto.length: 0;
			return "FileFromUser{" +
					"sesion=" + sesion +
					", foto=" + fotolen +
					", rowid=" + rowid +
					", from=" + from +
					", to=" + to +
					", pieceId=" + pieceId +
					", piecesCount=" + piecesCount +
					", getLength()=" + getLength() +
					", getId()=" + getId() +
					'}';
		}
	}





	// *************************************

	public static class ReqUserInfo  extends Packet  {
		public int sesion = 0;
		public long uid;
		public short qad;

		ReqUserInfo(short id) {
			super(id);
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			this.sesion = buffer.readInt();	
			this.uid = buffer.readLong();
			this.qad = buffer.readShort();	
		}
		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeInt(sesion);
			buffer.writeLong(uid);
			buffer.writeShort(qad);
		}

		@Override
		public int getLength() {
			return (Integer.SIZE  + Long.SIZE + Short.SIZE  ) / Byte.SIZE;
		}

		@Override
		public String toString() {
			return "ReqUserInfo [uid=" + uid + ", qad=" + qad + ", getLength()=" + getLength() + ", getId()=" + getId()
			+ "]";
		}

	}




}
