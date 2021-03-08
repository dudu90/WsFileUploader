package com.jojo.ws.uploader.cache;

import androidx.annotation.NonNull;

import com.jojo.ws.uploader.UploadTask;
import com.jojo.ws.uploader.core.breakstore.BreakInfo;
import com.jojo.ws.uploader.core.breakstore.BreakStore;
import com.jojo.ws.uploader.core.breakstore.BreakStoreOnCache;

public class BreakStoreOnDisk implements BreakStore {
    private BreakSqliteOpenHelper sqliteOpenHelper;
    private BreakStoreOnCache breakStoreOnCache;

    BreakStoreOnDisk(BreakSqliteOpenHelper sqliteOpenHelper) {
        this.sqliteOpenHelper = sqliteOpenHelper;
        this.breakStoreOnCache = new BreakStoreOnCache(this.sqliteOpenHelper.loadToCache());
    }

    @Override
    public BreakInfo get(int id) {
        return null;
    }

    @Override
    public int findOrCreateId(@NonNull UploadTask task) {
        return 0;
    }

    @Override
    public BreakInfo createAndInsert(UploadTask task) {
        final BreakInfo breakInfo = breakStoreOnCache.createAndInsert(task);
        if (breakInfo.getBlockList().size() == 0) {
            breakStoreOnCache.updateBlock(task.getId(), task.getBreakInfo().getBlockList());
        }
        sqliteOpenHelper.insert(breakStoreOnCache.createAndInsert(task));
        return breakStoreOnCache.createAndInsert(task);
    }

    @Override
    public void onTaskEnd(int id) {

    }

    @Override
    public void remove(int id) {

    }

    @Override
    public void update(BreakInfo breakInfo) {

    }
}
