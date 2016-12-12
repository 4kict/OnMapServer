package gr.ru.geocoder;


import com.google.gson.Gson;
import gr.ru.dao.User;
import gr.ru.dao.UserDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class GeoDecoder extends Thread {

    private static final Logger LOG = LogManager.getLogger(GeoDecoder.class);
    long unic_id;
    double lat, lon;
    private UserDAO userDao;
    private User user;
    private boolean bot = false;


//	public GeoDecoder(int _unic_id, int _lat, int _lon) {
//		unic_id = _unic_id;
//		lat = _lat * 1E-6;
//		lon = _lon * 1E-6;
//	}

//	public void execute(long _unic_id, int _lat, int _lon) {
//		unic_id = _unic_id;
//		lat = _lat * 1E-6;
//		lon = _lon * 1E-6;
//		System.out.println("GeoDecoder runing");
//		this.start();
//	}


    public void execute(User user) {
        LOG.trace("geoCodeer Start USer=" + user);
        this.user = user;
        this.lat = user.getLat() * 1E-6;
        this.lon = user.getLon() * 1E-6;
        this.start();
    }


    @Override
    public void run() {
        // �������� �� ����������� ������ � �����

        String country = "", city = "", area = "", area2 = "";
        //final String key = "AIzaSyAQQfyeNi9STBC8oZm0Bf19wU020hKE_zE";	//
        final String key = "AIzaSyDaO8r_c5vmpxhqv9tFQWlKSkW5pr-ca4I";    // новый

        final String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json?language=en&latlng=";
        String coord = lat + "," + lon;

        final String url = baseUrl + coord + "&key=" + key;
        LOG.trace(url);
        StringBuffer response = new StringBuffer();
        try {
            final InputStream is = new URL(url).openStream();
            final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String inputLine;
            while ((inputLine = rd.readLine()) != null) {
                response.append(inputLine);
            }
            is.close();
            rd.close();



        } catch (Exception e) {
            LOG.error("google GeoDecoder error: \n" + e);
            e.printStackTrace();
        }


        if (response.length() > 0) {
            Gson gson = new Gson();
            GeoCoderResponse googlResponse = gson.fromJson(response.toString(), GeoCoderResponse.class);

            if ("OK".equals(googlResponse.status)) {
                GeoCoderResponse.address_component[] addresParts = googlResponse.results[0].address_components;
                for (int i = 0; i < addresParts.length; i++) {
                    if (addresParts[i].types[0].equals("country"))
                        country = addresParts[i].long_name;
                    else if (addresParts[i].types[0].equals("locality"))
                        city = addresParts[i].long_name;
                    else if (addresParts[i].types[0].equals("administrative_area_level_1"))
                        area = addresParts[i].long_name;
                    else if (addresParts[i].types[0].equals("administrative_area_level_2"))
                        area2 = addresParts[i].long_name;
                }
            } else {
                LOG.error("google GeoDecoder response status = " + googlResponse.status);
            }
        }




//        if (area.equals("") && city.equals(""))
//            bestlocality = "";
//        else if (area.equals("") && !city.equals(""))
//            bestlocality = city;
//        else if (city == null && area != null)
//            bestlocality = area;
//        else {                                // (area!=null && city!=null)
//
//            bestlocality = city;
//        }


        if (city.equals(""))
            city = area;
        if (city.equals(""))
            city = area2;



        if (!city.equals("") && !country.equals("")) {
            LOG.info(city + " , " + country);
            user.setCity(city);
            user.setCountry(country);
            user.setLastGeoDecode(System.currentTimeMillis());
            //userDao.saveOrUpdate(user);
        }

        //updateGeoNames(unic_id, country, bestlocality );

    }


    public UserDAO getUserDao() {
        return userDao;
    }


    public void setUserDao(UserDAO userDao) {
        this.userDao = userDao;
    }


}











