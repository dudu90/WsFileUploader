package com.jojo.ws.uploader;

import android.text.TextUtils;


import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.breakstore.BreakInfo;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class UploadTask {
    private int id;
    private String tokenGetUrl;
    private String serverFileName;
    private Map<String, String> tokenGetHeader;
    private String uploadToken;
    private String type;
    private File uploadFile;
    private boolean created;
    private String partUploadUrl;
    //server filepath
    private String filePath;
    private String directUploadUrl;
    private BreakInfo breakInfo;
    private String uploadBatch;
    private volatile AtomicLong progress;

    /**
     * @param tokenGetUrl    拉去token的url
     * @param serverFileName 上传到服务器后的文件名
     * @param tokenGetHeader 获取token的auth header
     * @param uploadFile     本地文件
     * @param filePath       服务器文件地址
     */
    public UploadTask(final String tokenGetUrl,
                      final String serverFileName,
                      final Map<String, String> tokenGetHeader,
                      final File uploadFile,
                      final String filePath) {
        this.tokenGetUrl = tokenGetUrl;
        this.serverFileName = serverFileName;
        this.tokenGetHeader = tokenGetHeader;
        this.uploadFile = uploadFile;
        this.filePath = filePath;
        this.id = WsFileUploader.with().breakStore().findOrCreateId(this);
        breakInfo = WsFileUploader.with().breakStore().createAndInsert(this);
        progress = new AtomicLong();
    }

    public String getTokenGetUrl() {
        return tokenGetUrl;
    }

    public void setTokenGetUrl(String tokenGetUrl) {
        this.tokenGetUrl = tokenGetUrl;
    }

    public String getServerFileName() {
        return serverFileName;
    }

    public void setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
    }

    public Map<String, String> getTokenGetHeader() {
        return tokenGetHeader;
    }

    public void setTokenGetHeader(Map<String, String> tokenGetHeader) {
        this.tokenGetHeader = tokenGetHeader;
    }

    public void setProgress(AtomicLong progress) {
        this.progress = progress;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
        breakInfo.setUploadToken(uploadToken);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUploadFile(File uploadFile) {
        this.uploadFile = uploadFile;
    }

    public void setCreated(boolean created) {
        this.created = created;
        breakInfo.setCreated(created);
    }

    public void setPartUploadUrl(String partUploadUrl) {
        this.partUploadUrl = partUploadUrl;
        breakInfo.setPartUploadUrl(partUploadUrl);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setDirectUploadUrl(String directUploadUrl) {
        this.directUploadUrl = directUploadUrl;
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

    public BreakInfo getBreakInfo() {
        return breakInfo;
    }

    public static class Builder {
        final String localFilePath;
        final String uploadPath;
        private String tokenGetUrl;
        private String serverFileName;
        private Map<String, String> tokenGetHeader;

        public Builder(String uploadPath, String localFilePath) {
            this.uploadPath = uploadPath;
            this.localFilePath = localFilePath;
        }

        public Builder tokenGetUrl(final String tokenGetUrl) {
            this.tokenGetUrl = tokenGetUrl;
            return this;
        }

        public Builder serverFileName(final String serverFileName) {
            this.serverFileName = serverFileName;
            return this;
        }

        public Builder tokenGetHeader(final Map<String, String> tokenGetHeader) {
            this.tokenGetHeader = tokenGetHeader;
            return this;
        }

        public UploadTask build() {
            return new UploadTask(tokenGetUrl, serverFileName, tokenGetHeader, new File(localFilePath), uploadPath);
        }
    }

}
