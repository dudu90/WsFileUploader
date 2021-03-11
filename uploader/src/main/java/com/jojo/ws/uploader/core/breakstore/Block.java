package com.jojo.ws.uploader.core.breakstore;

import android.util.Log;

import com.jojo.ws.uploader.core.slice.ByteArray;
import com.jojo.ws.uploader.core.slice.Slice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Block {
    private static final int PER_SLICE_SIZE = 64 * 1024;
    private static final int MAX_BLOCK_SIZE = 100 * 1024 * 1024;
    private static final int DEFAULT_BLOCK_SIZE = 4 * 1024 * 1024;
    private static final int DEFAULT_SLICE_SIZE = 256 * 1024;


    private static int sDefaultSliceSize = DEFAULT_SLICE_SIZE;
    private static long sDefaultBlockSize = DEFAULT_BLOCK_SIZE;

    private int index;
    private String ctx;
    private String checksum;
    private long crc32;
    private long offset;
    private long start;
    private long size;
    private int sliceSize;

    private int sliceIndex = 0;
    private ByteArray mByteArray;
    private RandomAccessFile randomAccessFile;
    private boolean uploadSuccess = false;
    private int errorCode;

    Block( int index, long start, long blockSize, int sliceSize) {
        this( index, null, null, 0, 0, start, blockSize, sliceSize);
    }

    public Block( int index, String ctx, String checksum, long crc32, long offset, long start, long size, int sliceSize) {
        this.ctx = ctx;
        this.checksum = checksum;
        this.crc32 = crc32;
        this.offset = offset;
        this.start = start;
        this.size = size;
        this.index = index;
        this.sliceSize = sliceSize;
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setRandomAccessFile(RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }


    public void setUploadSuccess(boolean uploadSuccess) {
        this.uploadSuccess = uploadSuccess;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isUploadSuccess() {
        return uploadSuccess;
    }

    public static Block[] blocks(File file) {
        long fileSize = file.length();
        int blockCount = (int) ((fileSize + sDefaultBlockSize - 1) / sDefaultBlockSize);// TODO: 2017/5/3
        Block[] blocks = new Block[blockCount];
        long blockSize = sDefaultBlockSize;
        for (int i = 0; i < blockCount; i++) {
            if (i + 1 == blockCount) {
                long remain = fileSize % sDefaultBlockSize;
                blockSize = remain == 0 ? sDefaultBlockSize : remain;// TODO: 2017/5/2 最后一块
            }
            blocks[i] = new Block(i, i * sDefaultBlockSize, blockSize, sDefaultSliceSize);
            //有多少块就创建多少空间
            // TODO: 2017/5/4 需要优化,等真正执行块上传时再创建
//				blocks[i].mByteArrayBuffer = new ByteArrayBuffer(sDefaultSliceSize);// TODO: 2017/5/2 初始化slice
        }
        return blocks;
    }

    public boolean hasNext() {
        return sliceIndex * getSliceSize() < size;
    }

    public Slice nextSlice() {
        return getSlice(sliceIndex++);
    }

    private Slice getSlice(int index) {
        //如果没有创建buffer，创建之
        if (mByteArray == null) {
            mByteArray = new ByteArray(getSliceSize());
        }
        long offset = start + index * getSliceSize();//从1开始
        if (index * getSliceSize() >= size) {
            return null;
        }
        int sliceSize = getSliceSize();
        if ((offset + getSliceSize()) > (start + size)) {//计算最后一片
            sliceSize = (int) (size % getSliceSize());
        }
        byte[] sliceData = mByteArray.toBuffer();
        Arrays.fill(sliceData, (byte) 0);
        if (sliceSize < getSliceSize()) {
            sliceData = new byte[sliceSize];
        }
        try {
            randomAccessFile.seek(offset);
            randomAccessFile.read(sliceData, 0, sliceSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sliceSize < getSliceSize()) { // 利用同一个缓存
            return new Slice(index * getSliceSize(), sliceData);//记录最后一片
        } else {
            return new Slice(index * getSliceSize(), mByteArray);
        }
    }


    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getSliceSize() {
        return sliceSize;
    }

    public void setSliceSize(int sliceSize) {
        this.sliceSize = sliceSize;
    }

    public String getCtx() {
        return ctx;
    }

    public void setCtx(String ctx) {
        this.ctx = ctx;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public long getCrc32() {
        return crc32;
    }

    public void setCrc32(long crc32) {
        this.crc32 = crc32;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

}
