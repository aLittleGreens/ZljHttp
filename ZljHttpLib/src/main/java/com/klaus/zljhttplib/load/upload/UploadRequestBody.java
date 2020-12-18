package com.klaus.zljhttplib.load.upload;


import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 12/04/20 17:50
 * 上传RequestBody
 */
public class UploadRequestBody extends RequestBody {

    //实际的待包装请求体
    private final RequestBody requestBody;
    //进度回调接口
    private final UploadProgressCallback progressCallback;
    //源文件
    private File file;
    //当前下标
    private int currentIndex;
    //上传总文件个数
    private int totalFileCount;


    public UploadRequestBody(RequestBody requestBody, File file, int currentIndex, int totalFileCount, UploadProgressCallback progressCallback) {
        this.file = file;
        this.currentIndex = currentIndex;
        this.totalFileCount = totalFileCount;
        this.requestBody = requestBody;
        this.progressCallback = progressCallback;
    }

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    /**
     * 重写writeTo
     *
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        //由于拦截器中调用了RequestBody.writeto(),导致多次回调writeto方法。
        if (sink instanceof Buffer) {
            requestBody.writeTo(sink);
            return;
        }

        ProgressSink progressSink = new ProgressSink(sink);
        BufferedSink bufferedSink = Okio.buffer(progressSink);
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }


    protected final class ProgressSink extends ForwardingSink {
        //当前写入字节数
        long writtenBytesCount = 0L;
        //总字节长度，避免多次调用contentLength()方法
        long totalBytesCount = 0L;

        public ProgressSink(Sink delegate) {
            super(delegate);
        }


        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            //增加当前写入的字节数
            writtenBytesCount += byteCount;
            //获得contentLength的值，后续不再调用
            if (totalBytesCount == 0) {
                totalBytesCount = contentLength();
            }
            //回调接口
            if (progressCallback != null) {
//                    ZljHttp.Configure.get().getHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
                float progress = (float) writtenBytesCount / (float) totalBytesCount;
//                Logger2.d(TAG, "file:" + file.getName() + " currentSize:" + writtenBytesCount + " totalSize:" + totalBytesCount + " progress:" + progress + " currentIndex:" + currentIndex + " totalFile:" + totalFileCount);
                progressCallback.onProgress(file, writtenBytesCount, totalBytesCount, progress, currentIndex, totalFileCount);
//                        }
//                    });
            }
        }
    }

}
