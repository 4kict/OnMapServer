package gr.ru.processData;

import gr.ru.HashMapDB.HashMapDB;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client.UserInfo;
import gr.ru.netty.protokol.Packs2Server.ReqUserInfo;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestInfo implements HandleTelegramm {

    private static final Logger LOG = LogManager.getLogger(RequestInfo.class);
    @Autowired
    private HashMapDB hashMapDB;
    @Autowired
    private UserDAO userDao;

    @Override
    public void handle(ChannelHandlerContext ctxChanel, Packet packet) {
        ReqUserInfo reqUserInf = validTele(packet);
        if (reqUserInf == null) {
            LOG.error("Validation of ReqUserInf - ERR");
            return;
        }

        // Запрашиваемые данные юзера скорее всего есть в hashMapDB, т.к. этот запрос приходит когда по юзеру кликнули на карте
        // Еще этот запрос приходит когда у одного из юзеров есть непрочитанные сообщения от другого, неизвестного юзера
        // А еще когда кликаем по соотв. иконке в списке чатов
        //System.out.println("Asck Requesr Info in hashMapDB");
        User user = hashMapDB.getUser(reqUserInf.uid);
        LOG.trace("User Search in hash =" + user);
        if (user == null) {                            // Если юзер НЕнашелся в оперативной БД
            user = userDao.getUser(reqUserInf.uid);    // Ищем в МУСКЛ
            LOG.trace("User Search in MYSQL =" + user);
        }

        if (user != null) {                                // Только если данные юзера хоть где-то нашлись

            UserInfo userInfo = (UserInfo) PacketFactory.produce(PacketFactory.USER_INFO);
            userInfo.uid = user.getId();
            userInfo.name = user.getName();
            userInfo.lat = user.getLat();
            userInfo.lon = user.getLon();
            userInfo.stat = user.getStatus();
            // TODO: Гугл может не определить локацию, тогда клиент получит НУЛ (так ли?) и андройд выдаст ошибку.
            userInfo.country = user.getCountry() == null ? "" : user.getCountry();
            userInfo.local = user.getCity() == null ? "" : user.getCity();
            userInfo.language = user.getLocale();
            LOG.trace("UserInfo to Send =" + userInfo);
            ctxChanel.writeAndFlush(userInfo);
        } else {
            LOG.warn("can't find user " + reqUserInf);
        }
    }

    @Override
    public ReqUserInfo validTele(Packet packet) {

        ReqUserInfo reqUserInf = (ReqUserInfo) packet;
        if (reqUserInf.uid == 0) {
            return null;
        } else {
            return reqUserInf;
        }

    }

}
