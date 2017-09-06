package gr.ru.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gr.ru.HashMapDB.HashMapDB;
import gr.ru.MyApp;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by
 */
public class Scheduler {

    @Autowired
    private HashMapDB hashMapDB;

    private static final Logger LOG = LogManager.getLogger(MyApp.class);

    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    private static final String mapPath;
    private static final String clusterPath;
    private static boolean isDevelopMode = Boolean.parseBoolean(System.getProperty("developMode"));

    static {
        String path = System.getProperty("pathToMap") == null ? "" : System.getProperty("pathToMap");
        mapPath = path + "usersMap.json";
        clusterPath = path + "usersClusters.json";
    }

    @Scheduled(fixedDelay = 5000)
    public void actualHashReplicator() {
        if (isDevelopMode) {
            try (FileWriter fooWriter = new FileWriter(new File(mapPath), false)) {
                fooWriter.write(GSON.toJson(hashMapDB.getUsersHashMap()));
                fooWriter.close();
            } catch (IOException e) {
                LOG.warn("cant save usersMap to:" + mapPath);
            }
            try (FileWriter fooWriter = new FileWriter(new File(clusterPath), false)) {
                fooWriter.write(GSON.toJson(hashMapDB.getClustersCollector()));
                fooWriter.close();
            } catch (IOException e) {
                LOG.warn("cant save usersClusters to:" + clusterPath);
            }
        }
    }

}
