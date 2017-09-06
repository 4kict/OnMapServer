package gr.ru.processData;

import gr.ru.HashMapDB.HashMapDB;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.gutil;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDisconnect implements HandleTelegramm {

    private static final Logger LOG = LogManager.getLogger(UserDisconnect.class);
    @Autowired
    private UserDAO userDao;
    @Autowired
    private HashMapDB hashMapDB;

    @Override
    public void handle(ChannelHandlerContext ctxChanel, Packet packet) {
        User userToDelete = ctxChanel.channel().attr(NettyServer.USER).get();

        if (userToDelete != null) {
            userToDelete.setStatus(gutil.STATUS_OFFLINE);

            LOG.debug("userToDelete=" + userToDelete);
            hashMapDB.removeUser(userToDelete.getId());                    // Удаляем из Хэша
            userDao.saveOrUpdate(userToDelete);                            // Если нашли, сохраняем его последние данные в МУСКЛ, предварительно сделав его ОФФЛАЙН
        }
    }

    @Override
    public Packet validTele(Packet packet) {
        // TODO Auto-generated method stub
        return null;
    }

}
