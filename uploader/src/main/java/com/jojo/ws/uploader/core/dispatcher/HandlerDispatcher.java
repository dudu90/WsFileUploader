package com.jojo.ws.uploader.core.dispatcher;

import android.os.Handler;
import android.os.Looper;

public class HandlerDispatcher {
    private final Handler handler;

    public HandlerDispatcher() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    public synchronized void postMain(Runnable runnable) {
        handler.post(runnable);
    }
}
