package gr.ru;

import com.esotericsoftware.jsonbeans.JsonReader;
import com.esotericsoftware.jsonbeans.JsonValue;
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
	private boolean bot=false;
	

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
		LOG.trace("geoCodeer Start USer="+user );
		this.user = user;
		this.lat = user.getLat() * 1E-6;
		this.lon = user.getLon() * 1E-6;
		this.start();
	}
	

	
	
	
	
	

	@Override
	public void run() {
		// �������� �� ����������� ������ � ����� 

		String country=null, city=null, area=null, bestlocality=null;		
		//final String key = "AIzaSyAQQfyeNi9STBC8oZm0Bf19wU020hKE_zE";	// 
		final String key = "AIzaSyDaO8r_c5vmpxhqv9tFQWlKSkW5pr-ca4I";	// новый
		 
		final String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
		String coord = lat+","+lon;

		final String url = baseUrl + coord+"&key="+key; 
		LOG.trace(url);
		try {
			final InputStream is = new URL(url).openStream();
			final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = rd.readLine()) != null) {
				response.append(inputLine);
			}

			JsonValue root = new JsonReader().parse(response.toString());
			JsonValue status=root.get("status");

			if (status.asString().equals("OK")){
				
				for (JsonValue entry = root.get("results").child().get("address_components").child(); entry != null; entry = entry.next())
				{
					//System.out.println("@"+ entry.get("types").get(0) + " = "  + entry.get("long_name") +"  ||  " + entry.get("short_name") );
					if (entry.get("types").child().toString().equals("country") ) 
						country = entry.getString("long_name").toString();
					else if (entry.get("types").child().toString().equals("administrative_area_level_1") ) 
						area = entry.getString("long_name").toString();
					else if (entry.get("types").child().toString().equals("locality") ) 
						city = entry.getString("long_name").toString();
					

				}
			}
			else{
				LOG.error("unknown locality");
			}

			is.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		

		LOG.debug("GEOCODER country="+country+" area="+area+" city="+city+" ");

		if 		(area==null && city==null)
			bestlocality=null;
		else if (area==null && city!=null)
			bestlocality = city;
		else if (city==null && area!=null)
			bestlocality = area;
		else{								// (area!=null && city!=null)

			bestlocality = city;
		}

		
		LOG.info( " country="+country + "   bestlocality=" + bestlocality );


		if ( bestlocality!=null  ||  country!=null  ){		
			LOG.info(bestlocality + " , " + country);
			user.setCity(bestlocality);
			user.setCountry(country );
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











