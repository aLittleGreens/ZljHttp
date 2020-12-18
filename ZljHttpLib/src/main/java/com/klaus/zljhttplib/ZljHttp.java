package com.klaus.zljhttplib;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.klaus.zljhttplib.api.Api;
import com.klaus.zljhttplib.callback.DownloadCallback;
import com.klaus.zljhttplib.callback.HttpCallback;
import com.klaus.zljhttplib.callback.MergeHttpCallback;
import com.klaus.zljhttplib.callback.UploadCallback;
import com.klaus.zljhttplib.cancel.RequestManagerImpl;
import com.klaus.zljhttplib.function.ServerResultFunction;
import com.klaus.zljhttplib.load.upload.UploadRequestBody;
import com.klaus.zljhttplib.observer.HttpObservable;
import com.klaus.zljhttplib.observer.HttpObserver;
import com.klaus.zljhttplib.retrofit.Method;
import com.klaus.zljhttplib.retrofit.RetrofitUtils;
import com.klaus.zljhttplib.utils.RequestUtils;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/30/20 10:39
 */
public class ZljHttp {

    /**
     * 请求方式
     */
    private Method method;
    /**
     * 请求参数
     */
    private Map<String, String> parameter;
    /**
     * 请求头
     */
    private Map<String, String> header;
    /**
     * 标识请求的TAG，最好用int类型，tag上报到bugly，比较好找
     */
    private final Object tag;
    /**
     * 基础URL
     */
    private final String baseUrl;
    /**
     * apiUrl （path）
     */
    private final String apiUrl;
    /**
     * String参数/json字符串
     */
    private final String bodyString;
    /**
     * 是否强制JSON格式
     */
    private final boolean isJson;
    private final LifecycleProvider<?> lifecycle;
    private final ActivityEvent activityEvent;
    private final FragmentEvent fragmentEvent;
    /**
     * 是否主线程接收，默认true
     */
    private final boolean isMainThread;
    /**
     * 文件map
     */
    private final Map<String, File> fileMap;
    /**
     * 下载的本地保存路径
     */
    private final String saveFilePath;

    /**
     * 合并Observable
     *
     * @param builder
     */
    Observable<String[]> mergeObservable;

    /*构造函数*/
    private ZljHttp(Builder builder) {
        this.parameter = builder.parameter;
        this.header = builder.header;
        this.lifecycle = builder.lifecycle;
        this.activityEvent = builder.activityEvent;
        this.fragmentEvent = builder.fragmentEvent;
        this.tag = builder.tag;
        this.baseUrl = builder.baseUrl;
        this.apiUrl = builder.apiUrl;
        this.isJson = builder.isJson;
        this.bodyString = builder.bodyString;
        this.method = builder.method;
        this.isMainThread = builder.isMainThread;
        this.fileMap = builder.fileMap;
        this.saveFilePath = builder.saveFilePath;
    }

    /*执行上传请求*/
    public void execute(UploadCallback<?> uploadCallback) {
        if (uploadCallback == null) {
            throw new NullPointerException("UploadCallback must not null!");
        } else {
            doUpload(uploadCallback);
        }
    }

    /**
     * 执行普通Http请求
     */
    public void execute(HttpCallback<?> httpCallback) {
        if (httpCallback == null) {
            throw new NullPointerException("HttpCallback must not null!");
        } else {
            doRequest(httpCallback);
        }
    }

    public void execute(DownloadCallback<?> downloadCallback) {
        if (downloadCallback == null) {
            throw new NullPointerException("HttpCallback must not null!");
        } else {
            doDownload(downloadCallback);
        }
    }

    public void executeMerge(MergeHttpCallback<?> mergeHttpCallback) {
        if (mergeHttpCallback == null) {
            throw new NullPointerException("mergeHttpCallback must not null!");
        } else {
            doMerge(mergeHttpCallback);
        }
    }

