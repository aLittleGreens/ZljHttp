package com.klaus.zljhttplib.api;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/30/20 16:00
 */
public interface Api {

    /**
     * GET 请求
     *
     * @param url       api接口url
     * @param parameter 请求参数map
     * @param header    请求头map
     * @return
     */
    @GET
    Observable<JsonElement> get(@Url String url, @QueryMap Map<String, String> parameter, @HeaderMap Map<String, String> header);


    /**
     * 表单提交 这种能满足大部分的需求
     *
     * @param url       api接口url
     * @param parameter 请求参数map
     * @param header    请求头map
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<JsonElement> post(@Url String url, @FieldMap Map<String, String> parameter, @HeaderMap Map<String, String> header);


    /**
     * RequestBody--json数据提交
     *
     * @param requestBody 发送非表单数据，如String/JSON格式数据
     */
    @POST
    Observable<JsonElement> post(@Url String url, @Body RequestBody requestBody, @HeaderMap Map<String, String> header);


    /**
     * DELETE 请求
     *
     * @param url       api接口url
     * @param parameter 请求参数map
     * @param header    请求头map
     * @return
     */
    @DELETE
    Observable<JsonElement> delete(@Url String url, @QueryMap Map<String, String> parameter, @HeaderMap Map<String, String> header);


    /**
     * PUT 请求
     *
     * @param url       api接口url
     * @param parameter 请求参数map
     * @param header    请求头map
     * @return
     */
    @FormUrlEncoded
    @PUT
    Observable<JsonElement> put(@Url String url, @FieldMap Map<String, String> parameter, @HeaderMap Map<String, String> header);

    /**
     * 多文件上传
     *
     * @param url       api接口url
     * @param parameter 请求接口参数
     * @param header    请求头map
     * @param fileList  文件列表
     * @return
     * @Multipart 文件上传注解 multipart/form-data
     */
    @Multipart
    @POST
    Observable<JsonElement> upload(@Url String url, @PartMap Map<String, String> parameter, @HeaderMap Map<String, String> header, @Part List<MultipartBody.Part> fileList);


    /**
     * 下载
     * @param url
     * @param header
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url, @HeaderMap Map<String, String> header);

}
