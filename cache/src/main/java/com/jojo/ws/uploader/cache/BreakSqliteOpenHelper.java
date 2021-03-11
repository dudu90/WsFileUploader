package com.jojo.ws.uploader.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.breakstore.BreakInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.jojo.ws.uploader.cache.BreakSqliteKey.CHECKSUM;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.CRC32;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.CREATED;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.CTX;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.CURRENTBLOCK;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.DIRECTUPLOADURL;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.FILEPATH;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.ID;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.INDEX;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.LOCALFILE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.OFFSET;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.PARTUPLOADURL;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.SIZE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.SLICESIZE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.START;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.TASKID;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.TYPE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.UPLOADTOKEN;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.UPLOAD_SUCCESS;

public class BreakSqliteOpenHelper extends SQLiteOpenHelper {
    private static final String NAME = "wsfileupload.db";
    private static final int VERSION = 1;
    private static final String TABLE_TASK = "upload_breakinfo";
    private static final String TABLE_BLOCK = "upload_block";

    BreakSqliteOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_TASK + "( "
                + ID + " INTEGER, "
                + UPLOADTOKEN + " VARCHAR NOT NULL, "
                + FILEPATH + " VARCHAR NOT NULL, "
                + CREATED + " INTEGER, "
                + PARTUPLOADURL + " VARCHAR NOT NULL, "
                + LOCALFILE + " VARCHAR NOT NULL )");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_BLOCK + "( "
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + INDEX + " INTEGER, "
                + TASKID + " INTEGER, "
                + START + " NUMERIC, "
                + SIZE + " NUMERIC, "
                + UPLOAD_SUCCESS + "  INTEGER, "
                + SLICESIZE + " INTEGER, "
                + CTX + " VARCHAR,"
                + CHECKSUM + " VARCHAR, "
                + CRC32 + " NUMERIC, "
                + OFFSET + " NUMERIC)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }


    public synchronized void update(BreakInfo breInfo) {
        removeTask(breInfo.getId());
        insert(breInfo);
    }


    public synchronized void removeTask(int id) {
        final SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_TASK, ID + "=?", new String[]{String.valueOf(id)});
        sqLiteDatabase.delete(TABLE_BLOCK, TASKID + "=?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
    }


    public synchronized void updateBreakInfo(BreakInfo breakInfo) {
        final SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UPLOADTOKEN, breakInfo.getUploadToken());
        contentValues.put(PARTUPLOADURL, breakInfo.getPartUploadUrl());
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.update(TABLE_TASK, contentValues, ID + "=?", new String[]{String.valueOf(breakInfo.getId())});
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }

    public synchronized void updateBlock(Block block) {
        final SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UPLOAD_SUCCESS, block.isUploadSuccess() ? 1 : 0);
        contentValues.put(CTX, block.getCtx());
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.update(TABLE_BLOCK, contentValues, INDEX + "=?", new String[]{String.valueOf(block.getIndex())});
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }

    public synchronized int insert(BreakInfo breakInfo) {
        final SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        int id = (int) sqLiteDatabase.insert(TABLE_TASK, null, Utils.breakToRow(breakInfo).toContentValues());
        for (Block block : breakInfo.getBlockList()) {
            final BlockInfoRow blockInfoRow = Utils.blockToRow(breakInfo.getId(), block);
            blockInfoRow.setTaskId(id);
            sqLiteDatabase.insert(TABLE_BLOCK, null, blockInfoRow.toContentValues());
        }
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
        return id;
    }


    public synchronized SparseArray<BreakInfo> loadToCache() {
        final SQLiteDatabase database = getWritableDatabase();
        final SparseArray<BreakInfo> breakpointInfoMap = new SparseArray<>();
        Cursor breakInfoCursor = null;
        Cursor blockInfoCursor = null;
        final List<BreakInfoRow> breakInfoRows = new ArrayList<>();
        final List<BlockInfoRow> blockInfoRows = new ArrayList<>();
        try {
            breakInfoCursor = database.rawQuery("SELECT * FROM " + TABLE_TASK, null);
            while (breakInfoCursor.moveToNext()) {
                breakInfoRows.add(new BreakInfoRow(breakInfoCursor));
            }
            blockInfoCursor = database.rawQuery("SELECT * FROM " + TABLE_BLOCK, null);
            while (blockInfoCursor.moveToNext()) {
                blockInfoRows.add(new BlockInfoRow(blockInfoCursor));
            }
        } finally {
            if (breakInfoCursor != null) {
                breakInfoCursor.close();
            }
            if (blockInfoCursor != null) {
                blockInfoCursor.close();
            }
        }

        for (BreakInfoRow breakInfoRow : breakInfoRows) {
            final BreakInfo breakInfo = breakInfoRow.toInfo();
            final Iterator<BlockInfoRow> blockInfoRowIterator = blockInfoRows.iterator();
            while (blockInfoRowIterator.hasNext()) {
                final BlockInfoRow blockInfoRow = blockInfoRowIterator.next();
                if (blockInfoRow.getTaskId() == breakInfoRow.getId()) {
                    breakInfo.addBlock(blockInfoRow.toBlock());
                }
            }
            breakpointInfoMap.put(breakInfo.getId(), breakInfo);
        }

        return breakpointInfoMap;
    }
}
