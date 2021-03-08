package com.jojo.ws.uploader.cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.jojo.ws.uploader.core.breakstore.BreakInfo;

import static com.jojo.ws.uploader.cache.BreakSqliteKey.CREATED;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.CURRENTBLOCK;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.DIRECTUPLOADURL;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.FILEPATH;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.ID;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.LOCALFILE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.PARTUPLOADURL;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.TYPE;
import static com.jojo.ws.uploader.cache.BreakSqliteKey.UPLOADTOKEN;

public class BreakInfoRow {
    private int id;
    private String uploadToken;
    private String type;
    private String filePath;
    private boolean created;
    private String partUploadUrl;
    private String directUploadUrl;
    private String localFile;
    private int currentBlock;

    public BreakInfoRow(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(ID));
        this.uploadToken = cursor.getString(cursor.getColumnIndex(UPLOADTOKEN));
        this.type = cursor.getString(cursor.getColumnIndex(TYPE));
        this.filePath = cursor.getString(cursor.getColumnIndex(FILEPATH));
        this.created = cursor.getInt(cursor.getColumnIndex(CREATED)) != 0;
        this.partUploadUrl = cursor.getString(cursor.getColumnIndex(PARTUPLOADURL));
        this.directUploadUrl = cursor.getString(cursor.getColumnIndex(DIRECTUPLOADURL));
        this.localFile = cursor.getString(cursor.getColumnIndex(LOCALFILE));
        this.currentBlock = cursor.getInt(cursor.getColumnIndex(CURRENTBLOCK));
    }

    public BreakInfoRow(int id, String uploadToken, String type, String filePath, boolean created, String partUploadUrl, String directUploadUrl, String localFile, int currentBlock) {
        this.id = id;
        this.uploadToken = uploadToken;
        this.type = type;
        this.filePath = filePath;
        this.created = created;
        this.partUploadUrl = partUploadUrl;
        this.directUploadUrl = directUploadUrl;
        this.localFile = localFile;
        this.currentBlock = currentBlock;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public BreakInfo toInfo() {
        final BreakInfo breakInfo = new BreakInfo(id, partUploadUrl, uploadToken, filePath, created, localFile, currentBlock);
        return breakInfo;
    }

    public ContentValues toContentValues() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(UPLOADTOKEN, uploadToken);
        contentValues.put(TYPE, type);
        contentValues.put(FILEPATH, filePath);
        contentValues.put(CREATED, created ? 1 : 0);
        contentValues.put(PARTUPLOADURL, partUploadUrl);
        contentValues.put(DIRECTUPLOADURL, directUploadUrl);
        contentValues.put(LOCALFILE, localFile);
        contentValues.put(CURRENTBLOCK, currentBlock);
        return contentValues;
    }


}
