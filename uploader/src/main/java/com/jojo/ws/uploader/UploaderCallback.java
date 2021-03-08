package com.jojo.ws.uploader;

public interface UploaderCallback {
    void onStart();

    void onProgress(int total, int current);

    void onEnd();
}
