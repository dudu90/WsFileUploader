package com.jojo.ws.uploader.core.interceptors;

import com.jojo.ws.uploader.Interceptor;
import com.jojo.ws.uploader.WsFileUploader;
import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.connection.UploadConnection;
import com.jojo.ws.uploader.core.end.EndCause;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class BlockMergeInterceptor implements Interceptor {
    @Override
    public void intercept(Chain chain) throws IOException {
        final List<Block> blocks = chain.task().getBreakInfo().getBlockList();
        Collections.sort(blocks, (left, right) -> Integer.valueOf(left.getIndex()).compareTo(right.getIndex()));
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < blocks.size(); i++) {
            if (i == blocks.size() - 1) {
                buffer.append(blocks.get(i).getCtx());
            } else {
                buffer.append(blocks.get(i).getCtx());
                buffer.append(",");
            }
        }
        if (chain.call().isInterrupt()) {
            return;
        }
        final String uploadBatch = chain.task().getUploadBatch();
        final String url = chain.task().getPartUploadUrl() + "/mkfile/" + chain.task().getUploadFile().length();
        UploadConnection connection = WsFileUploader.with().uploadConnectionFactory().create(url);
        connection.addHeader("UploadBatch", uploadBatch);
        connection.addHeader("Authorization", chain.task().getUploadToken());
        connection.addHeader("Content-Type", "text/plain");
        UploadConnection.Connected connected = connection.postText(buffer.toString());
        if (connected.getResponseCode() == 200) {
            WsFileUploader.with().handlerDispatcher().postMain(() -> chain.call().uploaderCallback().onEnd(chain.task(), EndCause.COMPLETED, null));
        } else {
            try {
                JSONObject jsonObject = new JSONObject(connected.getResponseString());
            } catch (JSONException e) {

            }
        }
    }
}
