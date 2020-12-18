package com.klaus.zljhttplib.app;

import android.app.Application;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/17/20 20:02
 */
public class BaseApplication extends Application {

    private static BaseApplication sBaseApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        sBaseApplication = this;

    }

    public static BaseApplication getBaseApplication() {
        return sBaseApplication;
    }
}
