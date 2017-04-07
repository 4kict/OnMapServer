package gr.ru.translator;

import gr.ru.dao.User;
import gr.ru.translator.impl.MicrosoftTranslate;
import gr.ru.translator.impl.YandexTranslate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static gr.ru.netty.protokol.Packs2Client.MsgToUser;

/**
 * Created by
 */
public enum Engine {
    YANDEX {
        @Override
        public void translate(String from, String to, String message, User user, MsgToUser msgToUser) {
            new YandexTranslate().translate(from, to, message, user, msgToUser);

        }
    },
    GOOGLE {
        @Override
        public void translate(String from, String to, String message, User user, MsgToUser msgToUser) {
            throw new UnsupportedOperationException();
        }
    },

    MICROSOFT {
        @Override
        public void translate(String from, String to, String message, User user, MsgToUser msgToUser) {
            new MicrosoftTranslate().translate(from, to, message, user, msgToUser);
        }
    };

    public abstract void translate(String from, String to, String message, User user, MsgToUser msgToUser);

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
