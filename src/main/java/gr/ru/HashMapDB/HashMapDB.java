package gr.ru.HashMapDB;

import gr.ru.MyApp;
import gr.ru.dao.User;
import io.netty.channel.Channel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

//import org.apache.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

@Component
public class HashMapDB {
    private static final Logger LOG = LogManager.getLogger(MyApp.class);
    //private HashSet<User> usersHashSet = new HashSet<User>();

    private HashMap<String, User> usersHashMap = new HashMap<>();

    public HashMapDB() {
//        BotBuilder botBuilder = new BotBuilder();
//        add(botBuilder.setCountry("China").setCity("bejin").setLat(45261479).setLon(127754462).setName("bot1-Ch").build());
//        add(botBuilder.setCountry("Shri-Lanka").setCity("Bangalor").setLat(12942364).setLon(7767026).setName("bot1-Srialn").build());
//        add(botBuilder.setCountry("India").setCity("XZZ").setLat(28.6682049).setLon(77.2119786).setName("bot-Eg").build());
//        add(botBuilder.setCountry("JUAR").setCity("Keiptaun").setLat(-27906751).setLon(21498461).setName("bot-juar").build());
//        add(botBuilder.setCountry("Urugvai").setCity("Montevideo").setLat(-34543937).setLon(-56973623).setName("bot2").build());
//        add(botBuilder.setCountry("Panama").setCity("panama").setLat(9015195).setLon(-79530845).setName("bot3").build());
//        add(botBuilder.setCountry("Australia").setCity("sidney").setLat(-33.848673).setLon(151.034710).setName("Sidne bot1").build());
//        add(botBuilder.setCountry("USA").setCity("Torento").setLat(25.499860).setLon(-103.370393).setName("Sidne bot2").build());
//
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48854386).setLon(2319386).setName("bot4").build());
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48870796).setLon(2306236).setName("bot5").build());
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48804386).setLon(2339386).setName("bot6").build());
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48844386).setLon(2399386).setName("bot7").build());
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48864386).setLon(2355386).setName("bot8").build());
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48874386).setLon(2369386).setName("bot9").build());
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48884386).setLon(2374386).setName("bot10").build());
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48194386).setLon(2413386).setName("bot1").build());
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48294386).setLon(2423386).setName("bot1").build());
//        add(botBuilder.setCountry("France").setCity("Pari").setLat(48394386).setLon(2433386).setName("bot1").build());
    }

    public void add(User user) {
        usersHashMap.put(user.getId().toString(), user);
    }


    public User getUser(Long uId) {
        if (uId < 1)
            return null;
        return usersHashMap.get(uId.toString());

    }


    public User[] getAllUsers() {
        return usersHashMap.values().toArray(new User[usersHashMap.size()]);
    }


    public void removeUser(Long uId) {
        if (uId < 1)
            return;

        LOG.trace("usersHashMap.removeUser id=" + uId + " result=" + usersHashMap.remove(uId));
        usersHashMap.remove(uId.toString());
    }

    public int size() {
        return usersHashMap.size();
    }





}
