package com.jojo.ws.uploader.core.connection;

public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
