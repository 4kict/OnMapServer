package gr.ru.netty.protokol;

import io.netty.buffer.ByteBuf;

/**
 * Это базовый абстрактный пакет,
 * который обязует все пакеты использовать ИД, Читать и Писать в буфер, а так же получить размер пакета
 *
 */
public abstract  class Packet {
    private final short id;

    Packet (short id){
        this.id = id;
    }


    public short getId() {
        return id;
    }

    public abstract void readBuf(ByteBuf buffer);
    public abstract void write2Buf(ByteBuf buffer);
    public abstract int getLength();



}
