package gr.ru.processData;

import gr.ru.HashMapDB;
import gr.ru.dao.User;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client.MapPoint;
import gr.ru.netty.protokol.Packs2Client.PointArray;
import gr.ru.netty.protokol.Packs2Server.RequestPoints;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class RequestList implements HandleTelegramm {

    private static final Logger LOG = LogManager.getLogger(RequestList.class);
    private HashMapDB hashMapDB;

    @Override
    public void handle(ChannelHandlerContext ctxChanel, Packet packet) {

        // Преобразование и проверка данных
        RequestPoints rqstTel = validTele(packet);
        if (rqstTel == null) {
            LOG.error("Validation of Rqst - ERR");
            return;
        }

        User[] users = hashMapDB.getListOfUsers();
        PointArray pointsArr = (PointArray) PacketFactory.produce(PacketFactory.POINT_ARRAY);
        pointsArr.points = new MapPoint[users.length];
        for (int i = 0; i < users.length; i++) {
            pointsArr.points[i] = (MapPoint) PacketFactory.produce(PacketFactory.MAP_POINT);
            pointsArr.points[i].clu = users[i].isCluster();
            pointsArr.points[i].ico = users[i].getIcon();
            pointsArr.points[i].lat = users[i].getLat();
            pointsArr.points[i].lon = users[i].getLon();
            pointsArr.points[i].uid = users[i].getId();
            //LOG.trace("point ="+pointsArr.points[i]);
        }
        LOG.debug("send PointArray =" + pointsArr.toString());
        ctxChanel.writeAndFlush(pointsArr);

    }


    @Override
    public RequestPoints validTele(Packet packet) {

        RequestPoints rqstTel = (RequestPoints) packet;
        if (rqstTel.nx == 0 || rqstTel.ny == 0 || rqstTel.sx == 0 || rqstTel.sy == 0) {
            return null;
        } else {
            return rqstTel;
        }


    }

    public HashMapDB getHashMapDB() {
        return hashMapDB;
    }

    public void setHashMapDB(HashMapDB hashMapDB) {
        this.hashMapDB = hashMapDB;
    }


}
