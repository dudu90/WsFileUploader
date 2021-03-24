package com.jojo.ws.uploader.core.interceptors;

import com.jojo.ws.uploader.Interceptor;
import com.jojo.ws.uploader.UploadTask;
import com.jojo.ws.uploader.UploadToken;
import com.jojo.ws.uploader.WsFileUploader;
import com.jojo.ws.uploader.core.connection.UploadConnection;
import com.jojo.ws.uploader.core.end.EndCause;
import com.jojo.ws.uploader.core.exception.UploadException;
import com.jojo.ws.uploader.utils.Etag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class BaseInfoInterceptor implements Interceptor {
    private static final String PATH = "path";
    private static final String NAME = "name";
    private static final String HASH = "hash";

    @Override
    public void intercept(Chain chain) throws IOException {
        try {
            final UploadToken uploadToken = uploadToken(chain.task());
            if (uploadToken == null) {
                WsFileUploader.with().handlerDispatcher().postMain(() -> {
                    if (chain.call().uploaderCallback() != null) {
                        chain.call().uploaderCallback().onEnd(chain.task(), EndCause.ERROR, new UploadException("request token fail."));
                    }
                });
            } else {
                chain.task().setUploadToken(uploadToken.uploadToken);
                chain.task().setType(uploadToken.type);
                chain.task().setCreated(uploadToken.created);
                chain.task().setPartUploadUrl(uploadToken.partUploadUrl);
                chain.task().setDirectUploadUrl(uploadToken.directUploadUrl);
                chain.proceed();
            }
        } catch (IOException | JSONException e) {
            WsFileUploader.with().handlerDispatcher().postMain(() -> {
                if (chain.call().uploaderCallback() != null) {
                    chain.call().uploaderCallback().onEnd(chain.task(), EndCause.ERROR, e);
                }
            });
        }
    }

    public UploadToken uploadToken(final UploadTask task) throws IOException, JSONException {
        final UploadConnection connection = WsFileUploader.with().uploadConnectionFactory().create(task.getTokenGetUrl());
        final Map<String, String> header = task.getTokenGetHeader();
        if (header != null && !header.isEmpty()) {
            final Set<String> keySet = header.keySet();
            for (String key : keySet) {
                connection.addHeader(key, header.get(key));
            }
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(PATH, task.getFilePath());
        jsonObject.put(NAME, task.getServerFileName());
        jsonObject.put(HASH, Etag.file(task.getUploadFile()));
        UploadConnection.Connected connected = connection.excuted(jsonObject.toString());
        if (connected.getResponseCode() == 200) {
            final JSONObject result = new JSONObject(connected.getResponseString());
            final UploadToken uploadToken = new UploadToken();
            uploadToken.uploadToken = result.getString("uploadToken");
            uploadToken.type = result.getString("type");
            uploadToken.filePath = result.getString("filePath");
            uploadToken.created = result.getBoolean("created");
            uploadToken.partUploadUrl = result.getString("partUploadUrl");
            uploadToken.directUploadUrl = result.getString("directUploadUrl");
            return uploadToken;
        } else {
            return null;
        }
    }


}
