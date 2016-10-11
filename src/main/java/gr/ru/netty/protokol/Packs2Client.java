package gr.ru.netty.protokol;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public final class Packs2Client {


	// *************************************
	public static class MapPoint extends Packet {

		public long uid;
		public int lat;
		public int lon;
		public short accur;
		public byte ico;
		public boolean clu;

		MapPoint(short id) {
			super(id);
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			this.uid = buffer.readLong();
			this.lat = buffer.readInt();
			this.lon = buffer.readInt();
			this.accur = buffer.readUnsignedByte();
			this.ico = buffer.readByte();
			this.clu = buffer.readBoolean();
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeLong(uid);
			buffer.writeInt(lat);
			buffer.writeInt(lon);	
			buffer.writeByte(accur);
			buffer.writeByte(ico);
			buffer.writeBoolean(clu);    	
		}

		@Override
		public int getLength() {
			return (Long.SIZE + Integer.SIZE + Integer.SIZE + Byte.SIZE + Byte.SIZE + Byte.SIZE ) / Byte.SIZE;
		}

		@Override
		public String toString() {
			return "MapPoint [uid=" + uid + ", lat=" + lat + ", lon=" + lon + ", accur=" + accur + ", ico=" + ico + ", clu="
					+ clu + ", getLength()=" + getLength() + ", getId()=" + getId() + "]";
		}    

	}





	// *************************************

	public static class MsgToUser extends Packet {

		public byte[] foto;
		public String msg = "";
		public long unicId;
		public long from;
		public long to;
		public long time;
		public short msgtyp;

		MsgToUser(short id) {
			super(id);
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			// Фото
			int fotoLen = buffer.readInt();      // длина массива байт фотографии
			if (fotoLen > 0){								// если фото есть
				foto = new byte[fotoLen]; 		             // готовим массив байт
				buffer.readBytes(foto);                      // читаем байты
			}
			// Сообщение
			int msgLen = buffer.readUnsignedShort();      // длина сообщения
			if (msgLen>0){									// если сообщение есть
				byte[] byteArr = new byte[msgLen];             // готовим массив байт
				buffer.readBytes(byteArr);                      // читаем байты
				this.msg = new String(byteArr,   Charset.forName("UTF-8")); // Перегоняем массив байт в строку
			}

			this.unicId = buffer.readLong();
			this.from = buffer.readLong();
			this.to = buffer.readLong();
			this.time = buffer.readLong();
			this.msgtyp = buffer.readShort();
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			// Фото
			if (foto!=null && foto.length>0){
				buffer.writeInt(foto.length);
				buffer.writeBytes(foto);
			}else{
				buffer.writeInt(0);
			}

			// Текст
			byte[] byteArr = msg.getBytes(Charset.forName("UTF-8"));	// Переводим сообщение в массив байт
			buffer.writeShort(byteArr.length)  ;							// ЗАписываем кол-во байт
			if (byteArr.length > 0 )				// Пишем сообщение только если оно есть
				buffer.writeBytes(byteArr);
			buffer.writeLong(unicId);
			buffer.writeLong(from);
			buffer.writeLong(to);
			buffer.writeLong(time);
			buffer.writeShort(msgtyp);
		}

		@Override
		public int getLength() {
			int fotolen = (foto!=null)? foto.length: 0;
			int lenMsg = (msg==null)? 0 : msg.getBytes(Charset.forName("UTF-8")).length;
			return (Integer.SIZE
					+ (Byte.SIZE*fotolen)
					+ Short.SIZE
					+ (Byte.SIZE*lenMsg)
					+ Long.SIZE
					+ Long.SIZE
					+ Long.SIZE
					+ Long.SIZE
					+ Short.SIZE)/ Byte.SIZE;
		}

		@Override
		public String toString() {
			int fotolen = (foto!=null)? foto.length: 0;
			return "MsgToUser [foto=" + fotolen + ", msg=" + msg + ", unicId=" + unicId + ", from=" + from
					+ ", to=" + to + ", time=" + time + ", msgtyp=" + msgtyp + ", getLength()=" + getLength() + ", getId()="
					+ getId() + "]";
		}
	}
;


    // *************************************

    public static class FileToUser extends Packet {

        public byte[] foto;
        public long unicId; // RowId
        public long from;
        public long to;
        public long time;               // Время когда последний (??? или первый ???) кусочек упал на серер. Может и ваще не надо это.
        public short pieceId;        // Идентификатор этого кусочка
        public short piecesCount;    // Общее количество кусочков

        FileToUser(short id) {
            super(id);
        }

        @Override
        public void readBuf(ByteBuf buffer) {
            // Фото
            int fotoLen = buffer.readInt();      // длина массива байт фотографии
            if (fotoLen > 0){								// если фото есть
                foto = new byte[fotoLen]; 		             // готовим массив байт
                buffer.readBytes(foto);                      // читаем байты
            }


            this.unicId = buffer.readLong();
            this.from = buffer.readLong();
            this.to = buffer.readLong();
            this.time = buffer.readLong();
            this.pieceId = buffer.readShort();
            this.piecesCount = buffer.readShort();
        }

        @Override
        public void write2Buf(ByteBuf buffer) {
            // Фото
            if (foto!=null && foto.length>0){
                buffer.writeInt(foto.length);
                buffer.writeBytes(foto);
            }else{
                buffer.writeInt(0);
            }

            buffer.writeLong(unicId);
            buffer.writeLong(from);
            buffer.writeLong(to);
            buffer.writeLong(time);
            buffer.writeShort(pieceId);
            buffer.writeShort(piecesCount);
        }

        @Override
        public int getLength() {
            int fotolen = (foto!=null)? foto.length: 0;
            return (Integer.SIZE
                    + (Byte.SIZE*fotolen)
                    + Long.SIZE
                    + Long.SIZE
                    + Long.SIZE
                    + Long.SIZE
                    + Short.SIZE
                    + Short.SIZE)/ Byte.SIZE;
        }



        @Override
        public String toString() {
            int fotolen = (foto!=null)? foto.length: 0;
            return "FileToUser{" +
                    "foto=" + fotolen +
                    ", unicId=" + unicId +
                    ", from=" + from +
                    ", to=" + to +
                    ", time=" + time +
                    ", pieceId=" + pieceId +
                    ", piecesCount=" + piecesCount +
                    ", getLength()=" + getLength() +
                    ", getId()=" + getId() +
                    '}';
        }
    }






    // *************************************

	public static class PointArray extends Packet {

		public MapPoint[] points;

		PointArray(short id) {
			super(id);
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			int pointsLen = buffer.readUnsignedShort();
			if (pointsLen > 0){
				points = new MapPoint[pointsLen];
				for (int i=0; i<pointsLen; i++){
					points[i] = (MapPoint) PacketFactory.produceFromBuf(PacketFactory.MAP_POINT, buffer);
				}
			}
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeShort(points.length);
			for (int i=0; i<points.length; i++){
				if (points[i]!=null)
					points[i].write2Buf(buffer);
			}
		}

		@Override
		public int getLength() {
			if (points.length > 0)
				return (Short.SIZE/Byte.SIZE) + (points.length*points[0].getLength() );
			else return 0;
		}

		@Override
		public String toString() {
			return "PointArray [points=" + points.length + ", getLength()=" + getLength() + ", getId()=" + getId()
			+ "]";
		}
	}



	// *************************************
	
	public static class ServerStat  extends Packet  {
		
		public long sts; // О каком статусе речь
		public long dat; // состояние статуса
		public int typ;
		
		ServerStat(short id) {
			super(id); 
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			this.sts = buffer.readLong();
			this.dat = buffer.readLong();
			this.typ = buffer.readUnsignedShort();	
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			buffer.writeLong(sts);
			buffer.writeLong(dat);
			buffer.writeShort(typ);
		}

		@Override
		public int getLength() {
			return (Long.SIZE + Long.SIZE +Short.SIZE  ) / Byte.SIZE;
		}

		@Override
		public String toString() {
			return "ServerStat [sts=" + sts + ", dat=" + dat + ", typ=" + typ + ", getLength()=" + getLength()
					+ ", getId()=" + getId() + "]";
		}



		
	}


	
	// *************************************

	public static class UserInfo extends Packet {

		public String name = ""; // Логин
		public String country = "";
		public String local = "";
		public long uid;
		public int lat;
		public int lon;
		public int stat;

		UserInfo(short id) {
			super(id);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void readBuf(ByteBuf buffer) {
			// name
			short nameLen = buffer.readUnsignedByte();      // длина имени
			byte[] byteArr = new byte[nameLen];             // готовим массив байт
			buffer.readBytes(byteArr);                      // читаем байты
			this.name = new String(byteArr,   Charset.forName("UTF-8")); // Перегоняем массив байт в строку
			//country
			short countryLen = buffer.readUnsignedByte();      // длина 
			byteArr = new byte[countryLen];             	// готовим массив байт
			buffer.readBytes(byteArr);                      // читаем байты
			this.country = new String(byteArr,   Charset.forName("UTF-8")); // Перегоняем массив байт в строку
			// local 
			short localLen = buffer.readUnsignedByte();      // длина 
			byteArr = new byte[localLen];     	        	// готовим массив байт
			buffer.readBytes(byteArr);                      // читаем байты
			this.local = new String(byteArr,   Charset.forName("UTF-8")); // Перегоняем массив байт в строку

			this.uid = buffer.readLong();
			this.lat = buffer.readInt();
			this.lon = buffer.readInt();
			this.stat = buffer.readUnsignedShort();
		}

		@Override
		public void write2Buf(ByteBuf buffer) {
			byte[] byteArr = name.getBytes(Charset.forName("UTF-8"));
			buffer.writeByte(byteArr.length)  ;
			buffer.writeBytes(byteArr);

			byteArr = country.getBytes(Charset.forName("UTF-8"));
			buffer.writeByte(byteArr.length)  ;
			buffer.writeBytes(byteArr);

			byteArr = local.getBytes(Charset.forName("UTF-8"));
			buffer.writeByte(byteArr.length)  ;
			buffer.writeBytes(byteArr);

			buffer.writeLong(uid);
			buffer.writeInt(lat);
			buffer.writeInt(lon);
			buffer.writeShort(stat);
		}


		@Override
		public int getLength() {
			int lenName = (name==null)? 0 : name.getBytes(Charset.forName("UTF-8")).length;
			int lenCountry = (country==null)? 0 : country.getBytes(Charset.forName("UTF-8")).length;
			int lenLocal = (local==null)? 0 : local.getBytes(Charset.forName("UTF-8")).length;
			return (Byte.SIZE 
					+ (Byte.SIZE*lenName) 
					+ Byte.SIZE 
					+ (Byte.SIZE*lenCountry)
					+ Byte.SIZE
					+ (Byte.SIZE*lenLocal)
					+ Long.SIZE 
					+ Integer.SIZE 
					+ Integer.SIZE
					+ Short.SIZE )/ Byte.SIZE;
		}

		@Override
		public String toString() {
			return "UserInfo [name=" + name + ", country=" + country + ", local=" + local + ", uid=" + uid + ", lat=" + lat
					+ ", lon=" + lon + ", stat=" + stat + ", getLength()=" + getLength() + ", getId()=" + getId() + "]";
		}
	}

	

}
