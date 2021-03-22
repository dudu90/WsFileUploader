package com.jojo.wsfileuploader;

import android.util.Log;

import com.google.gson.Gson;
import com.jojo.ws.uploader.Interceptor;
import com.jojo.ws.uploader.UploadToken;
import com.jojo.ws.uploader.WsFileUploader;
import com.jojo.ws.uploader.core.connection.UploadConnection;
import com.jojo.ws.uploader.utils.Etag;
import com.jojo.wsuploader.okhttp.UploadOkhttpConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

// return new UploadTask(uploadToken.uploadToken, uploadToken.type, new File(filePath), uploadToken.created, uploadToken.partUploadUrl, uploadToken.filePath, uploadToken.directUploadUrl);
public class RequestUploadUrlInterceptor implements Interceptor {
    static String BASE_URL = "https://api.2dland.cn";
    static final String UPLOAD_TOKEN = BASE_URL + "/v3/file/uploadToken";

    @Override
    public void intercept(Chain chain) throws IOException {
        try {
            final UploadToken uploadToken = genConnection(chain.task().getUploadFile());
            Log.d("intercept_token--->", uploadToken.toString() + chain.task().getId());
            chain.task().setUploadToken(uploadToken.uploadToken);
            chain.task().setType(uploadToken.type);
            chain.task().setCreated(uploadToken.created);
            chain.task().setPartUploadUrl(uploadToken.partUploadUrl);
            chain.task().setDirectUploadUrl(uploadToken.directUploadUrl);
            chain.proceed();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UploadToken genConnection(File file) throws IOException, JSONException {
        UploadOkhttpConnection connection = (UploadOkhttpConnection) WsFileUploader.with().uploadConnectionFactory().create(UPLOAD_TOKEN);
        connection.addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZGVudGl0eSI6MTc5ODQwLCJuYW1lIjoidGVzdGVyMzAxIiwidmVyc2lvbiI6MSwic3NpZCI6Ijg2NmRlNjNkLTE3MTAtNDcxYy04NTY2LWU0YTk5Y2QxMjQyOCIsImRldmljZSI6IkFuZHJvaWQgQ2hyb21lIFdlYlZpZXcgODEuMC40MDQ0LjEzOCIsInBlcm1pc3Npb24iOjMsInNpZ25UaW1lIjoxNjE0OTQxNjA4NDEyLCJsb2dpblRpbWUiOjE2MTQ5NDE2MDg0MDcsImxvZ2luQWRkciI6IjEwMy4xMjEuMTY0LjE5NCIsInJlZnJlc2hUaW1lIjowLCJjayI6IjE3OTg0MF81YWQ2NTZhMWNhYmEiLCJpYXQiOjE2MTQ5NDE2MDgsImV4cCI6MTYxNzUzMzYwOH0.ORulQc5FCvbDwCPf41EgEoE3abFVS37Dat6UigHNjlg");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path", "/");
        jsonObject.put("name", "uploadFile");
        jsonObject.put("hash", Etag.file(file));
        UploadConnection.Connected connected = connection.excuted(jsonObject.toString());
        if (connected.getResponseCode() == 200) {
            String result = connected.getResponseString();
            Log.d("genConnection--->", result);
            UploadToken uploadToken = new Gson().fromJson(result, UploadToken.class);
            return uploadToken;
        } else {
            return null;
        }
    }

}