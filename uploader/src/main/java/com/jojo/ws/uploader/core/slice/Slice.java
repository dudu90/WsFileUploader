package com.jojo.ws.uploader.core.slice;

public class Slice {
    public static final int SLICE_MAX_RETRY = 2;
    private String ctx;
    private byte[] mData;
    private long mOffset;
    private ByteArray mByteArray;

    /**
     * 上传完会校验正确性，如果不正确则需要重试
     * 重试次数
     */
    private int mRetry;

    public Slice(long offset, ByteArray byteArray) {
        this.mOffset = offset;
        this.mByteArray = byteArray;
    }

    public Slice(long offset, byte[] data) {
        mOffset = offset;
        mData = data;
    }

    public long size() {
        if (mByteArray != null) {
            return mByteArray.size();
        } else if (mData != null) {
            return mData.length;
        }
        return 0;
    }

    public String getCtx() {
        return ctx;
    }

    public void setCtx(String ctx) {
        this.ctx = ctx;
    }

    public byte[] toByteArray() {
        if (null != mByteArray) {
            return mByteArray.toBuffer();
        } else if (null != mData) {
            return mData;
        }
        return new byte[0];
    }

    public long getOffset() {
        return mOffset;
    }

    public int getRetry() {
        return mRetry;
    }

    public void setRetry(int retry) {
        this.mRetry = retry;
    }
}
