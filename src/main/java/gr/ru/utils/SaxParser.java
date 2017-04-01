package gr.ru.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Синглтон.
 */
public class SaxParser {

    static SAXParser saxParser;
    private String tag;
    private String result;

    public static final SaxParser INSTANCE = new SaxParser();

    private SaxParser() {
        try {
            saxParser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            //TODO log
            e.printStackTrace();
        }
    }

    public SaxParser getInstande() {
        return INSTANCE;
    }


    public synchronized String parse(String xml, String tag) {
        this.tag = tag;
        try {
            saxParser.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), handler);
        } catch (SAXException | IOException e) {
            //TODO log
            e.printStackTrace();
        }
        return result;
    }


    private DefaultHandler handler = new DefaultHandler() {
        String thisElement;

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            thisElement = qName;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (thisElement.equals("string")) {
                result = new String(ch, start, length);
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            thisElement = "";
        }
    };

}
