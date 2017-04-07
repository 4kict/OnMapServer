package gr.ru.translator.impl;


import gr.ru.dao.User;
import gr.ru.translator.Translator;

import static gr.ru.netty.protokol.Packs2Client.MsgToUser;

/**
 * Created by
 */
public class GoogleTranslate implements Translator {

    @Override
    public void translate(String from, String to, String message, User user, MsgToUser msgToUser) {
        throw new UnsupportedOperationException();
    }
}
