package com.klaus.zljhttplib.load.upload;

import java.io.File;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/04/20 17:50
 */
public interface UploadProgressCallback {

    /**
     * 上传进度回调
     *
     * @param currentSize      当前值
     * @param totalSize        总大小
     * @param progress         进度
     * @param currentFileIndex 当前下标
     * @param totalFileCount   总文件数
     */
    void onProgress(File file, long currentSize, long totalSize, float progress, int currentFileIndex, int totalFileCount);
}
