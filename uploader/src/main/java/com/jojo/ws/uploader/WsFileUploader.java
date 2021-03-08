package com.jojo.ws.uploader;

import android.content.Context;

import com.jojo.ws.uploader.core.breakstore.BreakStore;
import com.jojo.ws.uploader.core.connection.UploadConnection;
import com.jojo.ws.uploader.core.dispatcher.UploadDispatcher;

public class WsFileUploader {
    static volatile WsFileUploader singleton;
    private final BreakStore breakStore;
    private final UploadConnection.Factory uploadConnectionFactory;
    private final UploadDispatcher uploadDispatcher;

    WsFileUploader(BreakStore breakStore, UploadConnection.Factory factory, UploadDispatcher uploadDispatcher) {
        this.breakStore = breakStore;
        this.uploadConnectionFactory = factory;
        this.uploadDispatcher = uploadDispatcher;
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

    public UploadConnection.Factory uploadConnectionFactory() {
        return uploadConnectionFactory;
    }

    public UploadDispatcher uploadDispatcher() {
        return uploadDispatcher;
    }

    public static class Builder {
        private BreakStore breakStore;
        private UploadConnection.Factory uploadConnectionFactory;
        private UploadDispatcher uploadDispatcher;
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

            return new WsFileUploader(breakStore, uploadConnectionFactory, uploadDispatcher);
        }
    }
}
