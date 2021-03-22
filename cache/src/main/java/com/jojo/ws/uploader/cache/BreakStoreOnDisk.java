package com.jojo.ws.uploader.cache;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jojo.ws.uploader.UploadTask;
import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.breakstore.BreakInfo;
import com.jojo.ws.uploader.core.breakstore.BreakStore;
import com.jojo.ws.uploader.core.breakstore.BreakStoreOnCache;

public class BreakStoreOnDisk implements BreakStore {
    private BreakSqliteOpenHelper sqliteOpenHelper;
    private BreakStoreOnCache breakStoreOnCache;


    public BreakStoreOnDisk(final Context context) {
        this(new BreakSqliteOpenHelper(context));
    }

    BreakStoreOnDisk(BreakSqliteOpenHelper sqliteOpenHelper) {
        this.sqliteOpenHelper = sqliteOpenHelper;
        this.breakStoreOnCache = new BreakStoreOnCache(this.sqliteOpenHelper.loadToCache());
    }

    @Override
    public BreakInfo get(int id) {
        return breakStoreOnCache.get(id);
    }

    @Override
    public int findOrCreateId(@NonNull UploadTask task) {
        return breakStoreOnCache.findOrCreateId(task);
    }

    @Override
    public BreakInfo createAndInsert(UploadTask task) {
        final BreakInfo breakInfo = breakStoreOnCache.createAndInsert(task);
        return breakInfo;
    }

    @Override
    public void onTaskEnd(int id) {
        breakStoreOnCache.onTaskEnd(id);
        remove(id);
    }

    @Override
    public void remove(int id) {
        sqliteOpenHelper.removeTask(id);
    }

    @Override
    public void update(BreakInfo breakInfo) {
        breakStoreOnCache.update(breakInfo);
        sqliteOpenHelper.update(breakInfo);
    }

    @Override
    public void updateBlock(int taskId, Block block) {
        breakStoreOnCache.updateBlock(taskId, block);
        sqliteOpenHelper.updateBlock(block);
    }
}
