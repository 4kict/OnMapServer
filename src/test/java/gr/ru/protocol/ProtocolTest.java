package gr.ru.protocol;

import gr.ru.netty.PacketDecoder;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client;
import gr.ru.netty.protokol.Packs2Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static gr.ru.netty.protokol.PacketFactory.*;
import static gr.ru.netty.protokol.Packs2Client.*;
import static gr.ru.netty.protokol.Packs2Server.*;
import static gr.ru.netty.protokol.Packs2Server.RegData;
import static gr.ru.netty.protokol.Packs2Server.RequestPoints;
import static io.netty.buffer.Unpooled.buffer;
import static java.lang.String.format;

/**
 * Created by
 */
public class ProtocolTest {

    @Test
    public void encoderTest() {

        RegData regData = (RegData) PacketFactory.produce(REG_DATA);
        //regData.sesion = 123;
        regData.an_id = 234;       // хэш от уникального ID андройда
        regData.dev_id = 345;        // хэш от уникального ID устройства
        regData.key_id = 456;           // уникальный (случайный) ID
        regData.u_id = 567;            // Уникальный ID выданный сервером. - скорее всего ноль, иначе - юзер меняет рег данные или реконект
        regData.lat = 1111;
        regData.lon = 2222;
        regData.qad = 33;
        regData.name = "login";           // Логин
        regData.locale = "en";
        regData.status = 300;
        regData.ico = 2;             // Иконка


        ByteBuf heapBuffer = buffer(regData.getLength() + 5);
        heapBuffer.writeByte(regData.getId());          // ИДентификатор объекта
        heapBuffer.writeInt(regData.getLength());    // Длину данных
        regData.write2Buf(heapBuffer);
        byte[] bArray = heapBuffer.array();
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < bArray.length; i++) {
            message.append(format("%02x", bArray[i]));
        }
        System.out.println(message);
//        regData.write2Buf();

        RequestPoints requestPoints = (RequestPoints) PacketFactory.produce(REQUEST_POINT);
        System.out.println("RequestPoints size= " + requestPoints.getLength());

        UserPosition userPosition = (UserPosition) PacketFactory.produce(USER_POSITION);
        System.out.println("UserPosition size= " + userPosition.getLength());

        MapPoint mapPoint = (MapPoint) PacketFactory.produce(MAP_POINT);
        System.out.println("userPosition size= " + mapPoint.getLength());



    }

    @Test
    public void decoderTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PacketDecoder packetDecoder = new PacketDecoder();
        Class[] paramTypes = new Class[]{ChannelHandlerContext.class, ByteBuf.class, List.class};
        Method decode = PacketDecoder.class.getDeclaredMethod("decode", paramTypes);
        decode.setAccessible(true);
        Object[] args = new Object[]{null, null, null};
        Object result = decode.invoke(packetDecoder, args);
    }
}
