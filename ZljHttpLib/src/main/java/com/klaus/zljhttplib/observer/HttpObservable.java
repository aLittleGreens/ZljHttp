package com.klaus.zljhttplib.observer;

import android.util.Log;

import com.google.gson.JsonElement;
import com.klaus.zljhttplib.callback.DownloadCallback;
import com.klaus.zljhttplib.function.ServerResultFunction;
import com.klaus.zljhttplib.function.ThrowableResultFunction;
import com.klaus.zljhttplib.utils.ResponseUtils;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.RxDialogFragment;
import com.trello.rxlifecycle2.components.RxPreferenceFragment;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.BuildConfig;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/30/20 16:42
 * HTTP上游事件封装
 */
public class HttpObservable {
    private final LifecycleProvider lifecycle;
    private final ActivityEvent activityEvent;
    private final FragmentEvent fragmentEvent;
    private final HttpObserver observer;
    private final Observable<JsonElement> apiObservable;
    /* 下载的本地保存路径 */
    private final String saveFilePath;

    private HttpObservable(Builder builder) {
        this.lifecycle = builder.lifecycle;
        this.activityEvent = builder.activityEvent;
        this.fragmentEvent = builder.fragmentEvent;
        this.observer = builder.observer;
        this.apiObservable = builder.apiObservable;
        this.saveFilePath = builder.saveFilePath;
    }

    /**
     * 数据转换
     */
    private Observable<?> serverResultMap() {
        return apiObservable.map(new ServerResultFunction());
    }

    /**
     * Activity #onDestory
     * Fragment #onDestoryView
     */
    private Observable<?> compose(boolean noMapServerResult) {
        Observable<?> observable = apiObservable;
        if (!noMapServerResult) {
            observable = serverResultMap();
        }
        return bindLifecycle(observable);
    }

    private Observable<?> bindLifecycle(Observable<?> observable) {
        if (lifecycle != null) {
            if (activityEvent != null || fragmentEvent != null) {
                //两个同时存在,以 activity 为准
                if (activityEvent != null && fragmentEvent != null) {
                    return observable.compose(lifecycle.bindUntilEvent(activityEvent));
                }
                if (activityEvent != null) {
                    return observable.compose(lifecycle.bindUntilEvent(activityEvent));
                }
                if (fragmentEvent != null) {
                    return observable.compose(lifecycle.bindUntilEvent(fragmentEvent));
                }
            } else {
                if (lifecycle instanceof RxAppCompatActivity
                        || lifecycle instanceof RxFragmentActivity
                        || lifecycle instanceof RxActivity
                ) {
                    return observable.compose(lifecycle.bindUntilEvent(ActivityEvent.DESTROY));
                }
                if (lifecycle instanceof RxFragment
                        || lifecycle instanceof RxFragment
                        || lifecycle instanceof RxDialogFragment
                        || lifecycle instanceof com.trello.rxlifecycle2.components.support.RxDialogFragment
                        || lifecycle instanceof RxPreferenceFragment
                        || lifecycle instanceof RxAppCompatDialogFragment) {
                    return observable.compose(lifecycle.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
                }
                return observable.compose(lifecycle.bindToLifecycle());
            }
        }
        return observable;
    }

    /**
     * 异常捕获
     */
    private Observable<?> onErrorResumeNext(boolean noMapServerResult) {
        return compose(noMapServerResult).onErrorResumeNext(new ThrowableResultFunction<>());
    }

    /**
     * dispose拦截
     */
    public Observable<?> doOnDispose(boolean noMapServerResult) {
        if (observer != null) {
            return onErrorResumeNext(noMapServerResult).doOnDispose(new Action() {
                @Override
                public void run() throws Exception {
                    observer.onCanceled();
                }
            });
        }
        return onErrorResumeNext(noMapServerResult);
    }


    /**
     * 线程设置
     *
     * @param isMainThread 默认主线长接收
     */
    public void toSubscribe(boolean isMainThread) {
        Observable<?> observable = doOnDispose(false).subscribeOn(Schedulers.io());

        if (isMainThread) {
            observable = observable.observeOn(AndroidSchedulers.mainThread());
        }
        observable.subscribe(observer);
    }

    public void toMerge(boolean isMainThread) {
        Observable<?> observable = doOnDispose(true).subscribeOn(Schedulers.io());

        if (isMainThread) {
            observable = observable.observeOn(AndroidSchedulers.mainThread());
        }
        observable.subscribe(observer);
    }

    public void toDownload(boolean isMainThread) {
        DownloadCallback<?> downloadCallback;
        if (observer instanceof DownloadCallback) {
            downloadCallback = (DownloadCallback<?>) observer;
        } else {
            if (BuildConfig.DEBUG) {
                Log.e("HttpObservable", "observer not instanceof DownloadCallback");
            }
            return;
        }

        Observable observable = doOnDispose(true).subscribeOn(Schedulers.io());
        observable = observable.map((Function<ResponseBody, Object>) responseBody ->
                ResponseUtils.get().saveFile(responseBody, new File(saveFilePath), downloadCallback));
        if (isMainThread) {
            observable = observable.observeOn(AndroidSchedulers.mainThread());
        }
        observable.subscribe(downloadCallback);
    }


    /**
     * Builder
     * 构造Observable所需参数，按需设置
     */
    public static final class Builder {

        LifecycleProvider<?> lifecycle;
        ActivityEvent activityEvent;
        FragmentEvent fragmentEvent;
        HttpObserver observer;
        Observable apiObservable;
        /* 下载的本地保存路径 */
        String saveFilePath;

        public Builder(Observable apiObservable) {
            this.apiObservable = apiObservable;
        }

        public Builder httpObserver(HttpObserver observer) {
            this.observer = observer;
            return this;
        }

        public Builder lifecycleProvider(LifecycleProvider<?> lifecycle) {
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

        public Builder saveFilePath(String saveFilePath) {
            this.saveFilePath = saveFilePath;
            return this;
        }


        public HttpObservable build() {
            return new HttpObservable(this);
        }
    }


}
