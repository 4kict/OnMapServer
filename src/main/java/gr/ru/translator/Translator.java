package gr.ru.translator;

import gr.ru.dao.User;

import static gr.ru.netty.protokol.Packs2Client.MsgToUser;

/**
 * Created by
 */
public interface Translator {
    void translate(String from, String to, String message, User useer, MsgToUser msgToUser);
}
