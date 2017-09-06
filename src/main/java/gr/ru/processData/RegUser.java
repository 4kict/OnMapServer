package gr.ru.processData;

import gr.ru.ApplicationContextUtils;
import gr.ru.HashMapDB.HashMapDB;
import gr.ru.dao.Mesage;
import gr.ru.dao.Notific;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import gr.ru.geocoder.GeoDecoder;
import gr.ru.gutil;
import gr.ru.netty.NettyServer;
import gr.ru.netty.protokol.Packet;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client.MsgToUser;
import gr.ru.netty.protokol.Packs2Client.ServerStat;
import gr.ru.netty.protokol.Packs2Server.RegData;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import static gr.ru.gutil.STATUS_HIDE;

@Component
public class RegUser implements HandleTelegramm {
    private static final Logger LOG = LogManager.getLogger(RegUser.class);
    @Autowired
    private UserDAO userDao;
    @Autowired
    private HashMapDB hashMapDB;
    //private GeoDecoder geoDecoder;


    @Override
    public void handle(final ChannelHandlerContext ctxChanel, Packet packet) {

        // Преобразование и проверка что данные действительно регистрационные
        RegData regData = validTele(packet);
        if (regData == null) {
            LOG.error("Validation of RegData - ERR");
            return;
        }

		
		/* !!!ATTENTION!!! 
         * Надо крайне осторожно сбрасывать таблицу юзеров на сервере (сохранять сквозную нумерацию), т.к. пока нет механизма оповещения о сбросе уникальных ИД
		 * Проблемма: если у юзера в МУСЛК сохранены аппоненты со старыми ИД, а МУСКЛ на сервере сброшен и юзерам будут присвоены новые ИД, очевидно что сообщения пойдут не по адресу  
		 * ХОрошо бы заставить всех юзеров сбросить свои базы аппонентов, но пока не понятно как заставить это сделать тех кто в оффлайне. 
		 * Хорошо бы сохранять оповещения, но что бы их раздавать, надо отличать: - регистрация, - переконнект, - обновление рег данных, - юзер с данными от сброшенной МУСКЛ. 
		 *   
		 */


        User user = null;
        Long hashKey = (long) new String("" + regData.an_id + "" + regData.dev_id + "" + regData.key_id + "" + regData.name).hashCode();
        user = userDao.getUserByHashKey(hashKey);        // Ищем Юзера по новому Хэшкоду в МУСКЛ


        // ********* ПРЕ -РЕГИСТРАЦИЯ ***********
        // пришили регистрационные данные без координат, при этом такой юзер уже есть в МУСКЛ, он должне получать все сообщения
        if (regData.lat == 0 && regData.lon == 0 && regData.qad == 0 && ctxChanel.channel().attr(NettyServer.SESSION).get() == null && regData.sesion == 0
                && ctxChanel.channel().attr(NettyServer.USER).get() == null) {
            LOG.trace("PreReg new User " + regData.name);
            if (user != null) {
                user.setChanel(ctxChanel.channel());                    // Сохраняем ссылку на Канал в Юзере
                user.setStatus(STATUS_HIDE);
                userDao.saveOrUpdate(user);                        // Сохраняем Юзера в МУСКЛ
                hashMapDB.add(user);                                    // Сохраняем Юзера в Оперативной базе
                registrationAccept(ctxChanel, user);
            } else {
                LOG.debug("new regData without position, or first connection.");
                return;
            }
        }


        // ********* РЕГИСТРАЦИЯ ***********
        // Если в конекте пока не определен ИД и Сессия, а так же в рег данных нет сессии, значит юзер регается (подключился первый раз)
        // unic_id может присутствовать в случае обновления рег.данных и реконекта
        else if (regData.lat != 0 && regData.lon != 0 && regData.qad != 0 && ctxChanel.channel().attr(NettyServer.SESSION).get() == null && regData.sesion == 0) {
            LOG.trace("Reg new User " + regData.name);
            if (user == null) {                                // Если не нашли
                user = new User();                            // Создем
                user.setHashkey(hashKey);
                user.setName(regData.name);
                user.setLat(regData.lat);
                user.setLon(regData.lon);
                user.setQad(regData.qad);
                user.setIcon((byte) regData.ico);
                user.setLocale(regData.locale);
            }


            user.setStatus(gutil.STATUS_ACTIVE);            // Устанавличае что Юзер активен
            user.setChanel(ctxChanel.channel());                        // Сохраняем ссылку на Коннект в Юзере
            userDao.saveOrUpdate(user);                        // Сохраняем Юзера в МУСКЛ
            hashMapDB.add(user);                            // Сохраняем Юзера в Оперативной базе
            registrationAccept(ctxChanel, user);


        }


        // ********* ОБНОВЛЕНИЕ рег.данных ***********
        // Иначе он обновляет какие-то рег. данные. Пока это может быть только логин или иконка
        // Юзер знает правильный свой ИД и сессию.
        else if (ctxChanel.channel().attr(NettyServer.USER).get() != null && ctxChanel.channel().attr(NettyServer.USER).get().getId() == regData.u_id &&
                ctxChanel.channel().attr(NettyServer.SESSION).get() != null && ctxChanel.channel().attr(NettyServer.SESSION).get() == regData.sesion) {
            LOG.trace("Update new User " + regData.name);
            if (user == null) {                                // Если не нашли, все Норм, можно обновлять рег.данные
                LOG.debug("delete user= " + ctxChanel.channel().attr(NettyServer.USER).get());
                //userDao.delete(ctxChanel.channel().attr(NettyServer.USER).get());    // Удаляем старого юзера из МУСКЛ    // А зачем?
                hashMapDB.removeUser(ctxChanel.channel().attr(NettyServer.USER).get().getId());    // Удаляем из оперативной БД
                user = new User();                            // Создем
                user.setHashkey(hashKey);
                user.setName(regData.name);
                user.setLocale(regData.locale);
                user.setLat(regData.lat);
                user.setLon(regData.lon);
                user.setQad(regData.qad);
                user.setIcon((byte) regData.ico);
                user.setStatus(gutil.STATUS_ACTIVE);            // Устанавличае что Юзер активен
                user.setChanel(ctxChanel.channel());                    // Сохраняем ссылку на Коннект в Юзере
                userDao.saveOrUpdate(user);                        // Сохраняем Юзера в МУСКЛ
                hashMapDB.add(user);                            // Сохраняем Юзера в Оперативной базе
                LOG.trace("new user aded= " + user);
                registrationAccept(ctxChanel, user);


            } else {
                LOG.warn("User try to delete himself");
                return;
            }


        }

        // ********* НИ регистрация, НИ обновление -> Выход!
        else {
            LOG.error(" ERR!!! Undefined RegData getId=" + ctxChanel.channel().attr(NettyServer.USER).get() + " u_id=" + regData.u_id + " getSession=" + ctxChanel.channel().attr(NettyServer.SESSION).get() + " regData.ses=" + regData.sesion);
            return;
        }


        // ***********************
        // Запуск ГЕОКОДЕРА
        // ***********************
        // Бин Геокодера должен быть prototype (т.е. каждый вызов - новый бин), по этому тут каждый раз создается новый бин
        LOG.trace("Run GEOCODER " + user.getLastGeoDecode() + " " + user.getCountry() + " " + regData.lat + " " + regData.lon);
        if ((user.getLastGeoDecode() < (System.currentTimeMillis() - gutil.SETUP_GEODECODE_TIMEOUT) ||
                user.getCountry() == null ||
                user.getCity() == null) &&
                regData.lat != 0 &&
                regData.lon != 0) {
            ApplicationContext appCtx = ApplicationContextUtils.getApplicationContext();        // Статический метод получения контекста
            GeoDecoder multiGeoDecoder = (GeoDecoder) appCtx.getBean("geoDecoder");        // Создаем бин
            multiGeoDecoder.execute(user);                                            // Запускаем  ГеоДекодер в отдельном  потоке
        }


        // ***********************
        // Пересылка сохраненных сообщений
        // ***********************
        if (user.getUnRecivedMsg().size() > 0) {

            Set<Mesage> msgHashSet = user.getUnRecivedMsg();
            Iterator<Mesage> iterator = msgHashSet.iterator();
            while (iterator.hasNext()) {                                // Перебираем все недополоученные сообщения.
                final Mesage msgRMO = iterator.next();
                // Создаем и тут же заполняем новый пакет
                MsgToUser msgNetty = msgRMO.fillNettyPack((MsgToUser) PacketFactory.produce(PacketFactory.MSG_TO_USER));
                ctxChanel.writeAndFlush(msgNetty);    // отправляем сообщение вновь зареганному юзеру
            }
        }


        // ***********************
        // Пересылка сохраненных уведомлений
        // ***********************

        if (user.getUnRecivedNotif().size() > 0) {
            Set<Notific> notifHashSet = user.getUnRecivedNotif();
            Iterator<Notific> iterator = notifHashSet.iterator();
            while (iterator.hasNext()) {                                // Перебираем все недополоученные уведомления.
                Notific notifORM = iterator.next();
                // Создаем оповещение в формате Netty
                ServerStat serverStat = notifORM.fillNettyPack((ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT));
                ctxChanel.writeAndFlush(serverStat);        // Отправляем оповещение (БЕЗ слушателя)

            }
            userDao.saveOrUpdate(user);
        }


    }


    @Override
    public RegData validTele(Packet packet) {
        RegData regData = (RegData) packet;
        if (regData.an_id == 0 || regData.dev_id == 0 || regData.key_id == 0 || regData.status == 0 || regData.ico == 0 || regData.name.equals("")) {
            return null;
        }
        return regData;
    }


    private void registrationAccept(ChannelHandlerContext ctxChanel, User user) {
        ctxChanel.channel().attr(NettyServer.SESSION).set(new Random().nextInt());     // Устанавливаем Сессию
        ctxChanel.channel().attr(NettyServer.USER).set(user);    // Записываем в Коннект Юзера
        // Оповещаем Юзера
        ServerStat serverStat = (ServerStat) PacketFactory.produce(PacketFactory.SERVER_STAT);
        serverStat.typ = gutil.SC_REGACCEPT;
        serverStat.sts = ctxChanel.channel().attr(NettyServer.SESSION).get();
        serverStat.dat = user.getId();
        ctxChanel.writeAndFlush(serverStat);
        System.out.println("registrationAccept");
    }
}
