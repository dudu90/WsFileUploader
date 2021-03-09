package com.jojo.ws.uploader;

import com.jojo.ws.uploader.core.end.EndCause;
import com.jojo.ws.uploader.core.interceptors.BlockBuildInterceptor;
import com.jojo.ws.uploader.core.interceptors.BlockMergeInterceptor;
import com.jojo.ws.uploader.core.interceptors.BlockUploadInterceptor;
import com.jojo.ws.uploader.core.interceptors.CacheInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadCall {
    private final UploadTask uploadTask;

    private UploaderCallback uploaderCallback;

    private volatile boolean canceled = false;
    private volatile boolean hasError = false;

    public UploadCall(UploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public void enqueue(UploaderCallback uploaderCallback) {
        this.uploaderCallback = uploaderCallback;
        WsFileUploader.with().uploadDispatcher().enqueue(new AsyncCall(this));
    }


    public UploadTask uploadTask() {
        return uploadTask;
    }

    public UploaderCallback uploaderCallback() {
        return uploaderCallback;
    }

    public synchronized boolean isInterrupt() {
        return canceled || hasError;
    }


    public synchronized void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public synchronized void setHasError(boolean hasError, Exception exception) {
        this.hasError = hasError;
        WsFileUploader.with().handlerDispatcher().postMain(() -> {
            if (uploaderCallback != null) {
                uploaderCallback.onEnd(uploadTask, EndCause.ERROR, exception);
            }
        });
    }

    public final class AsyncCall extends NamedRunnable {
        UploadCall uploadCall;

        AsyncCall(UploadCall uploadCall) {
            super("UploadTask" + uploadTaskToName());
            this.uploadCall = uploadCall;
        }

        public UploadCall get() {
            return UploadCall.this;
        }

        @Override
        protected void execute() {
            try {
                if (get().isInterrupt()) {
                    return;
                }
                uploadWithInterceptorChain();
            } catch (IOException e) {

            }
        }
    }

    public void uploadWithInterceptorChain() throws IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new BlockBuildInterceptor());
        interceptors.add(new CacheInterceptor());
        interceptors.add(new BlockUploadInterceptor());
        interceptors.add(new BlockMergeInterceptor());
        Interceptor.Chain chain = new RealUploadInterceptorChain(this, uploadTask, interceptors, 0);
        chain.proceed();
    }


    public synchronized void cancel() {
        canceled = true;
        WsFileUploader.with().handlerDispatcher().postMain(() -> {
            if (uploaderCallback != null) {
                uploaderCallback.onEnd(uploadTask, EndCause.CANCELED, null);
            }
        });
    }


    public String uploadTaskToName() {
        return uploadTask.toString();
    }
}
