package com.jojo.ws.uploader.utils;



public final class Configuration {

    /**
     * 分片上传版本 V1
     */
    public static int RESUME_UPLOAD_VERSION_V1 = 0;
    /**
     * 分片上传版本 V2
     */
    public static int RESUME_UPLOAD_VERSION_V2 = 1;


    /**
     * 断点上传时的分块大小(默认的分块大小, 不建议改变) 【已无效】
     */
    public static final int BLOCK_SIZE = 4 * 1024 * 1024;



}

