package gr.ru.HashMapDB;

import gr.ru.dao.User;
import gr.ru.gutil;

import java.util.Random;
import java.util.UUID;

/**
 * Created by
 */
// Строитель ботов
class BotBuilder {
    private String name = UUID.randomUUID().toString();        // логин
    private String country = UUID.randomUUID().toString();        // Страна
    private String city = UUID.randomUUID().toString();        // Город
    private String lang = "en";        // язык
    private Integer lat = 0;        // Координаты
    private Integer lon = 0;
    private Integer status = gutil.STATUS_HIDE;
    long id=0;




    public BotBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public BotBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public BotBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    public BotBuilder setCity(String city) {
        this.city = city;
        return this;
    }

    public BotBuilder setLat(Integer lat) {
        this.lat = lat;
        return this;
    }

    public BotBuilder setLon(Integer lon) {
        this.lon = lon;
        return this;
    }

    public BotBuilder setLat(Double lat) {
        lat *= 1000000;
        this.lat = lat.intValue();
        return this;
    }

    public BotBuilder setLon(Double lon) {
        lon *= 1000000;
        this.lon = lon.intValue();
        return this;
    }

    public User build() {
        User bot = new User();
        bot.setId(id);
        bot.setHashkey( new Random().nextLong());
        bot.setName(name);
        bot.setCountry(country);
        bot.setCity(city);
        bot.setLocale(lang);
        bot.setLat(lat);
        bot.setLon(lon);
        bot.setStatus(status);
        bot.bot = true;
        return bot;
    }
}
