package com.jojo.ws.uploader;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.breakstore.BreakInfo;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class UploadTask {
    private int id;
    private final String uploadToken;
    private final String type;
    private final File uploadFile;
    private final boolean created;
    private final String partUploadUrl;
    //server filepath
    private final String filePath;
    private final String directUploadUrl;
    private final Map<String, List<String>> headerMapFields;
    @Nullable
    private BreakInfo breakInfo;
    private String uploadBatch;
    private volatile AtomicLong progress;

    public UploadTask(String uploadToken, String type, File uploadFile, boolean created, String partUploadUrl, String filePath, String directUploadUrl, Map<String, List<String>> headerMapFields) {
        this.uploadToken = uploadToken;
        this.type = type;
        this.uploadFile = uploadFile;
        this.created = created;
        this.partUploadUrl = partUploadUrl;
        this.filePath = filePath;
        this.directUploadUrl = directUploadUrl;
        this.headerMapFields = headerMapFields;
        this.id = 1;
        progress = new AtomicLong();
    }

    public String getUploadBatch() {
        return uploadBatch;
    }

    public AtomicLong getProgress() {
        return progress;
    }

    public void setUploadBatch(String uploadBatch) {
        this.uploadBatch = uploadBatch;
    }

    public void setBreakInfo(BreakInfo info) {
        this.breakInfo = info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUploadToken() {
        return uploadToken;
    }

    public String getType() {
        return type;
    }

    public File getUploadFile() {
        return uploadFile;
    }

    public boolean isCreated() {
        return created;
    }

    public String getPartUploadUrl() {
        return partUploadUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDirectUploadUrl() {
        return directUploadUrl;
    }

    public Map<String, List<String>> getHeaderMapFields() {
        return headerMapFields;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadTask that = (UploadTask) o;
        if (id == that.id) {
            return true;
        } else {
            return equalsArg(that);
        }
    }

    private boolean equalsArg(UploadTask uploadTask) {
        return TextUtils.equals(uploadFile.getPath(), (uploadTask.uploadFile.getPath())) && TextUtils.equals(this.filePath, uploadTask.getFilePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setBlocks(Block[] blocks) {
        if (getBreakInfo() != null) {
            breakInfo.setBlocks(blocks);
        }
    }

    @Nullable
    public BreakInfo getBreakInfo() {
        if (breakInfo == null) {
            breakInfo = new BreakInfo(id, partUploadUrl, uploadToken, filePath, created, getUploadFile().getPath(), 0);
//            breakInfo = WsFileUploader.with().breakStore().createAndInsert(this);
        }
        return breakInfo;
    }

    public static class Builder {
        final UploadToken uploadToken;
        final String filePath;
        private Map<String, List<String>> headerMapFields;

        public Builder(UploadToken uploadToken, String filePath) {
            this.uploadToken = uploadToken;
            this.filePath = filePath;
        }

        public Builder header(Map<String, List<String>> headerMapFields) {
            this.headerMapFields = headerMapFields;
            return this;
        }

        public UploadTask build() {
            return new UploadTask(uploadToken.uploadToken, uploadToken.type, new File(filePath), uploadToken.created, uploadToken.partUploadUrl, uploadToken.filePath, uploadToken.directUploadUrl, headerMapFields);
        }
    }

}
