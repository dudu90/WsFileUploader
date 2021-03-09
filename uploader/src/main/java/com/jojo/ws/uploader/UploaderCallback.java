package com.jojo.ws.uploader;

import com.jojo.ws.uploader.core.breakstore.Block;

import java.util.List;

public interface UploaderCallback {
    void onStart(UploadTask uploadTask);

    void onBlockUploaded(UploadTask uploadTask, List<Block> blocks);

    void onProgress(UploadTask uploadTask, int total, int current);

    void onEnd(UploadTask uploadTask);
}
