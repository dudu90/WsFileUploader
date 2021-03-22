package com.jojo.ws.uploader;

import android.content.Context;

import com.jojo.ws.uploader.core.breakstore.BreakStore;
import com.jojo.ws.uploader.core.connection.UploadConnection;
import com.jojo.ws.uploader.core.dispatcher.HandlerDispatcher;
import com.jojo.ws.uploader.core.dispatcher.InterceptorDispatcher;
import com.jojo.ws.uploader.core.dispatcher.UploadDispatcher;

public class WsFileUploader {
    static volatile WsFileUploader singleton;
    private final BreakStore breakStore;
    private final UploadConnection.Factory uploadConnectionFactory;
    private final UploadDispatcher uploadDispatcher;
    private final HandlerDispatcher handlerDispatcher;
    private final InterceptorDispatcher interceptorDispatcher;

    WsFileUploader(BreakStore breakStore, UploadConnection.Factory factory, UploadDispatcher uploadDispatcher, HandlerDispatcher handlerDispatcher, InterceptorDispatcher interceptorDispatcher) {
        this.breakStore = breakStore;
        this.uploadConnectionFactory = factory;
        this.uploadDispatcher = uploadDispatcher;
        this.handlerDispatcher = handlerDispatcher;
        this.interceptorDispatcher = interceptorDispatcher;
    }

    public static WsFileUploader with() {
        if (singleton == null) {
            synchronized (WsFileUploader.class) {
                if (singleton == null) {
                    singleton = new Builder(UploaderProvider.context).build();
                }
            }
        }
        return singleton;
    }

    public BreakStore breakStore() {
        return breakStore;
    }

    public HandlerDispatcher handlerDispatcher() {
        return handlerDispatcher;
    }

    public UploadConnection.Factory uploadConnectionFactory() {
        return uploadConnectionFactory;
    }

    public UploadDispatcher uploadDispatcher() {
        return uploadDispatcher;
    }

    public InterceptorDispatcher interceptorDispatcher() {
        return interceptorDispatcher;
    }

    public static class Builder {
        private BreakStore breakStore;
        private UploadConnection.Factory uploadConnectionFactory;
        private UploadDispatcher uploadDispatcher;
        private HandlerDispatcher handlerDispatcher;
        private InterceptorDispatcher interceptorDispatcher;
        private final Context context;

        Builder(Context context) {
            this.context = context;
        }

        public Builder breakStore(final BreakStore breakStore) {
            this.breakStore = breakStore;
            return this;
        }

        public Builder uploadConnectionFactory(UploadConnection.Factory uploadConnectionFactory) {
            this.uploadConnectionFactory = uploadConnectionFactory;
            return this;
        }

        public Builder uploadDispatcher(UploadDispatcher uploadDispatcher) {
            this.uploadDispatcher = uploadDispatcher;
            return this;
        }

        public Builder handlerDispatcher(HandlerDispatcher handlerDispatcher) {
            this.handlerDispatcher = handlerDispatcher;
            return this;
        }

        public Builder interceptorDispatcher(InterceptorDispatcher interceptorDispatcher) {
            this.interceptorDispatcher = interceptorDispatcher;
            return this;
        }

        public WsFileUploader build() {
            if (breakStore == null) {
                breakStore = Utils.createBreakStoreOnDisk(context);
            }
            if (uploadConnectionFactory == null) {
                uploadConnectionFactory = Utils.createDefaultConnectionFactory();
            }
            if (uploadDispatcher == null) {
                uploadDispatcher = new UploadDispatcher();
            }

            if (handlerDispatcher == null) {
                handlerDispatcher = new HandlerDispatcher();
            }

            if (interceptorDispatcher == null) {
                interceptorDispatcher = new InterceptorDispatcher();
            }

            return new WsFileUploader(breakStore, uploadConnectionFactory, uploadDispatcher, handlerDispatcher, interceptorDispatcher);
        }
    }
}
