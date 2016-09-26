package gr.ru.processData;

import gr.ru.dao.Notific;
import gr.ru.dao.NotificDAO;
import gr.ru.dao.User;
import gr.ru.netty.NettyServer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Слушатель отправки нотификейшенов.
 *
 *
 */
public class SendNotifFutureListener  implements ChannelFutureListener {

    private NotificDAO notificDAO;

    Notific notifORM;

//    SendNotifFutureListener(Notific _notifORM){
//        notifORM = _notifORM;
//    }

    public void setnotifORM(Notific _notifORM){

    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        System.out.println("Notif operationComplete");
        // Если Нотифик удалось отправить и он присестивный, удаляем его из МУСКЛ
        if (future.isSuccess() && notifORM.getId()!=null ){
            System.out.println("REMOVE Notif");
            User curentUser = future.channel().attr(NettyServer.USER).get();
            curentUser.removeNotif(notifORM);
        }
        // Если попытки закончены, неудачно и Нотифик не пресист, сохраняем
        else if (future.isDone() && !future.isSuccess() && notifORM.getId()==null){
            notificDAO.saveNotific(notifORM);
        }
    }


    public NotificDAO getNotificDAO() {
        return notificDAO;
    }

    public void setNotificDAO(NotificDAO notificDAO) {
        this.notificDAO = notificDAO;
    }
}
