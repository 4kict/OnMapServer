package gr.ru.utils;

import gr.ru.HashMapDB.Cluster;
import gr.ru.dao.User;
import gr.ru.gutil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 */
public class UtilTest {

    @Test
    public void zoneTest() {

    }


    @Test
    public void testTest() {
        List<User> list = new ArrayList();
        User user1 = new User();
        user1.setId(1L);
        list.add(user1);
        System.out.println(list.size());
        user1.setLastPresist(123L);
        list.remove(user1);
        System.out.println(list.size());
    }


}
