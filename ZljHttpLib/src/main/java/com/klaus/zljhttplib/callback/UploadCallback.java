package com.klaus.zljhttplib.callback;



import com.klaus.zljhttplib.load.upload.UploadProgressCallback;

import java.io.File;


/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/01/20 11:05
 */
public abstract class UploadCallback<T> extends HttpCallback<T> implements UploadProgressCallback {

    @Override
    public void onProgress(File file, long currentSize, long totalSize, float progress, int currentIndex, int totalFile) {

    }


}
