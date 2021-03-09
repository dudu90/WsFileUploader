package com.jojo.ws.uploader.core.exception;

public class HttpUploadException extends Exception {
    private int blockIndex;
    private String message;
    private int errorCode;

    public HttpUploadException(int blockIndex, String message, int errorCode) {
        this.blockIndex = blockIndex;
        this.message = message;
        this.errorCode = errorCode;
    }
}
