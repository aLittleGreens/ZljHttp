package com.klaus.zljhttplib.function;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.HttpException;


/**
 * @file:HttpExecptionHandler.java
 * @date: 2018/4/26
 * @author： xieziqi
 * @describe: http错误处理器
 */

public class HttpExceptionHandler {
    private static final int UNAUTHORIZED = 401;//401
    private static final int FORBIDDEN = 403;//403
    private static final int NOT_FOUND = 404 ;//404
    private static final int REQUEST_TIMEOUT = 408;//408
    private static final int INTERNAL_SERVER_ERROR = 500;//500
    private static final int BAD_GATEWAY = 502;//502
    private static final int SERVICE_UNAVAILABLE = 503;//503
    private static final int GATEWAY_TIMEOUT = 504;//504
    public static final Map<String,String> ERROR_MAP = new HashMap<>();
    public static void init(){
        ERROR_MAP.put(String.valueOf(ERROR.DOMAIN_ERROR),"DE");
        ERROR_MAP.put(String.valueOf(ERROR.HTTP_ERROR),"HE");
        ERROR_MAP.put(String.valueOf(ERROR.NETWORK_ERROR),"NE");
        ERROR_MAP.put(String.valueOf(ERROR.PARSE_ERROR),"PE");
        ERROR_MAP.put(String.valueOf(ERROR.UNKNOWN_ERROR),"UE");
        ERROR_MAP.put(String.valueOf(ERROR.SSL_ERROR),"SE");
    }
    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, ERROR.HTTP_ERROR, httpException.code());
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    //ex.errorCode = httpException.errorCode();
                    ex.message = "网络错误";
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponseThrowable(resultException, resultException.errorCode);
            ex.message = resultException.message;
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSyntaxException
                /*|| e instanceof ParseException*/) {
            ex = new ResponseThrowable(e, ERROR.PARSE_ERROR);
            ex.message = "解析错误";
            return ex;
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
            ex = new ResponseThrowable(e, ERROR.NETWORK_ERROR);
            ex.message = "连接失败";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponseThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        } else if(e instanceof UnknownHostException){
            ex = new ResponseThrowable(e, ERROR.DOMAIN_ERROR);
            ex.message = "域名错误";
            return ex;
        }else {
            ex = new ResponseThrowable(e, ERROR.UNKNOWN_ERROR);
            ex.message = "未知错误";
            return ex;
        }
    }


    /**
     * 约定异常
     */
    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN_ERROR = 100;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 101;
        /**
         * 网络错误
         */
        public static final int NETWORK_ERROR = 102;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 103;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 105;

        /**
         * 域名错误
         */
        public static final int DOMAIN_ERROR = 106;
    }

    public static class ResponseThrowable extends Exception {
        public int errorCode;
        public String message;
        public int httpCode;

        public ResponseThrowable(Throwable throwable, int code) {
            super(throwable);
            this.errorCode = code;
        }

        public ResponseThrowable(Throwable throwable, int code, int httpCode) {
            super(throwable);
            this.errorCode = code;
            this.httpCode = httpCode;
        }
    }

    /**
     * ServerException发生后，将自动转换为ResponeThrowable返回
     */
    class ServerException extends RuntimeException {
        int errorCode;
        String message;
        int httpCode;
    }
}
