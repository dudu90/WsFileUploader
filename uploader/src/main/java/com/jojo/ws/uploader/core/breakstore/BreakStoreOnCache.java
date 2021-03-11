package com.jojo.ws.uploader.core.breakstore;

import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.jojo.ws.uploader.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BreakStoreOnCache implements BreakStore {
    public static final int FIRST_ID = 1;
    private final SparseArray<BreakInfo> storedInfos;
    private final SparseArray<BreakInfo> unStoredInfos;
    private final List<Integer> sortedOccupiedIds;


    public BreakStoreOnCache(SparseArray<BreakInfo> storedInfos) {
        this(storedInfos, new SparseArray<>());
    }


    public BreakStoreOnCache(SparseArray<BreakInfo> storedInfos, SparseArray<BreakInfo> unStoredInfos) {
        this.storedInfos = storedInfos;
        this.unStoredInfos = unStoredInfos;
        final int count = 0;

        sortedOccupiedIds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            sortedOccupiedIds.add(storedInfos.valueAt(i).getId());
        }
        Collections.sort(sortedOccupiedIds);
    }

    @Override
    public BreakInfo get(int id) {
        return storedInfos.get(id);
    }

    @Override
    public int findOrCreateId(@NonNull UploadTask task) {
        final int size = storedInfos.size();
        for (int i = 0; i < size; i++) {
            final BreakInfo breakInfo = storedInfos.valueAt(i);
            if (breakInfo != null && breakInfo.isFromTask(task)) {
                return breakInfo.getId();
            }
        }
        final int id = allocateId();
        final BreakInfo breakInfo = new BreakInfo(id, task.getPartUploadUrl(), task.getUploadToken(), task.getFilePath(), task.isCreated(), task.getUploadFile().getPath());
        putBreakInfo(id, breakInfo);
        return id;
    }

    public void updateBlock(final int id, List<Block> blocks) {
        final BreakInfo breakInfo = storedInfos.get(id);
        breakInfo.setBlocks(blocks);
        final int index = storedInfos.indexOfKey(id);
        storedInfos.setValueAt(index, breakInfo);
    }

    @Override
    public BreakInfo createAndInsert(@NonNull UploadTask task) {
        final int id = task.getId();
        final BreakInfo breakInfo = new BreakInfo(task.getId(), task.getPartUploadUrl(), task.getUploadToken(), task.getFilePath(), task.isCreated(), task.getUploadFile().getPath());
        synchronized (this) {
            storedInfos.put(id, breakInfo);
            unStoredInfos.remove(id);
        }
        return breakInfo;
    }

    @Override
    public void update(BreakInfo breakInfo) {
        final int id = storedInfos.indexOfKey(breakInfo.getId());
        storedInfos.setValueAt(id, breakInfo);
    }

    @Override
    public synchronized void updateBlock(int taskId, Block block) {
        final BreakInfo breakInfo = storedInfos.get(taskId);
        for (int i = 0; i < breakInfo.getBlockList().size(); i++) {
            if (breakInfo.getBlockList().get(i).getIndex() == block.getIndex()) {
                breakInfo.getBlockList().set(i, block);
            }
        }
    }

    @Override
    public void onTaskEnd(int id) {
        remove(id);
    }

    @Override
    public void remove(int id) {
        storedInfos.remove(id);
    }

    public void putBreakInfo(int id, BreakInfo breakInfo) {
        synchronized (this) {
            unStoredInfos.put(id, breakInfo);
        }
    }

    synchronized int allocateId() {
        int newId = 0;

        int index = 0;

        int preId = 0;
        int curId;

        for (int i = 0; i < sortedOccupiedIds.size(); i++) {
            final Integer curIdObj = sortedOccupiedIds.get(i);
            if (curIdObj == null) {
                index = i;
                newId = preId + 1;
                break;
            }

            curId = curIdObj;
            if (preId == 0) {
                if (curId != FIRST_ID) {
                    newId = FIRST_ID;
                    index = 0;
                    break;
                }
                preId = curId;
                continue;
            }

            if (curId != preId + 1) {
                newId = preId + 1;
                index = i;
                break;
            }

            preId = curId;
        }

        if (newId == 0) {
            if (sortedOccupiedIds.isEmpty()) {
                newId = FIRST_ID;
            } else {
                newId = sortedOccupiedIds.get(sortedOccupiedIds.size() - 1) + 1;
                index = sortedOccupiedIds.size();
            }
        }

        sortedOccupiedIds.add(index, newId);

        return newId;
    }
}
