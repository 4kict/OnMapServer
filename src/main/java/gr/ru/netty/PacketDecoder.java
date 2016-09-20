package gr.ru.netty;




import java.util.List;

import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by gr on 21.08.2016.
 */
public class PacketDecoder extends ByteToMessageDecoder {

    // Тут мы читаем массив байт, преобразуем его в объект, пишем объект в out и далее ловим уже объект в КлиентХендлере
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 5) {   // Смотрим, возможно ли прочитать id и длинну данных
            return;
        }

        in.markReaderIndex();		// отмечаем текущую позицию маркера (readerIndex) что бы потом можно было откатиться именно сюда методом resetReaderIndex()

        final short id = in.readUnsignedByte();				// Читаем ИД класса
        final int dataLength = in.readInt();		// Читаем размер объекта
       // System.out.println("PacketDecoder: id="+ id + " len="+ dataLength);
        if (in.readableBytes() < dataLength) {     // Смотрим, можно ли прочитать всю необходимую длинну
            in.resetReaderIndex();
            return;
        }
        
        // Декодируем объект
        Packet decodedPaket = PacketFactory.produceFromBuf(id, in.readSlice(dataLength));

        out.add(decodedPaket);       // пишем в выходной объект


    }
}



