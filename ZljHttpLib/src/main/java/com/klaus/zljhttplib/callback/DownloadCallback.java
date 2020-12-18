package com.klaus.zljhttplib.callback;


import com.klaus.zljhttplib.load.download.DownloadProgressCallback;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/7/20 18:17
 */
public abstract class DownloadCallback<T> extends BaseCallback<T> implements DownloadProgressCallback {

    @Override
    public void onProgress(long currentSize, int progress) {

    }

    @Override
    protected void inSuccess(T t) {
        onSuccess(t);
    }

}
