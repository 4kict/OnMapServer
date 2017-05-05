package gr.ru.HashMapDB;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by
 */
public class Cluster {

    static public String calcName(int lat, int lon) {
        return format("%02d%02d", getLatQad(lat), getLonQad(lon));
    }


    public static short getLatQad(int latitude) {
        double lat = (double) latitude / 1000000.0D;
        double latZone = 0.0D;
        if (lat < 0.0D) {
            latZone = (90.0D + lat) / 8.0D + 1.0D;
        } else {
            latZone = lat / 8.0D + 11.0D;
        }
        return (short) latZone;
    }


    public static short getLonQad(int longitude) {
        return getLongQad((double) longitude / 1000000.0D);
    }

    private static short getLongQad(double longitude) {
        double longZone = 0.0D;
        if (longitude < 0.0D) {
            longZone = (180.0D + longitude) / 6.0D + 1.0D;
        } else {
            longZone = longitude / 6.0D + 31.0D;
        }
        return (short) longZone;
    }


    /**
     * Подсчет сколько квадратов в области видимости
     *
     * @return
     */
    public static int calcSquare(int neLat, int neLon, int swLat, int swLon) {
        //Preconditions.checkArgument(neLat > 0 && neLat < 23 && neLon > 0 && neLon < 61, "Wrong qudrant neLat=%s, neLon=%s",  neLat, neLon);
        //Preconditions.checkArgument(swLat > 0 && swLat < 23 && swLon > 0 && swLon < 61, "Wrong qudrant neLat=%s, neLon=%s", swLat, swLon);
        if (!(neLat > 0 && neLat < 23 && neLon > 0 && neLon < 61)) {
            return 0;
        }
        if (!(swLat > 0 && swLat < 23 && swLon > 0 && swLon < 61)) {
            return 0;
        }

        int lat;
        int lon;
        if (neLat < swLat) {
            lat = neLat + 22 - swLat + 1;
        } else {
            lat = neLat - swLat + 1;
        }
        if (neLon < swLon) {
            lon = neLon + 60 - swLon + 1;
        } else {
            lon = neLon - swLon + 1;
        }
        return lat * lon;
    }

    public static List<String> getNames(int neLat, int neLon, int swLat, int swLon) {
        List<String> result = new ArrayList<>();
        int lat = swLat;
        int lon = swLon;
        while (lat != neLat + 1) {
            while (lon != neLon + 1) {
                result.add(format("%02d%02d", lat, lon));
                lon++;
                if (lon > 60) {
                    lon = 1;
                }
            }
            lon = swLon;
            lat++;
            if (lat > 22) {
                lat = 1;
            }
        }
        return result;
    }

}
