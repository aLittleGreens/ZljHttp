package com.klaus.zljhttplib.cancel;

import androidx.collection.ArrayMap;

import java.util.Set;

import io.reactivex.disposables.Disposable;


/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/27/20 11:50
 */
public class RequestManagerImpl implements RequestManager<Object> {

    private static volatile RequestManagerImpl mInstance;
    private ArrayMap<Object, Disposable> mMaps;//处理,请求列表

    public static RequestManagerImpl getInstance() {
        if (mInstance == null) {
            synchronized (RequestManagerImpl.class) {
                if (mInstance == null) {
                    mInstance = new RequestManagerImpl();
                }
            }
        }
        return mInstance;
    }

    private RequestManagerImpl() {
        mMaps = new ArrayMap<>();
    }

    @Override
    public void add(Object tag, Disposable disposable) {
        mMaps.put(tag, disposable);
    }

    @Override
    public void remove(Object tag) {
        if (!mMaps.isEmpty()) {
            mMaps.remove(tag);
        }
    }

    @Override
    public void cancel(Object tag) {
        if (mMaps.isEmpty()) {
            return;
        }
        if (mMaps.get(tag) == null) {
            return;
        }
        if (!mMaps.get(tag).isDisposed()) {
            mMaps.get(tag).dispose();
        }
        mMaps.remove(tag);
    }

    @Override
    public void cancelAll() {
        if (mMaps.isEmpty()) {
            return;
        }
        //遍历取消请求
        Disposable disposable;
        Set<Object> keySet = mMaps.keySet();
        for (Object key : keySet) {
            disposable = mMaps.get(key);
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        mMaps.clear();
    }


    /**
     * 判断是否取消了请求
     *
     * @param tag
     * @return
     */
    public boolean isDisposed(Object tag) {
        if (mMaps.isEmpty() || mMaps.get(tag) == null) return true;
        return mMaps.get(tag).isDisposed();
    }
}
