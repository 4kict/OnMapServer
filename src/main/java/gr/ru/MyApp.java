package gr.ru;

import com.esotericsoftware.kryonet.Server;
import gr.ru.netty.NettyServer;
import org.apache.log4j.Logger;

import java.io.IOException;


public class MyApp {
	private static final Logger LOG = Logger.getLogger(MyApp.class);

	Server serverKryo;
	
	public MyApp( MapServerListener mapServerListener) {
//
//		serverKryo = new Server() {
//			protected Connection newConnection () {
//				// By providing our own connection implementation, we can store per connection state without a connection ID to state look up.
//				return new MapConnection();
//			}
//		};
//		 
//		Network.register(this.serverKryo);						// ������������ ������������ ������
//		this.serverKryo.addListener(mapServerListener);		// ������������ ���������
//		try {
//			serverKryo.bind (Network.portTCP, Network.portUDP );// ������������� ����
//		} catch (IOException e) {
//			e.printStackTrace();
//		}	
//		serverKryo.start();										// �������� ������
//		
//		System.out.println("Server is open..."  );
		
	}
	
	

	public static void main(String[] args) {
		LOG.info("Start OnMapServer");
		System.out.println("Starting MyHiberSpring2");





		try {
			//new NettyServer();
			NettyServer.start();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		
//		new MyApp (mapServerListener);		
//		mapServerListener.received(fakeMapConnection, regDat );
//		
//		try {
//			Thread.sleep(3);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}			
//		System.out.println("Start disconnected");
//		
//		mapServerListener.disconnected(fakeMapConnection);
		
		//mapServerListener.received(fakeMapConnection2, regDat2 );

//		UserDAO userDao =  (UserDAO)ctx.getBean ("userDAO");
//		User user = userDao.regUser(regDat);Id());
		
		
	}


	
}

