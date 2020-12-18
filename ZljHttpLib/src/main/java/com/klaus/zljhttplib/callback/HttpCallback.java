package com.klaus.zljhttplib.callback;


import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.klaus.zljhttplib.Response;
import com.klaus.zljhttplib.function.HttpExceptionHandler;
import com.klaus.zljhttplib.function.UserTokenManager;
import com.klaus.zljhttplib.helper.ParseHelper;
import com.klaus.zljhttplib.utils.JsonUtils;
import com.klaus.zljhttplib.utils.ParameterUtils;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/30/20 18:05
 */
public abstract class HttpCallback<T> extends BaseCallback<T> implements ParseHelper<T> {

    private Response response;

    @Override
    public T parse(String data) {
        return onConvert(data);
    }

    @Override
    public void inSuccess(T value) {
        try {
            T result = parse((String) value);
            if (isBusinessOk()) {
                onSuccess(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(HttpExceptionHandler.ERROR.PARSE_ERROR + "", "解析数据出错:" + e.getMessage());
            onRequestFinish();
        }

    }


    /**
     * 数据转换/解析数据
     *
     * @param data
     * @return
     */
    private T onConvert(String data) {
        /**
         * 接口响应数据格式如@Response
         * 根据业务封装:
         * 1. response.isSuccess() (code==0) 业务逻辑成功回调convert()=>onSuccess()，否则失败回调onError()
         * 2.统一处理接口逻辑 例如:code== -400/-500 token过期等等
         */
        T t = null;
        response = JsonUtils.fromJson(data, Response.class);
        String code = response.getCode();
        String msg = response.getMsg();
        JsonElement result = response.getResult();
        if (isTokenExpire(code)) {
            //token过期，跳转登录页面重新登录(示例)
            UserTokenManager.getInstance().handleUserTokenExpire(code);
            onFail(code, msg);
        } else {
            if (response.isSuccess()) {//与服务器约定成功逻辑
                Type typeClass = getTypeClass();
                if (typeClass != null) {
                    t = JsonUtils.fromJson(result, typeClass);
                } else {
                    t = (T) result;
                }
            } else {
                onFail(code, msg);
            }
        }
        return t;
    }

    private boolean isTokenExpire(String code) {
        return "-400".equals(code) || "-500".equals(code);
    }

    /**
     * 业务逻辑是否成功
     *
     * @return
     */
    private boolean isBusinessOk() {
        return response.isSuccess();
    }


    @Nullable
    private Type getTypeClass() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return ParameterUtils.getParameterUpperBound(0, (ParameterizedType) type);
        }
        return null;
    }

}
