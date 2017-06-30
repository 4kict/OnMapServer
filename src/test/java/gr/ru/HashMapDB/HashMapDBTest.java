package gr.ru.HashMapDB;

import gr.ru.dao.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static gr.ru.gutil.SETUP_POINTS_IN_VIEW;

/**
 * Created by
 */
public class HashMapDBTest {
    private HashMapDB hashMapDB = new HashMapDB();

    private HashMap<String, User> mockUsersHashMap = new HashMap<>();
    private HashMap<String, List<User>> mockUserClusters = new HashMap<>();

    @Before
    public void init() {
        BotBuilder botBuilder = new BotBuilder();

        User bot;
        for (int i=0; i<100000; i++) {
            bot = botBuilder.setCountry(UUID.randomUUID().toString())
                    .setCity(UUID.randomUUID().toString())
                    .setLat(ThreadLocalRandom.current().nextInt(-90000000, 90000000))
                    .setLon(ThreadLocalRandom.current().nextInt(-180000000, 180000000))
                    .setName(UUID.randomUUID().toString())
                    .build();
            hashMapDB.add(bot);
        }
//        hashMapDB.add(botBuilder.setCountry("China").setCity("bejin").setLat(45261479).setLon(127754462).setName("bot1-Ch").build());
//        hashMapDB.add(botBuilder.setCountry("Shri-Lanka").setCity("Bangalor").setLat(12942364).setLon(7767026).setName("bot1-Srialn").build());
//        hashMapDB.add(botBuilder.setCountry("India").setCity("XZZ").setLat(28.6682049).setLon(77.2119786).setName("bot-Eg").build());
//        hashMapDB.add(botBuilder.setCountry("JUAR").setCity("Keiptaun").setLat(-27906751).setLon(21498461).setName("bot-juar").build());
//        hashMapDB.add(botBuilder.setCountry("Urugvai").setCity("Montevideo").setLat(-34543937).setLon(-56973623).setName("bot2").build());
//        hashMapDB.add(botBuilder.setCountry("Panama").setCity("panama").setLat(9015195).setLon(-79530845).setName("bot3").build());
//        hashMapDB.add(botBuilder.setCountry("Australia").setCity("sidney").setLat(-33.848673).setLon(151.034710).setName("Sidne bot1").build());
//        hashMapDB.add(botBuilder.setCountry("USA").setCity("Torento").setLat(25.499860).setLon(-103.370393).setName("Sidne bot2").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48854386).setLon(2319386).setName("bot4").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48870796).setLon(2306236).setName("bot5").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48804386).setLon(2339386).setName("bot6").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48844386).setLon(2399386).setName("bot7").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48864386).setLon(2355386).setName("bot8").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48874386).setLon(2369386).setName("bot9").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48884386).setLon(2374386).setName("bot10").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48194386).setLon(2413386).setName("bot1").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48294386).setLon(2423386).setName("bot1").build());
//        hashMapDB.add(botBuilder.setCountry("France").setCity("Pari").setLat(48394386).setLon(2433386).setName("bot1").build());
    }

    @Test
    public void getFromCluster() throws Exception {
        User[] clusters = hashMapDB.getFromCluster(8, 10, 3, 4);
        Assert.assertEquals(SETUP_POINTS_IN_VIEW, clusters.length);
        clusters = hashMapDB.getFromCluster(14, 37, 11, 33);
        Assert.assertTrue(clusters.length>1 && clusters.length<=SETUP_POINTS_IN_VIEW);

    }

    @Test
    public void getAllActive(){
        User[] usters = hashMapDB.getFromCluster(8, 10, 11, 33);
        System.out.println("");
    }

    @Test
    public void perfomanceTest() {
//        long start = System.currentTimeMillis();
//        hashMapDB.getAllUsers();
//        long stop = System.currentTimeMillis();
//        System.out.println("getAllUsers random= " + (stop-start));

    }


}