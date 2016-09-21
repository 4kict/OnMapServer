package gr.ru.netty.protokol;

import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by Gri on 21.09.2016.
 */
public class ChunkHandler extends ChunkedWriteHandler {

    @Override
    public void resumeTransfer() {
        System.out.println("resumeTransfer");
        super.resumeTransfer();
    }
}
