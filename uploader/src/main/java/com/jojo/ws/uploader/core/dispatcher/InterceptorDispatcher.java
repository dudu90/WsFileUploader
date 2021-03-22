package com.jojo.ws.uploader.core.dispatcher;

import com.jojo.ws.uploader.Interceptor;

import java.util.ArrayList;
import java.util.List;

public class InterceptorDispatcher {
    private List<Interceptor> interceptors = new ArrayList<>();

    public synchronized void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public synchronized void removeInterceptor(Interceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public synchronized List<Interceptor> getInterceptors() {
        return interceptors;
    }
}
