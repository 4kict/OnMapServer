package gr.ru.translator;

import gr.ru.dao.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by
 */
public class EngineTest {

    static final String RU_TEXT = "У меня есть большая собака.Я луплю ее по ночам!\nА теперь лирическое отступление. Направление перевода можно задавать одним из следующих способов: GET или POST";

    @Test
    public void yandexTranslate() throws Exception {
        User user = new User();
        Engine.YANDEX.translate("ru", "en", "БОльШой пес", user);
        Engine.YANDEX.translate("ru", "en", "БОльШой кот", user);
        Engine.YANDEX.translate("ru", "en", "Маленькая собачка", user);
        Engine.YANDEX.translate("ru", "en", "Маленькая кошка", user);
        TimeUnit.SECONDS.sleep(5);
    }


    @Test
    public void microsoftTranslate() throws InterruptedException {
        User user1 = new User();
        Engine.MICROSOFT.translate("ru", "en", "БОльШой пес", user1);
        User user2 = new User();
        Engine.MICROSOFT.translate("ru", "en", "БОльШой кот", user2);
        User user3 = new User();
        Engine.MICROSOFT.translate("ru", "en", "Маленькая собачка", user3);
        User user4 = new User();
        Engine.MICROSOFT.translate("ru", "en", "Маленькая кошка", user4);
        TimeUnit.SECONDS.sleep(5);

        Assert.assertEquals("Big dog", user1.getTrnslateMessage());
        Assert.assertEquals("Big cat", user2.getTrnslateMessage());
        Assert.assertEquals("Little doggy", user3.getTrnslateMessage());
        Assert.assertEquals("Small cat", user4.getTrnslateMessage());

//        Engine.MICROSOFT.translate("ru", "en", "БОльШой пес", user1);
//        Engine.MICROSOFT.translate( "en", "ru", "BigData", user2);
//        Engine.MICROSOFT.translate( "en", "ru", "Big Data", user2);
//        Engine.MICROSOFT.translate( "en", "ru", "Orange sdfevo", user2);
//        Engine.MICROSOFT.translate( "en", "ru", "Пвывщум", user2);


    }
}