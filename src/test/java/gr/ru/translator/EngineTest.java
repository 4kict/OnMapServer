package gr.ru.translator;

import gr.ru.dao.User;
import gr.ru.gutil;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.netty.protokol.Packs2Client;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by
 */
public class EngineTest {

    static final String RU_TEXT = "У меня есть большая собака.Я луплю ее по ночам!\nА теперь лирическое отступление. Направление перевода можно задавать одним из следующих способов: GET или POST";

//    @Test
//    public void yandexTranslate() throws Exception {
//        User user = new User();
//        Engine.YANDEX.translate("ru", "en", "БОльШой пес", user);
//        Engine.YANDEX.translate("ru", "en", "БОльШой кот", user);
//        Engine.YANDEX.translate("ru", "en", "Маленькая собачка", user);
//        Engine.YANDEX.translate("ru", "en", "Маленькая кошка", user);
//        TimeUnit.SECONDS.sleep(5);
//    }
//
//
    @Test
    public void microsoftTranslate() throws InterruptedException {

        Packs2Client.MsgToUser msgToUser = (Packs2Client.MsgToUser) PacketFactory.produce(PacketFactory.MSG_TO_USER);
        msgToUser.from = 1;
        msgToUser.msgtyp = gutil.MSG_TYP_TRANSLATE;
        msgToUser.unicId = 2;

        User user1 = new User();
        Engine.MICROSOFT.translate("ru", "en", "БОльШой пес", user1, msgToUser);
//        User user2 = new User();
//        Engine.MICROSOFT.translate("ru", "en", "БОльШой кот", user2);
//        User user3 = new User();
//        Engine.MICROSOFT.translate("ru", "en", "Маленькая собачка", user3);
//        User user4 = new User();
//        Engine.MICROSOFT.translate("ru", "en", "Маленькая кошка", user4);
//        TimeUnit.SECONDS.sleep(5);
//
//        Assert.assertEquals("Big dog", user1.getTrnslateMessage());
//        Assert.assertEquals("Big cat", user2.getTrnslateMessage());
//        Assert.assertEquals("Little doggy", user3.getTrnslateMessage());
//        Assert.assertEquals("Small cat", user4.getTrnslateMessage());

    }
}