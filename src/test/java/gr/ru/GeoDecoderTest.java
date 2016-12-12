package gr.ru;

import com.google.gson.Gson;
import gr.ru.geocoder.GeoCoderResponse;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Gri on 11.12.2016.
 */
public class GeoDecoderTest {

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