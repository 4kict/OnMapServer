package gr.ru;

import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import io.netty.channel.Channel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

//@Aspect
public class LoggerAOP {


    public LoggerAOP() {
        // TODO Auto-generated constructor stub
    }

//	@Pointcut("execution(* gr.ru.dao.UserDAO.saveOrUpdate(..)) && args(user)")
//	public void saveUser(){	}

//	//@Around("execution(* gr.ru.dao.UserDAO.saveOrUpdate(..)) && args(user)")
//	public void watchSaveUser(ProceedingJoinPoint joinpoint, User user){
//		System.out.println("AOP BEFORE save User=" + user);
//		try {
//			joinpoint.proceed();
//		} catch (Throwable e) {			
//			e.printStackTrace();
//		}
//		System.out.println("AOP UFTER save User=" + user);
//	}
//	


//	@Before("execution(* gr.ru.processData.RegUser.handle(..)) && args(mapConnect, tellegramm)")
//	public void watchRegUser(MapConnection mapConnect,TelClientServer tellegramm){
//		System.out.println("AOP BEFORE RegUser usr=" + mapConnect.getUser());
//	}


    @Around("execution(* gr.ru.processData.RegUser.handle(..)) && args(mapConnect, tellegramm)")
    public void watchRegUser(ProceedingJoinPoint joinpoint, Channel mapConnect, Packet tellegramm) {
        System.out.println("AOP BEFORE RegUser usr=" + mapConnect.attr(NettyServer.USER).get());
        try {
            joinpoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("AOP UFTER save User=" + mapConnect.attr(NettyServer.USER).get());
    }


    @Around("execution(* gr.ru.processData.UserDisconnect.handle(..)) && args(mapConnect, tellegramm)")
    public void watchDisconnectUser(ProceedingJoinPoint joinpoint, Channel mapConnect, Packet tellegramm) {
        System.out.println("AOP BEFORE Disconnect usr=" + mapConnect.attr(NettyServer.USER).get());
        try {
            joinpoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("AOP UFTER Disconnect User=" + mapConnect.attr(NettyServer.USER).get());
    }


    @Around("execution(* gr.ru.processData.ForwardedMsg.handle(..)) && args(mapConnect, tellegramm)")
    public void watchForwardedMsgUser(ProceedingJoinPoint joinpoint, Channel mapConnect, Packet tellegramm) {
        System.out.println("AOP BEFORE ForwardedMsg usr=" + mapConnect.attr(NettyServer.USER).get());
        try {
            joinpoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("AOP UFTER ForwardedMsg User=" + mapConnect.attr(NettyServer.USER).get());
    }


    @Around("execution(* gr.ru.processData.UserCommand.handle(..)) && args(mapConnect, tellegramm)")
    public void watchUserCommandUser(ProceedingJoinPoint joinpoint, Channel mapConnect, Packet tellegramm) {
        System.out.println("AOP BEFORE UserCommand usr=" + mapConnect.attr(NettyServer.USER).get());
        try {
            joinpoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("AOP UFTER UserCommand User=" + mapConnect.attr(NettyServer.USER).get());
    }


    @Around("execution(* gr.ru.processData.UserPosition.handle(..)) && args(mapConnect, tellegramm)")
    public void watchUserPositionUser(ProceedingJoinPoint joinpoint, Channel mapConnect, Packet tellegramm) {
        System.out.println("AOP BEFORE UserPosition usr=" + mapConnect.attr(NettyServer.USER).get());
        try {
            joinpoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("AOP UFTER UserPosition User=" + mapConnect.attr(NettyServer.USER).get());
    }


}








