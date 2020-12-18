package com.klaus.zljhttpsample;

import android.os.Bundle;
import android.util.Log;

import com.klaus.zljhttplib.ZljHttpRequest;
import com.klaus.zljhttplib.callback.UploadCallback;
import com.klaus.zljhttpsample.base.BaseActivity;
import com.klaus.zljhttpsample.bean.UploadBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
    }

    private void upload(){
            //要上传的文件集合
        List<File> fileList = new ArrayList<>();

        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("request_id", String.valueOf(1));

        ZljHttpRequest.upload()
                .lifecycle(this)
                .apiUrl("api/common/upload_file")
                .addParameter(paramsMap)
                .file("file", fileList)
//                .file()
                .build()
                .execute(new UploadCallback<UploadBean>() {

                    @Override
                    public void onProgress(File file, long currentSize, long totalSize, float progress, int currentIndex, int totalFile) {
                        Log.d(TAG, "file:" + file.getName() + " currentSize:" + currentSize + " totalSize:" + totalSize + " progress:" + progress+" currentIndex:"+currentIndex+" totalFile:"+totalFile);
                    }

                    @Override
                    public void onSuccess(UploadBean value) {
                        Log.d(TAG, "onSuccess: ");
                    }
                });

    }
}