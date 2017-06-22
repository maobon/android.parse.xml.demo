package com.xin.bob.xmlparsertest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private TextView tvResponse;
    private TextView tvParseResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResponse = (TextView) findViewById(R.id.tv_response);
        tvParseResult = (TextView) findViewById(R.id.btn_parse_result);
        Button btnPull = (Button) findViewById(R.id.btn_pull);
        btnPull.setOnClickListener(this);
        Button btnSax = (Button) findViewById(R.id.btn_sax);
        btnSax.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pull:
                RequestDataThread thread = new RequestDataThread();
                thread.setListener(new RequestDataThread.IStatusListener() {
                    @Override
                    public void response(final String resData) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResponse.setText(resData);
                                parseXMLUsePull(resData); // 解析XML
                            }
                        });
                    }
                });
                thread.start();
                break;

            case R.id.btn_sax:
                RequestDataThread requestData = new RequestDataThread();
                requestData.setListener(new RequestDataThread.IStatusListener() {
                    @Override
                    public void response(final String resData) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResponse.setText(resData);
                                parseXMLUseSAX(resData);
                            }
                        });

                    }
                });
                requestData.start();
                break;
        }
    }

    /**
     * Pull 解析
     * <p>
     * 声明返回数字0  START_DOCUMENT
     * 结束返回数字1  END_DOCUMENT
     * <p>
     * 开始标签返回数字2  START_TAG
     * 结束标签返回数字3  END_TAG
     * 文本返回数字4  TEXT
     *
     * @param xmlData String
     */
    private void parseXMLUsePull(String xmlData) {
        String temp = "";
        String humidity = "";
        String time = "";

        try {
            // 获得XmlPull解析器
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData)); // 读取目标文件

            // 事件类型
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                // 只要不是文档结束标签 就一直循环遍历
                // 循环遍历中个先获取节点名称
                String nodeName = parser.getName();

                // <temp> 开始标签
                // </temp> 结束标签
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 开始标签
                        if ("temp".equals(nodeName)) {
                            temp = parser.nextText();
                        } else if ("humidity".equals(nodeName)) {
                            humidity = parser.nextText();
                        } else if ("time".equals(nodeName)) {
                            time = parser.nextText();
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        // 结束标签
                        // 每当解析完成一个 "sk" 标签 就输出解析结果
                        if ("sk".equals(nodeName)) {
                            Log.d(TAG, "temp = " + temp);
                            Log.d(TAG, "humidity = " + humidity);
                            Log.d(TAG, "time = " + time);
                        }
                        break;
                }

                // 下一个事件(标签)
                eventType = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            tvParseResult.setText("温度: " + temp + "\n湿度: " + humidity + "\n时间: " + time);
        }

    }

    /**
     * SAX 解析
     *
     * @param xmlData String
     */
    private void parseXMLUseSAX(String xmlData) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();

            // 将自己的handler设置到reader上
            ContentHandler handler = new ContentHandler();

            // 返回数据(根据情况自定)
            handler.setEndDocumentListener(new ContentHandler.IEndDocumentListener() {
                @Override
                public void done(String temp, String humidity, String time) {
                    tvParseResult.setText("温度: " + temp + "\n湿度: " + humidity + "\n时间: " + time);
                }
            });
            xmlReader.setContentHandler(handler);

            // reader开始执行解析 注入目标XML内容
            xmlReader.parse(new InputSource(new StringReader(xmlData)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
