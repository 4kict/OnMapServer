package gr.ru.processData;


import gr.ru.netty.protokol.Packet;
import io.netty.channel.ChannelHandlerContext;

public interface HandleTelegramm {
    public Packet validTele(Packet packet);

    public void handle(ChannelHandlerContext ctxChanel, Packet packet);
}
