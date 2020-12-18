package com.klaus.zljhttplib;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/1/20 15:53
 * 外观
 */
public class ZljHttpRequest {

    public static ZljHttp.Builder get() {
        return new ZljHttp.Builder().get();
    }

    public static ZljHttp.Builder post() {
        return new ZljHttp.Builder().post();
    }

    public static ZljHttp.Builder delete() {
        return new ZljHttp.Builder().delete();
    }

    public static ZljHttp.Builder put() {
        return new ZljHttp.Builder().put();
    }

    public static ZljHttp.Builder upload() {
        return new ZljHttp.Builder();
    }

    public static ZljHttp.Builder download() {
        return new ZljHttp.Builder();
    }

}
