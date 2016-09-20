package gr.ru.netty.protokol;

import io.netty.buffer.ByteBuf;

public class Ping extends Packet {

	Ping(short id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void readBuf(ByteBuf buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write2Buf(ByteBuf buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return "Ping [getLength()=" + getLength() + ", getId()=" + getId() + "]";
	}



}
