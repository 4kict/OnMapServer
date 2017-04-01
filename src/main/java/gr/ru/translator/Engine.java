package gr.ru.translator;

import gr.ru.dao.User;
import gr.ru.translator.impl.MicrosoftTranslate;
import gr.ru.translator.impl.YandexTranslate;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by
 */
public enum Engine {
    YANDEX {
        @Override
        public void translate(String from, String to, String message, User user) {
            new YandexTranslate().translate(from, to, message, user);

        }
    },
    GOOGLE {
        @Override
        public void translate(String from, String to, String message, User user) {

        }
    },

    MICROSOFT {
        @Override
        public void translate(String from, String to, String message, User user) {
            new MicrosoftTranslate().translate(from, to, message, user);
        }
    };

    public abstract void translate(String from, String to, String message, User user);

    public static String urlEncoder(String msg) {
        try {
            return URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Log
            //throw new IllegalArgumentException();
            return null;
        }
    }
}
