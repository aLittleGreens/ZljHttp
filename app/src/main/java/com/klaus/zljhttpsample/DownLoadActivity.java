package com.klaus.zljhttpsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.klaus.zljhttplib.ZljHttpRequest;
import com.klaus.zljhttplib.callback.DownloadCallback;
import com.klaus.zljhttpsample.base.BaseActivity;

import java.io.File;

public class DownLoadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
    }

    private void download(){
        String filePath = "filename";
        String imgUrl = "";
//        showLoadingProgress();
        ZljHttpRequest.download()
                .apiUrl(imgUrl)
                .saveFilePath(filePath)
                .build()
                .execute(new DownloadCallback<File>() {

                    @Override
                    public void onProgress(long currentSize, int progress) {
                        Log.d(TAG, "currentSize:" + currentSize + " progress:" + progress);
                    }

                    @Override
                    public void onSuccess(File file) {
//                        showToastWithShort("图片已保存到手机");
                    }

                    @Override
                    public void onError(String code, String failMessage) {

                    }

                    @Override
                    public void onFail(String code, String failMessage) {
                    }

                    @Override
                    public void onRequestFinish() {
//                        hideLoadingProgress();
                    }
                });
    }
}