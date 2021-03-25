package com.jojo.ws.uploader.core.breakstore;

import androidx.annotation.NonNull;
import com.jojo.ws.uploader.UploadTask;

public interface BreakStore {

    BreakInfo get(int id);

    int findOrCreateId(@NonNull UploadTask task);

    BreakInfo createAndInsert(@NonNull UploadTask task);

    void update(BreakInfo breakInfo);

    void updateBlock(int taskId, Block block);

    void onTaskEnd(int id);

    void remove(int id);
}
