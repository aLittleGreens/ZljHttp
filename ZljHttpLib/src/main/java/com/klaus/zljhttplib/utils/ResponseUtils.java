package com.klaus.zljhttplib.utils;



import com.klaus.zljhttplib.load.download.DownloadProgressCallback;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;


public class ResponseUtils {


    private static ResponseUtils instance = null;

    private ResponseUtils() {
    }

    public static ResponseUtils get() {
        if (instance == null) {
            synchronized (ResponseUtils.class) {
                if (instance == null) {
                    instance = new ResponseUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 将文件写入本地
     *
     * @return 写入完成的文件
     * @throws IOException IO异常
     */
    public File saveFile(ResponseBody responseBody, File file, DownloadProgressCallback progressCallback) throws IOException {
        byte[] buf = new byte[8 * 1024];
        int len;
        //初始化
        InputStream is = responseBody.byteStream();
        FileOutputStream fos = null;
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;

        try {
            //创建文件夹
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            long currentLength = 0;
            fos = new FileOutputStream(file);
            long totalLength = responseBody.contentLength();
            bufferedInputStream = new BufferedInputStream(is);
            bufferedOutputStream = new BufferedOutputStream(fos);
            while ((len = bufferedInputStream.read(buf)) != -1) {
                bufferedOutputStream.write(buf, 0, len);
                currentLength += len;
                //计算当前下载进度
                progressCallback.onProgress(totalLength, (int) (100 * currentLength / totalLength));
            }
            bufferedOutputStream.flush();
            return file;

        } finally {
            try {
                if (is != null) is.close();
                if (bufferedInputStream != null) bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
                if (bufferedOutputStream != null) bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
