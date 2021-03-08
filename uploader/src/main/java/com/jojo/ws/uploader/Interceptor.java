package com.jojo.ws.uploader;

import java.io.IOException;

public interface Interceptor {

    void intercept(Chain chain) throws IOException;


    interface Chain {
        UploadTask task();

        UploadCall call();

        void proceed() throws IOException;
    }
}
