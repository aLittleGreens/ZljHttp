package com.klaus.zljhttplib;

import com.google.gson.JsonElement;
import com.klaus.zljhttplib.utils.JsonUtils;

/**
 * http响应参数实体类
 */
public class Response {

    /**
     * 数据对象/成功返回对象
     */
    private JsonElement data;
    private String code;
    private String msg;

    public JsonElement getResult() {
        return data;
    }

    public void setResult(JsonElement result) {
        this.data = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return "1".equals(code);
    }
}
