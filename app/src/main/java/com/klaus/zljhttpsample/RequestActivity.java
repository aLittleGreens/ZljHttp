package com.klaus.zljhttpsample;


import android.os.Bundle;
import android.util.Log;

import com.klaus.zljhttplib.ZljHttpRequest;
import com.klaus.zljhttplib.callback.HttpCallback;
import com.klaus.zljhttpsample.base.BaseActivity;
import com.klaus.zljhttpsample.bean.LoginBean;

import java.util.HashMap;
import java.util.Map;

public class RequestActivity extends BaseActivity {

    private static final String TAG = "RequestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
    }


    private void request() {
        Map<String, String> params = new HashMap<>();
        params.put("phone_num", "130123223232");

        ZljHttpRequest.get()
                .lifecycle(this) //绑定生命周期
                .tag("111")//tag用于取消请求,ZljHttp.cancel("");
                .addParameter(params) //业务参数
                .baseUrl("")//如果不传，用的默认的baseUrl
                .apiUrl("api/app/user/login")
//                .addHeader() //请求头
//                .isMainThread(false) //结果是否回调到主线程，默认是
                .build()
                .execute(new HttpCallback<LoginBean>() {

                    @Override
                    public void onRequestStart() {
                        Log.d(TAG, "onRequestStart");

                    }

                    @Override
                    public void onRequestFinish() {
                        Log.d(TAG, "onRequestFinish");
                    }

                    @Override
                    public void onNotNet() {
                        Log.d(TAG, "onNotNet");
                    }

                    @Override
                    public void onSuccess(LoginBean value) {
                        Log.d(TAG, "onSuccess:");
                    }

                    @Override
                    public void onError(String code, String desc) {
                        Log.d(TAG, "onError code:" + code + " desc:" + desc);
                    }

                    @Override
                    public void onFail(String code, String desc) {
                        Log.d(TAG, "onError code:" + code + " desc:" + desc);
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel ");
                    }
                });

    }
}