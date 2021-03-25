package com.jojo.ws.uploader.cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.jojo.ws.uploader.core.breakstore.Block;

import static com.jojo.ws.uploader.cache.BreakSqliteKey.CHECKSUM;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.CRC32;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.CTX;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.ID;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.INDEX;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.OFFSET;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.SIZE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.SLICESIZE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.START;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.TASKID;

public class BlockInfoRow {
    private int id;
    private int index;
    private int taskId;
    private long start;
    private long size;
    private int sliceSize;
    private String ctx;
    private String checkSum;
    private long crc32;
    private long offset;

    BlockInfoRow(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(ID));
        this.index = cursor.getInt(cursor.getColumnIndex(INDEX));
        this.taskId = cursor.getInt(cursor.getColumnIndex(TASKID));
        this.start = cursor.getLong(cursor.getColumnIndex(START));
        this.size = cursor.getLong(cursor.getColumnIndex(SIZE));
        this.sliceSize = cursor.getInt(cursor.getColumnIndex(SLICESIZE));
        this.ctx = cursor.getString(cursor.getColumnIndex(CTX));
        this.checkSum = cursor.getString(cursor.getColumnIndex(CHECKSUM));
        this.crc32 = cursor.getLong(cursor.getColumnIndex(CRC32));
        this.offset = cursor.getLong(cursor.getColumnIndex(OFFSET));
    }

    public BlockInfoRow(int id, int index, int taskId, long start, long size, int sliceSize, String ctx, String checkSum, long crc32, long offset) {
        this.id = id;
        this.index = index;
        this.taskId = taskId;
        this.start = start;
        this.size = size;
        this.sliceSize = sliceSize;
        this.ctx = ctx;
        this.checkSum = checkSum;
        this.crc32 = crc32;
        this.offset = offset;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Block toBlock() {
        return new Block(index, ctx, checkSum, crc32, offset, start, size, sliceSize);
    }

    public ContentValues toContentValues() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(TASKID, taskId);
        contentValues.put(START, start);
        contentValues.put(SIZE, size);
        contentValues.put(SLICESIZE, sliceSize);
        contentValues.put(CTX, ctx);
        contentValues.put(CHECKSUM, checkSum);
        contentValues.put(CRC32, crc32);
        contentValues.put(OFFSET, offset);
        return contentValues;
    }
}
