package com.jojo.ws.uploader.cache;

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
import static com.jojo.ws.uploader.cache.BreakSqliteKey.CTX;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.CURRENTBLOCK;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.DIRECTUPLOADURL;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.ID;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.LOCALFILE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.OFFSET;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.PARTUPLOADURL;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.SIZE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.SLICESIZE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.START;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.TASKID;

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
                + BreakSqliteKey.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BreakSqliteKey.UPLOADTOKEN + " VARCHAR NOT NULL, "
                + BreakSqliteKey.TYPE + " VARCHAR NOT NULL, "
                + BreakSqliteKey.FILEPATH + " VARCHAR NOT NULL, "
                + BreakSqliteKey.CREATED + " INTEGER, "
                + PARTUPLOADURL + " VARCHAR NOT NULL, "
                + DIRECTUPLOADURL + " VARCHAR NOT NULL, "
                + LOCALFILE + " VARCHAR NOT NULL, "
                + CURRENTBLOCK + " INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_BLOCK + "( "
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TASKID + " INTEGER, "
                + START + " NUMERIC, "
                + SIZE + " NUMERIC, "
                + SLICESIZE + " INTEGER, "
                + CTX + " VARCHAR,"
                + CHECKSUM + " VARCHAR, "
                + CRC32 + " NUMERIC, "
                + OFFSET + " NUMERIC)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public void updateInfo(BreakInfo breakInfo) {

    }

    public int insert(BreakInfo breakInfo) {
        final SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int id = (int) sqLiteDatabase.insert(TABLE_TASK, null, Utils.breakToRow(breakInfo).toContentValues());
        for (Block block : breakInfo.getBlockList()) {
            final BlockInfoRow blockInfoRow = Utils.blockToRow(block);
            blockInfoRow.setTaskId(id);
            sqLiteDatabase.insert(TABLE_BLOCK, null, blockInfoRow.toContentValues());
        }
        sqLiteDatabase.close();
        return id;
    }


    public SparseArray<BreakInfo> loadToCache() {
        final SQLiteDatabase database = getReadableDatabase();
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
