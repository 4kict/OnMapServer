package gr.ru;

import gr.ru.dao.User;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;


public class HashMapDB {
    private static final Logger LOG = LogManager.getLogger(MyApp.class);
    //private HashSet<User> usersHashSet = new HashSet<User>();

    private HashMap<Long, User> usersHashMap = new HashMap<Long, User>();

    public void add(User user) {
        usersHashMap.put(user.getId(), user);
    }

    public Channel getMapConnect(Long uId) {
        return this.getUser(uId).getMapChanel();
    }

    public User getUser(Long uId) {
        if (uId < 1)
            return null;
        return usersHashMap.get(uId);

    }


    public User[] getListOfUsers() {
        return usersHashMap.values().toArray(new User[usersHashMap.size()]);
    }


    public void removeUser(Long uId) {
        if (uId < 1)
            return;

        LOG.trace("usersHashMap.removeUser id=" + uId + " result=" + usersHashMap.remove(uId));
        usersHashMap.remove(uId);
    }

    public int size() {
        return usersHashMap.size();
    }


}
