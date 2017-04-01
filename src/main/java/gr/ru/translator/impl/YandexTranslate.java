package gr.ru.translator.impl;


import gr.ru.dao.User;
import gr.ru.translator.Engine;
import gr.ru.translator.Translator;
import gr.ru.utils.TestHttpUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by
 */
public class YandexTranslate extends Thread implements Translator {
    private final String url = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private final String key = "trnsl.1.1.20170323T203204Z.c83c608bb0295911.17200f862022352cd84d104ced29c808e785e530";

    private String encodedMessage;
    private String from;
    private String to;
    private User user;

    public void translate(String from, String to, String message, User user) {
            this.encodedMessage = Engine.urlEncoder(message);
            this.from = from;
            this.to = to;
            this.user = user;
            this.start();
    }

    @Override
    public void run() {
        TestHttpUtil.HttpResponse httpResponse = TestHttpUtil.GET(url + "?" +
                "key=" + key + "&" +
                "text=" + encodedMessage + "&" +
                "lang=" + from + "-" + to);
        Map<String, Object> bodyAsJson = httpResponse.getBodyAsJson();

        if ((int) bodyAsJson.get("code") != 200) {
            //TODO LOG
            return;
        }

        user.setTrnslateMessage((String) ((ArrayList) bodyAsJson.get("text")).get(0));
    }
}
