package gr.ru.netty;

import gr.ru.netty.protokol.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by gr on 21.08.2016.
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private static final Logger LOG = LogManager.getLogger(PacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
    	LOG.trace("encode id=" + packet.getId() + " len=" + packet.getLength()  );
        // Пишем в выходной буфер
        out.writeByte(packet.getId());          // ИДентификатор объекта
        out.writeInt(packet.getLength() );    // Длину данных
        packet.write2Buf(out);                  // Тело объекта

    }
}
