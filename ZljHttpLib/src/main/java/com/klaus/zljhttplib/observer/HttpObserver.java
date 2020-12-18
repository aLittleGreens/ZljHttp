package com.klaus.zljhttplib.observer;


import android.content.Context;
import android.widget.Toast;

import com.klaus.zljhttplib.R;
import com.klaus.zljhttplib.app.BaseApplication;
import com.klaus.zljhttplib.callback.INetRequestCallBack;
import com.klaus.zljhttplib.cancel.RequestCancel;
import com.klaus.zljhttplib.cancel.RequestManagerImpl;
import com.klaus.zljhttplib.utils.NetworkUtils;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


/**
 * 适用Retrofit网络请求Observer(监听者)
 * 备注:
 * 1.重写onSubscribe，添加请求标识
 * 2.重写onError，移除请求
 * 4.重写cancel，取消请求
 * 3.重写onNext，移除请求
 */
public abstract class HttpObserver<T> implements Observer<T>, RequestCancel, INetRequestCallBack<T> {
    /*请求标识*/
    private Object mTag;

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (mTag != null) {
//            CrashReport.postCatchedException(new Exception(createErrorJson(), e));
            RequestManagerImpl.getInstance().remove(mTag);
        }
        onRequestFinish();
    }

    @Override
    public void onComplete() {
        /**
         * 由于LifecycleProvider取消监听直接截断事件发送，但是必定回调onComplete()
         * 因此在这里判断请求是否被取消，如果到这里还未被取消，说明是LifecycleProvider导致的取消请求，回调onCancel逻辑
         * 备注：
         * 1.子类重写此方法时需要调用super
         * 2.多个请求复用一个监听者HttpObserver时，tag会被覆盖，取消回调会有误
         */
        if (!RequestManagerImpl.getInstance().isDisposed(mTag)) {
            cancel();
        }
        onRequestFinish();
    }

    @Override
    public void onNext(@NonNull T value) {
        if (mTag != null) {
            RequestManagerImpl.getInstance().remove(mTag);
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        if (NetworkUtils.isNetworkConnected(BaseApplication.getBaseApplication())) {
            onRequestStart();
            if (mTag != null) {
                RequestManagerImpl.getInstance().add(mTag, d);
            }
        } else {
            onNotNet();
            onRequestFinish();
            if (!d.isDisposed()) {
                d.dispose();
            }
        }

    }

    @Override
    public void onNotNet() {
        onRequestFail();
        Context context = BaseApplication.getBaseApplication();
        Toast.makeText(context,R.string.network_unreachable, Toast.LENGTH_SHORT).show();
    }


    /**
     * 手动取消请求
     */
    @Override
    public void cancel() {
        if (mTag != null) {
            RequestManagerImpl.getInstance().cancel(mTag);
        }
    }

    /**
     * 是否已经处理
     *
     * @author ZhongDaFeng
     */
    public boolean isDisposed() {
        if (mTag == null) {
            return true;
        }
        return RequestManagerImpl.getInstance().isDisposed(mTag);
    }

    /**
     * 设置标识请求的TAG
     *
     * @param tag
     */
    public void setTag(Object tag) {
        this.mTag = tag;
    }

//    private String createErrorJson() {
//        String json = "";
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("network_type", NetworkUtils.getNetworkStateString(BaseApplication.getBaseApplication()));
//        if (mTag instanceof Integer) {
//            mTag = Integer.toHexString((Integer) mTag);
//        }
//        map.put("request_tag", mTag);
//        json = JsonUtils.toJson(map);
//        if (TextUtils.isEmpty(json)) {
//            json = "request_tag : " + mTag;
//        }
//        return json;
//    }


}
