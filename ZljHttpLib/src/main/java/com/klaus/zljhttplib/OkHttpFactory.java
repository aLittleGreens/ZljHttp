package com.klaus.zljhttplib;


import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/30/20 16:01
 */
public class OkHttpFactory {

    private static final String TAG = "OkHttpFactory";
    private static OkHttpFactory mInstance;
    private OkHttpClient mDownloadHttpClient;

    private OkHttpFactory() {

    }

    public static OkHttpFactory getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpFactory.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpFactory();
                }
            }
        }
        return mInstance;
    }


    public OkHttpClient getHttpClient() {
        if (mDownloadHttpClient == null) {
            Dispatcher mDispatcher = new Dispatcher();
            //同域名最大请求数
            mDispatcher.setMaxRequestsPerHost(10);
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .dispatcher(mDispatcher)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS);

            mDownloadHttpClient = builder.build();
        }
        return mDownloadHttpClient;
    }

}
