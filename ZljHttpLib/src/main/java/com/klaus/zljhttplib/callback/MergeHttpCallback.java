package com.klaus.zljhttplib.callback;

import com.google.gson.JsonElement;
import com.klaus.zljhttplib.Response;
import com.klaus.zljhttplib.annotation.MergeType;
import com.klaus.zljhttplib.function.HttpExceptionHandler;
import com.klaus.zljhttplib.function.UserTokenManager;
import com.klaus.zljhttplib.utils.JsonUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/11/20 20:25
 */
public abstract class MergeHttpCallback<T> extends BaseCallback<T> {

    private boolean isSuccess;

    @Override
    protected void inSuccess(T t) {
        try {

            String[] jsonArray = (String[]) t;
            Class<?>[] classArray = getAnnotationType();

            if (classArray == null) {
                throw new IllegalArgumentException("please write Annotation MergeType on onSuccess()");
            }

            if(jsonArray.length != classArray.length){
                throw new IllegalArgumentException("jsonArray.length != observable length");
            }

            Map<Class<?>, Object> map = new HashMap<>();
            for (int i = 0; i < classArray.length; i++) {
                Class<?> aClass = classArray[i];
                Response response = JsonUtils.fromJson(jsonArray[i], Response.class);
                isSuccess = response.isSuccess();
                Object bean = onConvert(response, aClass);
                if (!isSuccess) {
                    break;
                }
                map.put(aClass, bean);
            }
            if (isSuccess) {
                onSuccess((T) map);
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
     * @return
     */
    private <M> M onConvert(Response response, Class<?> classKey) {
        /**
         * 接口响应数据格式如@Response
         * 根据业务封装:
         * 1. response.isSuccess() (code==0) 业务逻辑成功回调convert()=>onSuccess()，否则失败回调onError()
         * 2.统一处理接口逻辑 例如:code== -400/-500 token过期等等
         */
        M t = null;
        String code = response.getCode();
        String msg = response.getMsg();
        JsonElement result = response.getResult();
        if (isTokenExpire(code)) {
            //token过期，跳转登录页面重新登录(示例)
            UserTokenManager.getInstance().handleUserTokenExpire(code);
            onFail(code, msg);
        } else {
            if (response.isSuccess()) {//与服务器约定成功逻辑
                if (classKey != null) {
                    t = JsonUtils.fromJson(result, classKey);
                } else {
                    t = (M) result;
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


    private Class<?>[] getAnnotationType() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getName().equals("onSuccess")) {
                return getClassType(method);
            }
        }
        return null;
    }

    private Class<?>[] getClassType(Method method) {
        if (method == null) {
            return null;
        }
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        MergeType mergeType = method.getAnnotation(MergeType.class);
        return mergeType.value();
    }
}
