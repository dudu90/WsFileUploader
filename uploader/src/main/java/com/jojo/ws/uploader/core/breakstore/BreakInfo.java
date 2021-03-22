package com.jojo.ws.uploader.core.breakstore;

import android.text.TextUtils;

import com.jojo.ws.uploader.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BreakInfo {
    private int id;
    private String partUploadUrl;
    private String uploadToken;
    //server path
    private String filePath;
    private boolean created;
    private String localFile;
    private List<Block> blockList;

    public BreakInfo(int id, String partUploadUrl, String uploadToken, String filePath, boolean created, String localFile) {
        this.id = id;
        this.partUploadUrl = partUploadUrl;
        this.uploadToken = uploadToken;
        this.filePath = filePath;
        this.created = created;
        this.localFile = localFile;
        blockList = new ArrayList<>();
    }

    public void setPartUploadUrl(String partUploadUrl) {
        this.partUploadUrl = partUploadUrl;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public void setBlockList(List<Block> blockList) {
        this.blockList = blockList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addBlock(Block block) {
        blockList.add(block);
    }

    public void setBlocks(Block[] blocks) {
        blockList.addAll(Arrays.asList(blocks));
    }

    public synchronized void setBlocks(List<Block> blocks) {
        blockList.clear();
        blockList.addAll(blocks);
    }

    public int getId() {
        return id;
    }

    public String getPartUploadUrl() {
        return partUploadUrl;
    }

    public String getUploadToken() {
        return uploadToken;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isCreated() {
        return created;
    }

    public String getLocalFile() {
        return localFile;
    }


    public List<Block> getBlockList() {
        return blockList;
    }


    public boolean isFromTask(UploadTask uploadTask) {
        return TextUtils.equals(filePath, uploadTask.getFilePath()) && TextUtils.equals(this.localFile, uploadTask.getUploadFile().getPath());
    }


}
