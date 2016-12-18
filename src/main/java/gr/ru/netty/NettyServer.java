package gr.ru.netty;


import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class NettyServer {
    private static final Logger LOG = LogManager.getLogger(NettyServer.class);
    static public final int portTCP = 56888;
    // Атрибуты которые будут у канала
    public final static AttributeKey<Integer> SESSION = AttributeKey.valueOf("unic_session");
    public final static AttributeKey<User> USER = AttributeKey.valueOf("userdata");

    public static void start() throws IOException, InterruptedException {
        LOG.info("Start Netty...");

        // SPRING

        final ApplicationContext ctx = new ClassPathXmlApplicationContext("configSpring.xml");
        // Ставим всех юзеров в ОФФлайн
        UserDAO userDao = (UserDAO) ctx.getBean("userDAO");
        LOG.info("set to offline users " + userDao.setAllOffline());

        //MapServerHandler mapServerHandler =  (MapServerHandler)ctx.getBean ("mapServerHandler");


        // ===========================================================
        // 1. define a separate thread pool to execute handlers with
        //    slow business logic. e.g database operation
        // ===========================================================
        final EventExecutorGroup group = new DefaultEventExecutorGroup(1500); //thread pool of 1500

        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
//		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                LOG.info("init new Channel isTcpNoDelay = " + ch.config().isTcpNoDelay() + " addr=" + ch.remoteAddress());
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast("idleStateHandler", new IdleStateHandler(30, 10, 0)); // генерирует евенты-тригеры, которые будут перехвачены в хендлере, метод userEventTriggered, каждые указаные ххх секунд. readerIdleTime, writerIdleTime, allIdleTime
                pipeline.addLast("streamer", new ChunkedWriteHandler());
                pipeline.addLast("MyEncoder", new PacketEncoder()); // add without name, name auto generated
                pipeline.addLast("MeDecoder", new PacketDecoder()); // add without name, name auto generated

                //===========================================================
                // 2. run handler with slow business logic
                //    in separate thread from I/O thread
                //===========================================================
                pipeline.addLast(group, "serverHandler", (MapServerHandler) ctx.getBean("mapServerHandler"));
            }
        });


        try {
            ChannelFuture future = bootstrap.bind(portTCP).sync();
            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        System.out.println("new connection UNSuccess " + channelFuture.channel().id());
                    } else {
                        System.out.println("new connection isSuccess " + channelFuture.channel().remoteAddress());
                    }

                }
            });
            future.channel().closeFuture().sync();
        } finally {
            System.out.println("finally");
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }


}
