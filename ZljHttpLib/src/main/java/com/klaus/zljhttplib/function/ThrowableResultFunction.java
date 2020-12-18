package com.klaus.zljhttplib.function;


import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * @author LittleGreens <a href="mailto:alittlegreens@foxmail.com">Contact me.</a>
 * @version 1.0
 * @since 11/30/20 16:42
 * 异常转换
 */
public class ThrowableResultFunction<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        HttpExceptionHandler.ResponseThrowable handleException = HttpExceptionHandler.handleException(throwable);
        return Observable.error(handleException);
    }
}
