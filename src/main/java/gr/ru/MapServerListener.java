package gr.ru;

///import com.esotericsoftware.kryonet.Listener;

//@Component	//	обьявляем этот класс компонентом Спринга
public class MapServerListener{
/*public class MapServerListener extends Listener {

	private ForwardedMsg forwardedMsg;
	private UserPosition userPosition;
	private RegUser regUser;
	private RequestInfo requestInfo;
	private RequestList requestList;
	private UserDisconnect userDisconnect;
	private UserCommand userCommand;

	@Override
	public void connected(Connection arg0) {
		
		super.connected(arg0);
	}

	@Override
	public void disconnected(Connection connect) {		
		//System.out.println("disconnected connect="+connect+ " MapConnection="+ ((MapConnection) connect) +" userDisconnect"+userDisconnect);
		userDisconnect.handle((MapConnection) connect, null);
		super.disconnected(connect);
	}

	@Override
	public void received(Connection connect, Object object) {

	      SimpleDateFormat ft = new SimpleDateFormat ("H:m:s S");

				
		//Смотрим что пришло сообщение соотв формату Клиент->Сервер
		if (object instanceof TelClientServer) {

			MapConnection mapConnect = (MapConnection) connect;
			TelClientServer tellegramm = (TelClientServer) object;

			
//			if( mapConnect.getUser() != null )
//				System.out.println("["+ft.format(new Date(System.currentTimeMillis()))+ "] received id#"+connect.getID()+" '" + object.getClass().getSimpleName() + "' from "+mapConnect.getUser().getName() );
//			else
//				System.out.println("["+ft.format(new Date(System.currentTimeMillis()))+ "] received id#"+connect.getID()+" '" + object.getClass().getSimpleName() + "' from unregistred user name="+((RegData)object).name  );				
//		
			
			
			
			//  Проверяем сессию и регистрацию 
			if ( (tellegramm.ses==mapConnect.getSession() && mapConnect.getUser()!=null )  || tellegramm.typ==gutil.TYPE_REGISTRATION ){
				switch (tellegramm.typ) {
				case gutil.TYPE_REGISTRATION:					
					regUser.handle(mapConnect, tellegramm);
					break;
				case gutil.TYPE_REQUEST:
					requestList.handle(mapConnect, tellegramm);
					break;
				case gutil.TYPE_POSITION:
					userPosition.handle(mapConnect, tellegramm);
					break;
				case gutil.TYPE_MESSAGE:
					forwardedMsg.handle(mapConnect, tellegramm);
					break;				
				case gutil.TYPE_REQUSERINFO:
					requestInfo.handle(mapConnect, tellegramm);
					break;
				case gutil.TYPE_COMMAND:					
					userCommand.handle(mapConnect, tellegramm);
					break;
				default:
					//System.out.println("Telegram not identyfired typ="+tellegramm.typ);
					break;
				}
			}else {
				System.out.println("TelClientServer not valid. Typ="+tellegramm.typ+" ses="+tellegramm.ses+"=="+mapConnect.getSession()+ " usr="+mapConnect.getUser());
				return;
			}
			return;
		}else{
			//System.out.println( "["+ft.format(new Date(System.currentTimeMillis()))+ "] recived id#"+connect.getID()+" obj="+object.getClass().getName());
		}
	}

	
	
	
	
	
	
	
	


	public UserDisconnect getUserDisconnect() {
		return userDisconnect;
	}

	public void setUserDisconnect(UserDisconnect userDisconnect) {
		this.userDisconnect = userDisconnect;
	}


	public ForwardedMsg getForwardedMsg() {
		return forwardedMsg;
	}


	public void setForwardedMsg(ForwardedMsg forwardedMsg) {
		this.forwardedMsg = forwardedMsg;
	}


	public UserPosition getUserPosition() {
		return userPosition;
	}


	public void setUserPosition(UserPosition userPosition) {
		this.userPosition = userPosition;
	}


	public RegUser getRegUser() {
		return regUser;
	}


	public void setRegUser(RegUser regUser) {
		this.regUser = regUser;
	}


	public RequestInfo getRequestInfo() {
		return requestInfo;
	}


	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}


	public RequestList getRequestList() {
		return requestList;
	}


	public void setRequestList(RequestList requestList) {
		this.requestList = requestList;
	}

	public UserCommand getUserCommand() {
		return userCommand;
	}

	public void setUserCommand(UserCommand userCommand) {
		this.userCommand = userCommand;
	}
	
	
*/
}
