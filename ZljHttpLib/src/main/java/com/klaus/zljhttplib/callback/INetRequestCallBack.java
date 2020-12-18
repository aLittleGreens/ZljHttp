package com.klaus.zljhttplib.callback;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/4/20 15:27
 */
public interface INetRequestCallBack<T> {

    /**
     * 成功回调
     * 如果回调HttpCallBack没有设置范型，value会是JsonElement
     *
     * @param value / JsonElement
     */
    void onSuccess(T value);

    /**
     * 网络请求错误(404,服务器异常)
     */
    default void onError(String code, String desc) {
        onRequestFail();
    }

    /**
     * 网络请求成功, code 不等于 1,业务逻辑失败
     */
    default void onFail(String code, String desc) {
        onRequestFail();
    }

    /**
     * 取消回调
     */
    default void onCancel() {

    }

    /**
     * 无网络，不会触发网络请求
     */
    default void onNotNet() {
        onRequestFail();
    }


    /**
     * 请求开始
     */
    default void onRequestStart() {

    }

    /**
     * 请求结束
     */
    default void onRequestFinish() {

    }

    /**
     * 统一处理请求失败 3种情况都会回调
     * 1、onError 2、onFail、3、onNotNet
     */
    default void onRequestFail(){

    }


}
