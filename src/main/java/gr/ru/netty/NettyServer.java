package gr.ru.netty;


import java.io.IOException;
import java.util.HashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class NettyServer {
	static public final int portTCP = 56888;
	// Атрибуты которые будут у канала
	public final static AttributeKey<Integer> SESSION 	= AttributeKey.valueOf("unic_session");
	public final static AttributeKey<User> USER 		= AttributeKey.valueOf("userdata");

	public static void start() throws IOException, InterruptedException {
		
 
		// SPRING
		
		final ApplicationContext ctx = new ClassPathXmlApplicationContext( "configSpring.xml" );
		// Ставим всех юзеров в ОФФлайн
		UserDAO userDao = (UserDAO) ctx.getBean ("userDAO");
		System.out.println("set to offline users " + userDao.setAllOffline());
		
		//MapServerHandler mapServerHandler =  (MapServerHandler)ctx.getBean ("mapServerHandler");
		
		
		// ===========================================================
		// 1. define a separate thread pool to execute handlers with
		//    slow business logic. e.g database operation
		// ===========================================================
		final EventExecutorGroup group = new DefaultEventExecutorGroup(1500); //thread pool of 1500
		
		System.out.println("server...");
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
				System.out.println("isTcpNoDelay = "+ch.config().isTcpNoDelay());
				ChannelPipeline pipeline = ch.pipeline();
				
				pipeline.addLast("idleStateHandler", new IdleStateHandler(30,10,0)); // генерирует евенты-тригеры, которые будут перехвачены в хендлере, метод userEventTriggered, каждые указаные ххх секунд. readerIdleTime, writerIdleTime, allIdleTime
				pipeline.addLast("MyEncoder", new PacketEncoder()); // add without name, name auto generated
				pipeline.addLast("MeDecoder", new PacketDecoder()); // add without name, name auto generated

				//===========================================================
				// 2. run handler with slow business logic 
				//    in separate thread from I/O thread
				//===========================================================
				pipeline.addLast(group,"serverHandler", (MapServerHandler)ctx.getBean ("mapServerHandler") ); 
			}
		});

		
		
		try{
			ChannelFuture future = bootstrap.bind(portTCP).sync();
			future.addListener(new ChannelFutureListener(){
				public void operationComplete(ChannelFuture channelFuture) throws Exception {
		            if (!channelFuture.isSuccess()) {
		            	System.out.println("new connection UNSuccess " + channelFuture.channel().id());
		            }else {
		            	System.out.println("new connection isSuccess "+ channelFuture.channel().id());
		            }
					
				}});
			future.channel().closeFuture().sync();
		}finally {
			System.out.println("finally");
			boosGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}
	

    
    
}
