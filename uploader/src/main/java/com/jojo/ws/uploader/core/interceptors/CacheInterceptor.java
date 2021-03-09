package com.jojo.ws.uploader.core.interceptors;

import com.jojo.ws.uploader.Interceptor;

import java.io.IOException;

public class CacheInterceptor implements Interceptor {
    @Override
    public void intercept(Chain chain) throws IOException {
        chain.proceed();
    }
}
