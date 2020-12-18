package com.klaus.zljhttpsample.app;

import com.klaus.zljhttplib.ZljHttp;
import com.klaus.zljhttplib.app.BaseApplication;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/17/20 20:26
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ZljHttp.Configure.get().baseUrl("Https://www.baidu.com");
    }
}
