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
    private String filePath;
    private boolean created;
    private String partUploadUrl;
    private String localFile;

    public BreakInfoRow(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(ID));
        this.uploadToken = cursor.getString(cursor.getColumnIndex(UPLOADTOKEN));
        this.filePath = cursor.getString(cursor.getColumnIndex(FILEPATH));
        this.created = cursor.getInt(cursor.getColumnIndex(CREATED)) != 0;
        this.partUploadUrl = cursor.getString(cursor.getColumnIndex(PARTUPLOADURL));
        this.localFile = cursor.getString(cursor.getColumnIndex(LOCALFILE));
    }

    public BreakInfoRow(int id, String uploadToken, String type, String filePath, boolean created, String partUploadUrl, String localFile) {
        this.id = id;
        this.uploadToken = uploadToken;
        this.filePath = filePath;
        this.created = created;
        this.partUploadUrl = partUploadUrl;
        this.localFile = localFile;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public BreakInfo toInfo() {
        final BreakInfo breakInfo = new BreakInfo(id, partUploadUrl, uploadToken, filePath, created, localFile);
        return breakInfo;
    }

    public ContentValues toContentValues() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(UPLOADTOKEN, uploadToken);
        contentValues.put(FILEPATH, filePath);
        contentValues.put(CREATED, created ? 1 : 0);
        contentValues.put(PARTUPLOADURL, partUploadUrl);
        contentValues.put(LOCALFILE, localFile);
        return contentValues;
    }


}
