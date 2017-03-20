package gr.ru.netty;

import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Server.*;
import gr.ru.processData.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MapServerHandler extends SimpleChannelInboundHandler<Packet> {
    @Autowired
    private ForwardedMsg forwardedMsg;
    @Autowired
    private UserPositionProc userPositionProc;
    @Autowired
    private RegUser regUser;
    @Autowired
    private RequestInfo requestInfo;
    @Autowired
    private RequestList requestList;
    @Autowired
    private UserDisconnect userDisconnect;
    @Autowired
    private UserCommand userCommand;
    @Autowired
    private ForwardFile forwardFile;
    private static final Logger LOG = LogManager.getLogger(MapServerHandler.class);

    public MapServerHandler() {
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        //System.out.println("handlerAdded " + ctx.channel().id() );
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        //LOG.debug("get Packet id="+ packet.getId() );
        switch (packet.getId()) {

            case PacketFactory.REQUEST_POINT:
                LOG.trace("get REQUEST_POINT " + (RequestPoints) packet);
                requestList.handle(ctx, packet);
                break;
            case PacketFactory.REG_DATA:
                LOG.trace("get REG_DATA " + (RegData) packet);
                regUser.handle(ctx, packet);

                break;
            case PacketFactory.USER_POSITION:
                LOG.trace("get USER_POSITION " + (UserPosition) packet);
                userPositionProc.handle(ctx, packet);
                break;
            case PacketFactory.MSG_FROM_USER:
                LOG.trace("get MSG_FROM_USER " + (MsgFromUser) packet);
                forwardedMsg.handle(ctx, packet);
                break;
            case PacketFactory.CMD_FROM_USER:
                LOG.trace("get CMD_FROM_USER " + (CmdFromUser) packet);
                userCommand.handle(ctx, packet);
                break;
            case PacketFactory.REQ_USER_INFO:
                LOG.trace("get REQ_USER_INFO " + (ReqUserInfo) packet);
                requestInfo.handle(ctx, packet);
                break;
            case PacketFactory.FILE_FROM_USER:
                LOG.trace("get FILE_FROM_USER " + (FileFromUser) packet);
                forwardFile.handle(ctx, packet);
                break;
            case PacketFactory.PING:
                //LOG.trace("Ping ");
                break;

            default:
                LOG.error("Unknown packet ID=" + packet.getId());
                throw new DecoderException("channelRead unrekognized paket ID # " + packet.getId());

        }

    }


    // Тут будут перехватываться евенты-тригеры
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                LOG.info("keepAlive Disconnect #" + ctx.channel().id());
                ctx.close();        // если не было вхоящего трафика, рубим канал
            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(PacketFactory.produce(PacketFactory.PING));    // Если долно не было исходящиго трафика, посылаем Пинг
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userDisconnect.handle(ctx, null);
        ctx.close();
        LOG.trace("channel Inactive " + ctx.channel().id() + " addr=" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.trace("channel Active id=" + ctx.channel().id() + " addr=" + ctx.channel().remoteAddress());
    }

    ;


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("channel exceptionCaught " + cause);
        cause.printStackTrace();
        ctx.close();
    }

}