package gr.ru.netty.protokol;

import gr.ru.netty.protokol.Packs2Client.*;
import gr.ru.netty.protokol.Packs2Server.*;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

/**
 * Ключевая роль в передаче данных - это привязка класса объекта к Идентификатору.
 * Потому и создаваться объекты классадолжны по идентификатору. Следовательно, идентификаторы должны быть открыты
 */


public class PacketFactory {

    //Клиент -> Сервер
    public static final short REQUEST_POINT = 1;    // Запрос точек на карте
    public static final short REG_DATA = 2;    // Регистрационные данные
    public static final short USER_POSITION = 3;  // Периодически  данные от юзера
    public static final short MSG_FROM_USER = 4;  // Исходящее сооббщение от юзера
    public static final short CMD_FROM_USER = 5;  // Команда от юзера
    public static final short REQ_USER_INFO = 6;  // Запрос данных от юзера о другом юзере
    public static final short FILE_FROM_USER = 7;  //

    // Сервер -> Клиент
    public static final short MSG_TO_USER = 50; // Сообщение для юзера
    public static final short MAP_POINT = 51;    // Точка на карте
    public static final short POINT_ARRAY = 52;
    public static final short USER_INFO = 53;
    public static final short SERVER_STAT = 54;
    public static final short FILE_TO_USER = 55; // Сообщение для юзера

    public static final short PING = 201;


    public static Packet produce(short id) {
        switch (id) {
            case REQUEST_POINT:
                return new RequestPoints(id);
            case REG_DATA:
                return new RegData(id);
            case USER_POSITION:
                return new UserPosition(id);
            case MSG_FROM_USER:
                return new MsgFromUser(id);
            case CMD_FROM_USER:
                //int asd = Packs2Server.TYPE_FLAG;
                return new CmdFromUser(id);
            case REQ_USER_INFO:
                return new ReqUserInfo(id);
            case FILE_FROM_USER:
                return new FileFromUser(id);


            case MSG_TO_USER:
                return new MsgToUser(id);
            case MAP_POINT:
                return new MapPoint(id);
            case POINT_ARRAY:
                return new PointArray(id);
            case USER_INFO:
                return new UserInfo(id);
            case SERVER_STAT:
                return new ServerStat(id);
            case FILE_TO_USER:
                return new FileToUser(id);

            case PING:
                return new Ping(id);

            default:
                throw new DecoderException("Невозможно определить идентификатор # " + id);


        }
    }


    // Создать пакет из массива байт
    public static Packet produceFromBuf(short id, ByteBuf byteBuf) {
        Packet newPaket = produce(id);        // Сначала просто создаем пакет
        newPaket.readBuf(byteBuf);            // Потом пакет сам прочитает свои данные из буфера
        return newPaket;
    }


}
















