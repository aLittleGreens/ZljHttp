package com.klaus.zljhttplib.function;


/**
 * @file:UserTokenManager.java
 * @describe: token 过期管理器
 */
public class UserTokenManager {
    private static final String TAG = "UserTokenManager";
    private static final UserTokenManager mInstance = new UserTokenManager();

    private UserTokenManager() {
    }

    public static UserTokenManager getInstance() {
        return mInstance;
    }


    public void handleUserTokenExpire(String code) {
// TODO: 12/17/20
        //获取顶层Activity，跳转到登录页面，重新登录
//        Context context = ActivityUtils.getStackTopActivity();
//        if (context == null) {
//            return false;
//        }
//
//        return false;
    }
}
