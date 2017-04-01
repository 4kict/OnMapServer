package gr.ru;

import com.google.gson.Gson;
import gr.ru.dao.MesagaDAO;
import gr.ru.dao.Mesage;
import gr.ru.dao.User;
import gr.ru.geocoder.GeoDecoder;
import gr.ru.processData.ForwardedMsg;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

/**
 * Created by Gri on 11.12.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/configSpring.xml"})
public class GeoDecoderTest {


    @Autowired
    MesagaDAO mesagaDAO;
    @Autowired
    GeoDecoder geoDecoder;

    @Test
    public void geoCoderTest(){
        ApplicationContext appCtx = ApplicationContextUtils.getApplicationContext();        // Статический метод получения контекста



        User germany = new User();
        germany.setLat(51215049);
        germany.setLon(13969932);
        User france = new User();
        france.setLat(48830715);
        france.setLon(2394496);
        User italy = new User();
        italy.setLat(42480658);
        italy.setLon(12600065);
        User russia = new User();
        russia.setLat(55627995);
        russia.setLon(41397824);

        GeoDecoder multiGeoDecoder1 = (GeoDecoder) appCtx.getBean("geoDecoder");        // Создаем бин
        multiGeoDecoder1.setKey("AIzaSyBggZQG7bUv_eZpvxRp_8IsMhGgl_wuNxU");
        GeoDecoder multiGeoDecoder2 = (GeoDecoder) appCtx.getBean("geoDecoder");        // Создаем бин
        multiGeoDecoder2.setKey("AIzaSyBggZQG7bUv_eZpvxRp_8IsMhGgl_wuNxU");
        GeoDecoder multiGeoDecoder3 = (GeoDecoder) appCtx.getBean("geoDecoder");        // Создаем бин
        multiGeoDecoder3.setKey("AIzaSyBggZQG7bUv_eZpvxRp_8IsMhGgl_wuNxU");
        GeoDecoder multiGeoDecoder4 = (GeoDecoder) appCtx.getBean("geoDecoder");        // Создаем бин
        multiGeoDecoder4.setKey("AIzaSyBggZQG7bUv_eZpvxRp_8IsMhGgl_wuNxU");

        long startTime = System.currentTimeMillis();

        multiGeoDecoder1.execute(russia);
        multiGeoDecoder2.execute(germany);
        multiGeoDecoder3.execute(italy);
        multiGeoDecoder4.execute(france);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        assertEquals("Russia", russia.getCountry());
        assertEquals("Italy", italy.getCountry());
        assertEquals("France", france.getCountry());
        assertEquals("Germany", germany.getCountry());

        System.out.println("Russia  " + (russia.getLastGeoDecode() - startTime));
        System.out.println("Italy   " + (italy.getLastGeoDecode() - startTime));
        System.out.println("France  " + (france.getLastGeoDecode() - startTime));
        System.out.println("Germany " + (germany.getLastGeoDecode() - startTime));


    }

    @Test
    public void saveMessageWithoutUserTest() {
        Assert.notNull(mesagaDAO);
        Mesage mesage = new Mesage();
        mesage.setAutorId(123L);
        mesage.setLocalRowId(321L);
        mesage.setMesaga("some text");
        mesage.setMsgType((short) 1);
        mesage.setTime(9999L);
        User user = new User();
        user.setId(1119L);
        mesage.setUserRecipient(user);
        try{
            mesagaDAO.saveMesaga(mesage);
        }catch (ConstraintViolationException e){
            System.out.println("Fuck off!!!");
        }


        String locale = "";
        System.out.println(locale.getBytes());

    }


    public void execute() throws Exception {

        // Запрос на сервер. Ответ в response
        final String key = "AIzaSyDaO8r_c5vmpxhqv9tFQWlKSkW5pr-ca4I";
        final String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json?language=en&latlng=";
        String coord = "55.9,37.7";
        final String url = baseUrl + coord + "&key=" + key;

        StringBuffer response = new StringBuffer();
        try {
            long tStsrt = System.currentTimeMillis();
            final InputStream is = new URL(url).openStream();
            final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            long tEnd = System.currentTimeMillis();
            String inputLine;
            while ((inputLine = rd.readLine()) != null) {
                response.append(inputLine);
            }
            rd.close();

            System.out.println("request time = " + (tEnd - tStsrt) + "ms");
        } catch (Exception e) {
            System.out.println("Google ERR");
        }

    }




}