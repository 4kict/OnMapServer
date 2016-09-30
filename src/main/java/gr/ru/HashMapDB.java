package gr.ru;

import gr.ru.dao.User;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.HashMap;


public class HashMapDB {
	private static final Logger LOG = Logger.getLogger(HashMapDB.class);
	//private HashSet<User> usersHashSet = new HashSet<User>();
	
	private HashMap<Long, User> usersHashMap = new HashMap<Long, User>();
	
	public void add(User user) {
		usersHashMap.put(user.getId(), user);		
	}
	
	public Channel getMapConnect (Long uId){
		return this.getUser(uId).getMapChanel();
	}
	
	public User getUser (Long uId){
		if (uId < 1)
			return null;
		return usersHashMap.get(uId);
		
	}



	public User[]  getListOfUsers() {	
//		ArrayList<User> values = new ArrayList<User>();
//		int i = 0;
//		Iterator<User> itr = usersHashSet.iterator();
//		while (itr.hasNext()) {
//			User nextUser = itr.next();
//			if (!nextUser.isCluster()){
//				values.add(nextUser)	;					
//			}
//		}		
		return usersHashMap.values().toArray(new User[usersHashMap.size()]);	
	}

	
	public void removeUser (Long uId){		
		if (uId < 1)
			return;
		
		LOG.trace("usersHashMap.removeUser id="+uId + " result=" + usersHashMap.remove(uId));
		usersHashMap.remove(uId);
	}
	
	public int size(){
		return usersHashMap.size();
	}
	
	
}
