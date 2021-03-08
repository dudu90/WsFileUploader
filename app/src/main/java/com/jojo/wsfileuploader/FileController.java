package com.jojo.wsfileuploader;


import com.google.gson.Gson;
import com.jojo.ws.uploader.UploadCall;
import com.jojo.ws.uploader.UploadTask;
import com.jojo.ws.uploader.UploadToken;
import com.jojo.ws.uploader.UploaderCallback;
import com.jojo.ws.uploader.WsFileUploader;
import com.jojo.ws.uploader.core.connection.UploadConnection;
import com.jojo.ws.uploader.utils.Etag;
import com.jojo.wsuploader.okhttp.UploadOkhttpConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class FileController {
    static String BASE_URL = "https://api.2dland.cn";
    static final String UPLOAD_TOKEN = BASE_URL + "/v3/file/uploadToken";

    public void run(File file) {
        new Thread(() -> {
            try {
                final UploadToken uploadToken = genConnection(file);
                if (uploadToken == null) return;
                UploadTask uploadTask = new UploadTask.Builder(uploadToken, file.getPath()).build();
                new UploadCall(uploadTask).enqueue(new UploaderCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onProgress(int total, int current) {

                    }

                    @Override
                    public void onEnd() {

                    }
                });
            } catch (JSONException | IOException e) {

            }
        }).start();
    }

    public UploadToken genConnection(File file) throws IOException, JSONException {
        UploadOkhttpConnection connection = (UploadOkhttpConnection) WsFileUploader.with().uploadConnectionFactory().create(UPLOAD_TOKEN);
        connection.addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZGVudGl0eSI6MTc5ODQwLCJuYW1lIjoidGVzdGVyMzAxIiwidmVyc2lvbiI6MSwic3NpZCI6Ijg2NmRlNjNkLTE3MTAtNDcxYy04NTY2LWU0YTk5Y2QxMjQyOCIsImRldmljZSI6IkFuZHJvaWQgQ2hyb21lIFdlYlZpZXcgODEuMC40MDQ0LjEzOCIsInBlcm1pc3Npb24iOjMsInNpZ25UaW1lIjoxNjE0OTQxNjA4NDEyLCJsb2dpblRpbWUiOjE2MTQ5NDE2MDg0MDcsImxvZ2luQWRkciI6IjEwMy4xMjEuMTY0LjE5NCIsInJlZnJlc2hUaW1lIjowLCJjayI6IjE3OTg0MF81YWQ2NTZhMWNhYmEiLCJpYXQiOjE2MTQ5NDE2MDgsImV4cCI6MTYxNzUzMzYwOH0.ORulQc5FCvbDwCPf41EgEoE3abFVS37Dat6UigHNjlg");
        JSONObject jsonObject = new JSONObject();
        /*
         'path': taskModel.panPath,
                'name': taskModel.name,
                'hash': md5
         */
        /*
          json['uploadToken'] as String,
    json['type'] as String,
    json['filePath'] as String,
    json['created'] as bool,
    json['partUploadUrl'] as String,
    json['directUploadUrl'] as String,
    json['createInfo'] == null
         */
        jsonObject.put("path", "/");
        jsonObject.put("name", "uploadFile");
        jsonObject.put("hash", Etag.file(file));
        UploadConnection.Connected connected = connection.excuted(jsonObject.toString());
        if (connected.getResponseCode() == 200) {
            UploadToken uploadToken = new Gson().fromJson(connected.getResponseString(), UploadToken.class);
            return uploadToken;
        } else {
            return null;
        }
    }
}
