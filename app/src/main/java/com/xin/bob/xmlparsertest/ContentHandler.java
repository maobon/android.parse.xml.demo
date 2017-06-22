package com.xin.bob.xmlparsertest;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by bob on 2017/6/23.
 * <p>
 * SAX parse handler
 */

public class ContentHandler extends DefaultHandler {

    private static final String TAG = "ContentHandler";

    private String nodeName;

    private StringBuilder temp;
    private StringBuilder humidity;
    private StringBuilder time;

    private IEndDocumentListener endDocumentListener;

    interface IEndDocumentListener {
        void done(String temp, String humidity, String time);
    }

    public void setEndDocumentListener(IEndDocumentListener endDocumentListener) {
        this.endDocumentListener = endDocumentListener;
    }

    @Override
    public void startDocument() throws SAXException {
        temp = new StringBuilder();
        humidity = new StringBuilder();
        time = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        nodeName = localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("temp".equals(nodeName)) {
            temp.append(ch, start, length);
        } else if ("humidity".equals(nodeName)) {
            humidity.append(ch, start, length);
        } else if ("time".equals(nodeName)) {
            time.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("sk".equals(localName)) {
            Log.d(TAG, "temp = " + temp.toString().trim());
            Log.d(TAG, "humidity = " + humidity.toString().trim());
            Log.d(TAG, "time = " + time.toString().trim());

            if (endDocumentListener != null) {
                endDocumentListener.done(temp.toString().trim(),
                        humidity.toString().trim(), time.toString().trim());
            }

            temp.setLength(0);
            humidity.setLength(0);
            time.setLength(0);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
