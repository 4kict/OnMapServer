package gr.ru.HashMapDB;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by
 */
public class ClusterTest {

    @Test
    public void calcName() {
        Assert.assertEquals(1, Cluster.getLonQad(-178999999));
        Assert.assertEquals(60, Cluster.getLonQad(179999999));
        Assert.assertEquals(31, Cluster.getLonQad(1099999));
        Assert.assertEquals(30, Cluster.getLonQad(-1099999));

        Assert.assertEquals(1, Cluster.getLatQad(-89666666));
        Assert.assertEquals(22, Cluster.getLatQad(89666666));
        Assert.assertEquals(12, Cluster.getLatQad(-1333333));
        Assert.assertEquals(11, Cluster.getLatQad(1333333));

        Assert.assertEquals("0101", Cluster.calcName(-89666666, -178999999));
        Assert.assertEquals("2260", Cluster.calcName(89666666, 179999999));
        Assert.assertEquals("1101", Cluster.calcName(1333333, -178999999));
    }

    @Test
    public void calcSquare() {
        Assert.assertEquals(42, Cluster.calcSquare(8, 10, 3, 4));
        Assert.assertEquals(12, Cluster.calcSquare(2, 1, 21, 59));
    }


    @Test
    public void getSquare() {
        List<String> clusterNames = Cluster.getNames(2, 1, 21, 59);
        Assert.assertEquals(12, clusterNames.size());
        Assert.assertEquals("2159", clusterNames.get(0));
        Assert.assertEquals("0201", clusterNames.get(11));

        System.out.println(clusterNames);
    }

}