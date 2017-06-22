package com.xin.bob.xmlparsertest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bob on 2017/6/22.
 * Use OKHTTP request data
 */

public class RequestDataThread extends Thread {

    private IStatusListener listener;

    interface IStatusListener {
        void response(String resData);
    }

    public void setListener(IStatusListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            if (listener == null) return;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://v.juhe.cn/weather/index?format=2&cityname=北京&key=2e1c3cc55294df2f04c5cb866ac10f03&dtype=xml")
                    .build();
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();

            listener.response(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            listener.response(e.toString());
        }
    }
}
