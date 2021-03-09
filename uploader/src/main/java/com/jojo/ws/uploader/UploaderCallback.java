package com.jojo.ws.uploader;

import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.end.EndCause;

import java.util.List;

public interface UploaderCallback {
    void onStart(UploadTask uploadTask);

    void onBlockUploaded(UploadTask uploadTask, List<Block> blocks);

    void onProgress(UploadTask uploadTask, long total, long current);

    void onEnd(UploadTask uploadTask, EndCause cause,Exception errorCause);
}
