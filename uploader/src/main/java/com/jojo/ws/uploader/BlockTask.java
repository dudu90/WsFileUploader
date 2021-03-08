package com.jojo.ws.uploader;

import android.util.Log;

import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.connection.UploadConnection;
import com.jojo.ws.uploader.core.slice.Slice;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;

public class BlockTask implements Callable<Block> {
    private final int SLICE_RETRY_COUNT = 3;
    private final Interceptor.Chain chain;
    private final int index;
    private Block block;
    private RandomAccessFile randomAccessFile;

    public BlockTask(final int index, Interceptor.Chain chain, Block block) {
        this.index = index;
        this.chain = chain;
        this.block = block;
    }

    private boolean execute(int retry) {
        try {
            final Slice firstSlice = block.nextSlice();
            String url = chain.task().getPartUploadUrl() + "/mkblk/" + block.getSize() + "/" + block.getIndex();
            UploadConnection connection = WsFileUploader.with().uploadConnectionFactory().create(url);
            connection.addHeader("UploadBatch", chain.task().getUploadBatch());
            connection.addHeader("Authorization", chain.task().getUploadToken());
            UploadConnection.Connected connected = connection.postExcuted(firstSlice.toByteArray());
            if (connected.getResponseCode() == 200) {
                JSONObject jsonObject = new JSONObject(connected.getResponseString());
                block.setCtx(jsonObject.getString("ctx"));
                block.setChecksum(jsonObject.getString("checksum"));
                block.setCrc32(jsonObject.getLong("crc32"));
                block.setOffset(jsonObject.getLong("offset"));
                return uploadBlock(jsonObject.getString("ctx"));
            } else {
                throw new IllegalStateException("mkblk failed.");
            }
        } catch (IllegalStateException | IOException e) {
            if (retry < SLICE_RETRY_COUNT) {
                return execute(retry--);
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
    }

    private boolean uploadBlock(String ctx) {
        while (block.hasNext()) {
            Slice slice = block.nextSlice();
            slice.setCtx(ctx);
            ctx = uploadSlice(SLICE_RETRY_COUNT, slice);
            block.setCtx(ctx);
        }
        return true;
    }

    private String uploadSlice(int retry, Slice slice) {
        final String url = chain.task().getPartUploadUrl() + "/bput/" + slice.getCtx() + "/" + slice.getOffset();
        try {
            UploadConnection uploadConnection = WsFileUploader.with().uploadConnectionFactory().create(url);
            uploadConnection.addHeader("UploadBatch", chain.task().getUploadBatch());
            uploadConnection.addHeader("Authorization", chain.task().getUploadToken());
            UploadConnection.Connected connected = uploadConnection.postExcuted(slice.toByteArray());
            if (connected.getResponseCode() == 200) {
                JSONObject jsonObject = new JSONObject(connected.getResponseString());
                return jsonObject.getString("ctx");
            } else {
                if (retry < SLICE_RETRY_COUNT) {
                    return uploadSlice(retry--, slice);
                } else {
                    return null;
                }
            }
        } catch (IOException e) {
            if (retry < SLICE_RETRY_COUNT) {
                return uploadSlice(retry--, slice);
            } else {
                return null;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Block call() throws Exception {
        if (randomAccessFile == null) {
            try {
                randomAccessFile = new RandomAccessFile(chain.task().getUploadFile(), "r");
                block.setRandomAccessFile(randomAccessFile);
            } catch (FileNotFoundException e) {
                block.setUploadSuccess(false);
//                return block;
            }
        }
        final boolean success = execute(SLICE_RETRY_COUNT);
        block.setUploadSuccess(success);
        return block;
    }
}
