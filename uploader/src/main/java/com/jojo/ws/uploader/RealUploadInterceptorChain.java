package com.jojo.ws.uploader;

import java.io.IOException;
import java.util.List;

public final class RealUploadInterceptorChain implements Interceptor.Chain {
    private final UploadCall call;
    private final UploadTask uploadTask;
    private final List<Interceptor> interceptors;
    private final int index;


    RealUploadInterceptorChain(UploadCall call, UploadTask uploadTask, List<Interceptor> interceptors, int index) {
        this.call = call;
        this.uploadTask = uploadTask;
        this.interceptors = interceptors;
        this.index = index;
    }


    @Override
    public UploadTask task() {
        return uploadTask;
    }

    @Override
    public UploadCall call() {
        return call;
    }

    @Override
    public void proceed() throws IOException {
        if (index >= interceptors.size()) throw new AssertionError();

        if (call == null || uploadTask == null) {
            throw new IllegalStateException("call must be not null.");
        }

        RealUploadInterceptorChain chain = new RealUploadInterceptorChain(call, uploadTask, interceptors, index + 1);
        interceptors.get(index).intercept(chain);
    }


}
