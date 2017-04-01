package gr.ru.translator;

import gr.ru.dao.User;

/**
 * Created by
 */
public interface Translator {
    public void translate(String from, String to, String message, User useer);
}
