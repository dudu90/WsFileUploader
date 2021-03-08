package com.jojo.ws.uploader.core.breakstore;

import android.text.TextUtils;

import com.jojo.ws.uploader.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BreakInfo {
    private int id;
    private final String partUploadUrl;
    private final String uploadToken;
    //server path
    private final String filePath;
    private final boolean created;
    private final String localFile;
    private final int currentBlock;
    private final List<Block> blockList;

    public BreakInfo(int id, String partUploadUrl, String uploadToken, String filePath, boolean created, String localFile, int currentBlock) {
        this.id = id;
        this.partUploadUrl = partUploadUrl;
        this.uploadToken = uploadToken;
        this.filePath = filePath;
        this.created = created;
        this.localFile = localFile;
        this.currentBlock = currentBlock;
        blockList = new ArrayList<>();
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

    public int getCurrentBlock() {
        return currentBlock;
    }

    public List<Block> getBlockList() {
        return blockList;
    }


    public boolean isFromTask(UploadTask uploadTask) {
        return TextUtils.equals(filePath, uploadTask.getFilePath()) && TextUtils.equals(this.localFile, uploadTask.getUploadFile().getPath());
    }


}
