package com.klaus.zljhttplib.callback;


import com.klaus.zljhttplib.ZljHttp;
import com.klaus.zljhttplib.function.HttpExceptionHandler;
import com.klaus.zljhttplib.observer.HttpObserver;
import com.klaus.zljhttplib.utils.ThreadUtils;

import io.reactivex.annotations.NonNull;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/30/20 17:59
 */
public abstract class BaseCallback<T> extends HttpObserver<T> {

    @Override
    public void onNext(@NonNull T value) {
        super.onNext(value);
        inSuccess(value);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        if (e instanceof HttpExceptionHandler.ResponseThrowable) {
            HttpExceptionHandler.ResponseThrowable exception = (HttpExceptionHandler.ResponseThrowable) e;
            onError(exception.errorCode + "", exception.message);
        } else {
            onError(HttpExceptionHandler.ERROR.UNKNOWN_ERROR + "", "未知错误");
        }
    }

    @Override
    public void onCanceled() {
        onCanceledLogic();
    }

    /**
     * 请求成功
     *
     * @param t
     */
    protected abstract void inSuccess(T t);


    /**
     * Http被取消回调处理逻辑
     */
    private void onCanceledLogic() {
        if (!ThreadUtils.isMainThread()) {
            ZljHttp.Configure.get().getHandler().post(() -> {
                onCancel();
                onRequestFinish();
            });
        } else {
            onCancel();
            onRequestFinish();
        }
    }


}
