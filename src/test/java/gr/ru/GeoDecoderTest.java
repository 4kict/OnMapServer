package gr.ru;

import com.google.gson.Gson;
import gr.ru.dao.MesagaDAO;
import gr.ru.dao.Mesage;
import gr.ru.dao.User;
import gr.ru.processData.ForwardedMsg;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Gri on 11.12.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/configSpring.xml"})
public class GeoDecoderTest {


    @Autowired
    MesagaDAO mesagaDAO;

    private ForwardedMsg forwardedMsg;

    @Test
    public void forwardedMsgTest(){

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

    @Test
    public void execute() throws Exception {


        long tStsrt = System.currentTimeMillis();
        // Запрос на сервер. Ответ в response
        final String key = "AIzaSyDaO8r_c5vmpxhqv9tFQWlKSkW5pr-ca4I";
        final String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json?language=en&latlng=";
        String coord = "55.9,37.7";
        final String url = baseUrl + coord + "&key=" + key;

        StringBuffer response = new StringBuffer();
        try {
            final InputStream is = new URL(url).openStream();
            final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            String inputLine;
            while ((inputLine = rd.readLine()) != null) {
                response.append(inputLine);
            }
            rd.close();
            long tEnd = System.currentTimeMillis();
            System.out.println("request time = " + (tEnd - tStsrt) + "ms");
        } catch (Exception e) {
            System.out.println("Google ERR");
        }


        Gson gson = new Gson();
//
//        if (response.length() > 0) {
//            GeoCoderResponse googlResponse = gson.fromJson(response.toString(), GeoCoderResponse.class);
//
//            if ("OK".equals(googlResponse.status)) {
//                String country = "", city = null, area = null, bestlocality = null;
//                GeoCoderResponse.address_component[] addresParts = googlResponse.results[0].address_components;
//                for (int i = 0; i < addresParts.length; i++) {
//                    if (addresParts[i].types[0].equals("country"))
//                        country = addresParts[i].long_name;
//                    else if (addresParts[i].types[0].equals("locality"))
//                        city = addresParts[i].long_name;
//                    else if (addresParts[i].types[0].equals("administrative_area_level_1"))
//                        area = addresParts[i].long_name;
//
//
//
//                }
//                System.out.println("country?=" + country + "   city?=" + city + "   area?="+area);
//
//                System.out.println(googlResponse.status);
//            } else {
//                System.out.println(googlResponse.status);
//            }
//
//        }


    }


}