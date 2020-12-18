package com.klaus.zljhttplib.helper;


/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/30/20 18:10
 */
public interface ParseHelper<T> {
    /*解析数据*/
    T parse(String data);
}
