package com.klaus.zljhttplib.load.download;


/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/07/20 18:08
 */
public interface DownloadProgressCallback {

    /**
     * 下载进度回调
     *
     * @param currentSize 当前值
     * @param progress   进度
     */
    void onProgress(long currentSize, int progress);

}
