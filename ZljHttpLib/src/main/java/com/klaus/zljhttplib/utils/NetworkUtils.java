package com.klaus.zljhttplib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @Description : 网络操作工具类
 */
public final class NetworkUtils {
    public static final int NETWORK_NONE = 0; // 没有网络连接
    public static final int NETWORK_WIFI = 1; // wifi连接
    public static final int NETWORK_2G = 2; // 2G
    public static final int NETWORK_3G = 3; // 3G
    public static final int NETWORK_4G = 4; // 4G
    public static final int NETWORK_MOBILE = 5; // 手机流量

    /**
     * 判断网络是否已连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null) return false;
        try {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conn != null) {
                NetworkInfo net = conn.getActiveNetworkInfo();
                if (net != null && net.isConnected()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断当前已连接网络类型
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        int curState = NetworkMessageType.NET_STATUS_TYPE_DISCONNECTED;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null || connManager.getActiveNetworkInfo() == null) {
            return curState;
        }

        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isConnected()) {
            return curState;
        }

        if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            curState = NetworkMessageType.NET_STATUS_TYPE_WIFI;
        } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (isFastMobileNetwork(context)) {
                curState = NetworkMessageType.NET_STATUS_TYPE_3G;
            } else {
                curState = NetworkMessageType.NET_STATUS_TYPE_2G;
            }
        }

        return curState;
    }

    /**
     * 判断当前网络是否是Wifi网络
     *
     * @param context
     * @return
     */
    public static boolean isWifiNetwork(Context context) {
        return getNetworkType(context) == NetworkMessageType.NET_STATUS_TYPE_WIFI;
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    /**
     * 网络状态消息类型
     */
    public interface NetworkMessageType {

        int BASE = 0x1000;

        /** 已连接 */
        int NET_STATUS_TYPE_CONNECTED = BASE + 1;

        /** 已断开 */
        int NET_STATUS_TYPE_DISCONNECTED = BASE + 2;

        /** WiFi网络 */
        int NET_STATUS_TYPE_WIFI = BASE + 3;

        /** 2G网络 */
        int NET_STATUS_TYPE_2G = BASE + 4;

        /** 3G网络 */
        int NET_STATUS_TYPE_3G = BASE + 5;

        /** 4G网络 */
        int NET_STATUS_TYPE_4G = BASE + 6;

    }
}
