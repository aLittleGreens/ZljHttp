package com.klaus.zljhttplib.function;

import com.google.gson.JsonElement;
import com.klaus.zljhttplib.utils.JsonUtils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/30/20 15:42
 */
public class ServerResultFunction implements Function<JsonElement, String> {
    @Override
    public String apply(@NonNull JsonElement response) throws Exception {
        //打印服务器回传结果
//        Logger2.d("ServerResultFunction", "HttpResponse:" + response.toString());
        return JsonUtils.toJson(response);
    }
}