package com.klaus.zljhttpsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.klaus.zljhttplib.ZljHttpRequest;
import com.klaus.zljhttplib.annotation.MergeType;
import com.klaus.zljhttplib.callback.MergeHttpCallback;
import com.klaus.zljhttpsample.base.BaseActivity;
import com.klaus.zljhttpsample.bean.MessageCenterBean;
import com.klaus.zljhttpsample.bean.NewAdvertBean;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class MergeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
    }

    private void merge() {
        Map<String, String> params = new HashMap<>();
        Observable<String> observable1 = ZljHttpRequest.get()
                .addParameter(params)
                .apiUrl("api/message/get_message_prompt_info")
                .call();

        Observable<String> observable2 = ZljHttpRequest.get()
                .apiUrl("api/app/home/activity_info")
                .call();


        ZljHttpRequest.get()
                .lifecycle(this)
                .tag(111)
                .build()
                .zip(observable1, observable2)
                .executeMerge(new MergeHttpCallback<Map<Class<?>, Object>>() {

                                  @MergeType({MessageCenterBean.class, NewAdvertBean.class})
                                  @Override
                                  public void onSuccess(Map<Class<?>, Object> resultMap) {
                                      Log.d(TAG, "success");
                                      NewAdvertBean newAdvertBean = (NewAdvertBean) resultMap.get(NewAdvertBean.class);
                                      MessageCenterBean messageCenterBean = (MessageCenterBean) resultMap.get(MessageCenterBean.class);

                                  }


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
                                  public void onError(String code, String desc) {
                                      Log.d(TAG, "onError code:" + code + " desc:" + desc);
                                  }

                                  @Override
                                  public void onFail(String code, String desc) {
                                  }

                              }

                );

    }
}