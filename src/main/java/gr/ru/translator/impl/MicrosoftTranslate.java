package gr.ru.translator.impl;

import com.google.common.collect.ImmutableMap;
import gr.ru.dao.User;
import gr.ru.netty.protokol.PacketFactory;
import gr.ru.translator.Engine;
import gr.ru.translator.Translator;
import gr.ru.utils.SaxParser;
import gr.ru.utils.TestHttpUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Map;

import static gr.ru.netty.protokol.Packs2Client.MsgToUser;

/**
 * Created by
 */
public class MicrosoftTranslate extends Thread implements Translator {

    private static volatile String token = "";
    private static volatile long tokenTime = 0;
    private final static long tokenIncrement = 1000 * 60 * 8;
    private final String headerKey = "Ocp-Apim-Subscription-Key";
    private final String key1 = "9cdf12fab33141c78ee369178bfbb7fd";
    private final String key2 = "16ba8aa99147425ba67488c71a8164a9";
    private final String tokenUrl = "https://api.cognitive.microsoft.com/sts/v1.0/issueToken";
    private final String translateUrl = "https://api.microsofttranslator.com/v2/http.svc/Translate";


    private String encodedMessage;
    private String from;
    private String to;
    private User user;
    private MsgToUser msgToUser;


    private synchronized void updateToken() {
        if (tokenTime + tokenIncrement > System.currentTimeMillis()) {
            return;
        }
        Map<String, String> headers = ImmutableMap.<String, String>builder()
                .put(headerKey, key1)
                .build();
        TestHttpUtil.HttpResponse httpResponse = TestHttpUtil.POST(tokenUrl, "", headers);
        tokenTime = System.currentTimeMillis();
        token = httpResponse.getBody();
        System.out.println("Update Microsoft Token");
    }


    @Override
    public void translate(String from, String to, String message, User user, MsgToUser msgToUser) {
        if (tokenTime + tokenIncrement < System.currentTimeMillis()) {
            updateToken();
        }

        this.encodedMessage = Engine.urlEncoder(message);
        this.from = from;
        this.to = to;
        this.user = user;
        this.msgToUser = msgToUser;
        this.start();

    }

    @Override
    public void run() {
        TestHttpUtil.HttpResponse httpResponse = TestHttpUtil.GET(translateUrl + "?" +
                "appid=Bearer%20" + token + "&" +
                "text=" + encodedMessage + "&" +
                "from=" + from + "&" +
                "to=" + to);

        msgToUser.msg = SaxParser.INSTANCE.parse(httpResponse.getBody(), "string");
        if (user.getMapChanel() != null && user.getMapChanel().isActive()) {
            user.getMapChanel().writeAndFlush(msgToUser);        // Отправляем сообщение (БЕЗ слушателя)
        }
    }


    public static class SAXPars extends DefaultHandler {
        String thisElement;

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            thisElement = qName;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (thisElement.equals("id")) {
                System.out.println(new String(ch, start, length));
                //doc.setId(new Integer(new String(ch, start, length)));
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            thisElement = "";
        }
    }

}
