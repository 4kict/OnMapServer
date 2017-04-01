package gr.ru.translator;

import gr.ru.dao.User;
import gr.ru.translator.impl.YandexTranslate;
import gr.ru.utils.TestHttpUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by
 */
public class TranslateTest {

    static final String RU_TEXT = "У меня есть большая собака.Я луплю ее по ночам!\nА теперь лирическое отступление. Направление перевода можно задавать одним из следующих способов: GET или POST";

    @Before
    public void init() throws UnsupportedEncodingException {
        //System.out.println(RU_TEXT);
    }

    @Test
    public void googleTest() throws Exception {
//        long startTime = System.currentTimeMillis();
//        String ru_en = new GoogleTranslate().translate("ru", "en", RU_TEXT);
//        long finishTime = System.currentTimeMillis();
//        String en_ru = new GoogleTranslate().translate("en", "ru", ru_en);
//        System.out.println((finishTime - startTime) + " ====== google ============ \n" + ru_en + "\n" + en_ru);
    }

    @Test
    public void yandexTest() throws InterruptedException {
        User user = new User();

        new YandexTranslate().translate("ru", "en", RU_TEXT, user);
        TimeUnit.SECONDS.sleep(4);

        System.out.println( "====== yandex ============ \n" + user.getTrnslateMessage() );

        new YandexTranslate().translate("en", "ru", user.getTrnslateMessage(), user);
        TimeUnit.SECONDS.sleep(4);
        System.out.println( "====== yandex ============ \n" + user.getTrnslateMessage() );
    }


    public void geocoder() {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?language=en&latlng=55.627995,41.397824&key=AIzaSyBggZQG7bUv_eZpvxRp_8IsMhGgl_wuNxU";
        long startTime = System.currentTimeMillis();
        TestHttpUtil.HttpResponse httpResponse = TestHttpUtil.GET(url);
        Map<String, Object> bodyAsJson = httpResponse.getBodyAsJson();
        long finishTime = System.currentTimeMillis();
        System.out.println("geocoder: " + (finishTime - startTime));

    }


}