    private void doMerge(MergeHttpCallback<?> mergeHttpCallback) {
        if (mergeObservable == null) {
            throw new IllegalArgumentException("mergeObservable is null!!!");
        }
        setRequestTag(mergeHttpCallback);

        new HttpObservable.Builder(mergeObservable)
                .httpObserver(mergeHttpCallback)
                .lifecycleProvider(lifecycle)
                .activityEvent(activityEvent)
                .fragmentEvent(fragmentEvent)
                .build()
                .toMerge(isMainThread);

    }

    private void doDownload(DownloadCallback<?> downloadCallback) {
        setRequestTag(downloadCallback);
        /*header处理*/
        disposeHeader();

        /*请求方式处理*/
        Observable<?> apiObservable = getDownloadApiObservable();

        /* 被观察者 HttpObservable */
        new HttpObservable.Builder(apiObservable)
                .httpObserver(downloadCallback)
                .lifecycleProvider(lifecycle)
                .activityEvent(activityEvent)
                .fragmentEvent(fragmentEvent)
                .saveFilePath(saveFilePath)
                .build()
                .toDownload(isMainThread);


    }

    private Observable<?> getDownloadApiObservable() {
        Api apiService;
        Retrofit retrofit = RetrofitUtils.get().getRetrofit(RequestUtils.getBasUrl(apiUrl), OkHttpFactory.getInstance().getHttpClient());
        apiService = retrofit.create(Api.class);
        return apiService.download(apiUrl, header);
    }


    /**
     * 请求是否已经取消
     */
    public static boolean isCanceled(Object tag) {
        if (tag == null) {
            return true;
        }
        return RequestManagerImpl.getInstance().isDisposed(tag);
    }

    /**
     * 根据tag取消请求
     *
     * @param tag
     */
    public static void cancel(Object tag) {
        if (tag == null) {
            return;
        }
        RequestManagerImpl.getInstance().cancel(tag);
    }

    /**
     * 取消全部请求
     */
    public static void cancelAll() {
        RequestManagerImpl.getInstance().cancelAll();
    }


    private void doRequest(HttpCallback httpCallback) {
        setRequestTag(httpCallback);
        /*header处理*/
        disposeHeader();

        /*Parameter处理*/
        disposeParameter();

        /*请求方式处理*/
        Observable<?> apiObservable = getRequestApiObservable();

        /* 被观察者 HttpObservable */
        new HttpObservable.Builder(apiObservable)
                .httpObserver(httpCallback)
                .lifecycleProvider(lifecycle)
                .activityEvent(activityEvent)
                .fragmentEvent(fragmentEvent)
                .build()
                .toSubscribe(isMainThread);

        /* 观察者  httpObserver */
    }

    public Observable<String> call() {
        /*header处理*/
        disposeHeader();

        /*Parameter处理*/
        disposeParameter();

        /*请求方式处理*/
        Observable<JsonElement> apiObservable = getRequestApiObservable();

        return apiObservable.map(new ServerResultFunction());

    }

    private void setRequestTag(HttpObserver<?> httpCallback) {
        /*设置请求唯一标识*/
        httpCallback.setTag(tag == null ? String.valueOf(System.currentTimeMillis()) : tag);
    }


    /*执行文件上传*/
    private void doUpload(UploadCallback<?> uploadCallback) {

        setRequestTag(uploadCallback);
        /*header处理*/
        disposeHeader();

        /*Parameter处理*/
        disposeParameter();
        Observable<?> apiObservable = getUploadApiObservable(uploadCallback);

        new HttpObservable.Builder(apiObservable)
                .httpObserver(uploadCallback)
                .lifecycleProvider(lifecycle)
                .activityEvent(activityEvent)
                .fragmentEvent(fragmentEvent)
                .build()
                .toSubscribe(isMainThread);

    }

