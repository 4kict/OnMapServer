package gr.ru.HashMapDB;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import gr.ru.MyApp;
import gr.ru.dao.User;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

import static gr.ru.gutil.SETUP_CLUSTERS_IN_VIEW;
import static gr.ru.gutil.SETUP_POINTS_IN_VIEW;
import static gr.ru.gutil.STATUS_ACTIVE;
import static java.lang.String.format;


@Component
public class HashMapDB {
    private static final Logger LOG = LogManager.getLogger(MyApp.class);
    //private HashSet<User> usersHashSet = new HashSet<User>();

    // Мэпа юзеров для поиска по ID. для чата
    private HashMap<String, User> usersHashMap = new HashMap<>();
    // Мэпа кластеров. В кластере только Активные юзеры
    private HashMap<String, List<User>> userClusters = new HashMap<>();

    public HashMapDB() {
//        BotBuilder botBuilder = new BotBuilder();
//
//        User bot;
//        for (int i=0; i<1000; i++) {
//            bot = botBuilder.setCountry(UUID.randomUUID().toString())
//                    .setCity(UUID.randomUUID().toString())
//                    .setLat(ThreadLocalRandom.current().nextInt(-90000000, 90000000))
//                    .setLon(ThreadLocalRandom.current().nextInt(-180000000, 180000000))
//                    .setName(UUID.randomUUID().toString()).build();
//            this.add(bot);
//        }
////        BotBuilder botBuilder = new BotBuilder();
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


    public User getUser(Long uId) {
        if (uId < 1)
            return null;
        return usersHashMap.get(uId.toString());

    }


    private User[] getAllActiveUsers() {
        List<User> visibleUsers = Lists.newArrayList(Iterables.filter(usersHashMap.values(), new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                return input.getStatus()==STATUS_ACTIVE;
            }
        }));

        if (visibleUsers.size() > SETUP_POINTS_IN_VIEW) {
            Collections.shuffle(visibleUsers);
            return visibleUsers.subList(0, SETUP_POINTS_IN_VIEW).toArray(new User[SETUP_POINTS_IN_VIEW]);
        }
        return visibleUsers.toArray(new User[visibleUsers.size()]);

    }


    public void add(User user) {
        usersHashMap.put(user.getId().toString(), user);
        String clusterName = Cluster.calcName(user.getLat(), user.getLon());
        List<User> usersList = userClusters.get(clusterName);
        if (usersList == null) {
            usersList = new ArrayList<>();
            userClusters.put(clusterName, usersList);
        }
        usersList.add(user);
    }

    public User[] getFromCluster(int neLat, int neLon, int swLat, int swLon) {
        int totalClusters = Cluster.calcSquare(neLat, neLon, swLat, swLon);
        if (totalClusters == 0) {
            LOG.warn(format("Wrong request of points: neLat %s, neLon %s, swLat %s, swLon %s", neLat, neLon, swLat, swLon));
        }
        // Если в область видисости слишком много кластеров, вернем всех
        if (totalClusters > SETUP_CLUSTERS_IN_VIEW) {
            return getAllActiveUsers();
        }
        List<String> clusterNames = Cluster.getNames(neLat, neLon, swLat, swLon);
        List<User> usersFromClusters = new ArrayList<>();
        for (String clusterName : clusterNames) {
            List<User> userCluster = userClusters.get(clusterName);
            if (userCluster != null) {
                usersFromClusters.addAll(userCluster);
            }
        }
        // Если в области видимости слишком много юзеров
        if (usersFromClusters.size() > SETUP_POINTS_IN_VIEW) {
            Collections.shuffle(usersFromClusters);
            return usersFromClusters.subList(0, SETUP_POINTS_IN_VIEW).toArray(new User[SETUP_POINTS_IN_VIEW]);
        }
        return usersFromClusters.toArray(new User[usersFromClusters.size()]);
    }

    public void activateUser(Long uId){
        User userToActive = usersHashMap.get(uId.toString());
        String clusterName = Cluster.calcName(userToActive.getLat(), userToActive.getLon());
        List<User> usersList = userClusters.get(clusterName);
        if (usersList == null) {
            usersList = new ArrayList<>();
            userClusters.put(clusterName, usersList);
        }
        usersList.add(userToActive);
    }

    public void deactiveUser(Long uId){
        User userToRemove = usersHashMap.get(uId.toString());
        List<User> usersList = userClusters.get(Cluster.calcName(userToRemove.getLat(), userToRemove.getLon()));
        if (usersList == null) {
            LOG.warn("There is no cluster '" + Cluster.calcName(userToRemove.getLat(), userToRemove.getLon()) + "' for user to delete with id=" + uId);
            return;
        }
        if (!usersList.remove(userToRemove)) {
            LOG.warn("There is no user to delete with id=" + uId + " in user cluster '" + Cluster.calcName(userToRemove.getLat(), userToRemove.getLon()) + "'");
        }
    }

    public void removeUser(Long uId) {
        if (uId < 1) {
            return;
        }
        LOG.trace("usersHashMap.removeUser id=" + uId + " result=" + usersHashMap.remove(uId));
        User userToDelete = usersHashMap.remove(uId.toString());
        if (userToDelete == null) {
            LOG.warn("User to delete with id=" + uId + " is missing!");
            return;
        }
        List<User> usersList = userClusters.get(Cluster.calcName(userToDelete.getLat(), userToDelete.getLon()));
        if (usersList == null) {
            LOG.warn("There is no cluster '" + Cluster.calcName(userToDelete.getLat(), userToDelete.getLon()) + "' for user to delete with id=" + uId);
            return;
        }
        if (!usersList.remove(userToDelete)) {
            LOG.warn("There is no user to delete with id=" + uId + " in user cluster '" + Cluster.calcName(userToDelete.getLat(), userToDelete.getLon()) + "'");
        }
    }

    public int size() {
        return usersHashMap.size();
    }

    public HashMap<String, User> getUsersHashMap() {
        return usersHashMap;
    }

    public HashMap<String, List<User>> getUserClusters() {
        return userClusters;
    }
}