    private Observable<?> getUploadApiObservable(UploadCallback<?> uploadCallback) {
        /*处理文件集合*/
        List<MultipartBody.Part> fileList = new ArrayList<>();
        if (fileMap != null && fileMap.size() > 0) {
            int size = fileMap.size();
            int index = 1;
            File file;
            RequestBody requestBodya;
            for (String key : fileMap.keySet()) {
                file = fileMap.get(key);
                requestBodya = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), new UploadRequestBody(requestBodya, file, index, size, uploadCallback));
                fileList.add(part);
                index++;
            }
        }

        Api apiService;
        Retrofit retrofit = RetrofitUtils.get().getRetrofit(getBaseUrl(), OkHttpFactory.getInstance().getHttpClient());
        apiService = retrofit.create(Api.class);
        return apiService.upload(disposeApiUrl(), parameter, header, fileList);
    }

    /*获取基础URL*/
    private String getBaseUrl() {
        //如果没有重新指定URL则是用默认配置
        return TextUtils.isEmpty(baseUrl) ? Configure.get().getBaseUrl() : baseUrl;
    }

    /*ApiUrl处理*/
    private String disposeApiUrl() {
        return TextUtils.isEmpty(apiUrl) ? "" : apiUrl;
    }

    /*处理Header*/
    private void disposeHeader() {

        /*header空处理*/
        if (header == null) {
            header = new TreeMap<>();
        }

        if (!header.isEmpty()) {
            //处理header中文或者换行符出错问题
            for (String key : header.keySet()) {
                header.put(key, RequestUtils.getHeaderValueEncoded(header.get(key)));
            }
        }

    }

    /*处理 Parameter*/
    private void disposeParameter() {

        /*空处理*/
        if (parameter == null) {
            parameter = new TreeMap<>();
        }

    }

    /*处理ApiObservable*/
    private Observable<JsonElement> getRequestApiObservable() {

        Observable<JsonElement> apiObservable = null;

        /*是否JSON格式提交参数*/
        boolean hasBodyString = !TextUtils.isEmpty(bodyString);
        RequestBody requestBody = null;
        if (hasBodyString) {
            String mediaType = isJson ? "application/json; charset=utf-8" : "text/plain;charset=utf-8";
            requestBody = RequestBody.create(MediaType.parse(mediaType), bodyString);
        }

        /*Api接口*/

        Api apiService;
        Retrofit retrofit = RetrofitUtils.get().getRetrofit(getBaseUrl(), OkHttpFactory.getInstance().getHttpClient());
        apiService = retrofit.create(Api.class);

        /*未指定默认GET*/
        if (method == null) method = Method.GET;

        switch (method) {
            case GET:
                apiObservable = apiService.get(disposeApiUrl(), parameter, header);
                break;
            case POST:
                if (hasBodyString)
                    apiObservable = apiService.post(disposeApiUrl(), requestBody, header);
                else
                    apiObservable = apiService.post(disposeApiUrl(), parameter, header);
                break;
            case DELETE:
                apiObservable = apiService.delete(disposeApiUrl(), parameter, header);
                break;
            case PUT:
                apiObservable = apiService.put(disposeApiUrl(), parameter, header);
                break;
        }
        return apiObservable;
    }

    /**
     * Builder
     * 构造Request所需参数，按需设置
     */
    public static final class Builder {
        Method method;
        Map<String, String> parameter;
        Map<String, String> header;
        LifecycleProvider<?> lifecycle;
        ActivityEvent activityEvent;
        FragmentEvent fragmentEvent;
        Object tag;
        String baseUrl;
        String apiUrl;
        String bodyString;
        boolean isJson;
        boolean isMainThread = true;
        Map<String, File> fileMap;
        String saveFilePath;

        public Builder() {
        }

        public Builder saveFilePath(String loadFilePath) {
            this.saveFilePath = loadFilePath;
            return this;
        }

        /*文件集合*/
        public Builder file(IdentityHashMap<String, File> file) {
            this.fileMap = file;
            return this;
        }

        /*一个Key对应多个文件*/
        public Builder file(String key, List<File> fileList) {
            if (fileMap == null) {
                fileMap = new IdentityHashMap();
            }
            if (fileList != null && fileList.size() > 0) {
                for (File file : fileList) {
                    fileMap.put(new String(key), file);
                }
            }
            return this;
        }

        public Builder isMainThread(boolean isMainThread) {
            this.isMainThread = isMainThread;
            return this;
        }

        /*GET*/
        public Builder get() {
            this.method = Method.GET;
            return this;
        }

        /*POST*/
        public Builder post() {
            this.method = Method.POST;
            return this;
        }

        /*DELETE*/
        public Builder delete() {
            this.method = Method.DELETE;
            return this;
        }

        /*PUT*/
        public Builder put() {
            this.method = Method.PUT;
            return this;
        }

        /*基础URL*/
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /*API URL*/
        public Builder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        /* 增加 Parameter 不断叠加参数 包括基础参数 */
        public Builder addParameter(Map<String, String> parameter) {
            if (this.parameter == null) {
                this.parameter = new TreeMap<>();
            }
            this.parameter.putAll(parameter);
            return this;
        }

        /*设置 Parameter 会覆盖 Parameter 包括基础参数*/
        public Builder setParameter(Map<String, String> parameter) {
            this.parameter = parameter;
            return this;
        }

        /* 设置String 类型参数  覆盖之前设置  isJson:是否强制JSON格式    bodyString设置后Parameter则无效 */
        public Builder setBodyString(String bodyString, boolean isJson) {
            this.isJson = isJson;
            this.bodyString = bodyString;
            return this;
        }

        /* 增加 Header 不断叠加 Header 包括基础 Header */
        public Builder addHeader(Map<String, String> header) {
            if (this.header == null) {
                this.header = new TreeMap<>();
            }
            this.header.putAll(header);
            return this;
        }

        /*设置 Header 会覆盖 Header 包括基础参数*/
        public Builder setHeader(Map<String, String> header) {
            this.header = header;
            return this;
        }

        /*LifecycleProvider*/
        public Builder lifecycle(LifecycleProvider lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        public Builder activityEvent(ActivityEvent activityEvent) {
            this.activityEvent = activityEvent;
            return this;
        }

        public Builder fragmentEvent(FragmentEvent fragmentEvent) {
            this.fragmentEvent = fragmentEvent;
            return this;
        }

        /*tag*/
        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public ZljHttp build() {
            return new ZljHttp(this);
        }

        public Observable<String> call() {
            return new ZljHttp(this).call();
        }
    }


    /**
     * Configure配置
     */
    public static final class Configure {

        /*全局Handler*/
        Handler mHandler;

        /*请求基础路径*/
        String baseUrl;

        public ZljHttp.Configure baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public String getBaseUrl() {
            return baseUrl;
        }


        public static Configure get() {
            return Holder.holder;
        }

        public Handler getHandler() {
            return mHandler;
        }

        private static class Holder {
            private static final Configure holder = new Configure();
        }

        private Configure() {
            this.mHandler = new Handler(Looper.getMainLooper());
        }

    }

    /**
     * 目前最多支持5个合并请求,可扩展9个
     *
     * @param observables 上游请求数据
     */
    @SafeVarargs
    public final ZljHttp zip(Observable<String>... observables) {
        int length = observables.length;
        if (length <= 1) {
            throw new IllegalArgumentException("参数长度异常");
        }
        if (length == 2) {
            mergeObservable = Observable.zip(observables[0], observables[1], (s, s2) -> RequestUtils.parse(s, s2));
        } else if (length == 3) {
            mergeObservable = Observable.zip(observables[0], observables[1], observables[2], (s, s2, s3) -> RequestUtils.parse(s, s2, s3));
        } else if (length == 4) {
            mergeObservable = Observable.zip(observables[0], observables[1], observables[2], observables[3], (s, s2, s3, s4) -> RequestUtils.parse(s, s2, s3, s4));
        } else if (length == 5) {
            mergeObservable = Observable.zip(observables[0], observables[1], observables[2], observables[3], observables[4], (s, s2, s3, s4, s5) -> RequestUtils.parse(s, s2, s3, s4, s5));
        } else {
            throw new IllegalArgumentException("合并还没扩展到6个");
        }
        return this;
    }

}
