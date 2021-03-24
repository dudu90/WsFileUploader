package com.jojo.wsfileuploader;

import android.util.Log;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jojo.ws.uploader.UploadCall;
import com.jojo.ws.uploader.UploadTask;
import com.jojo.ws.uploader.UploaderCallback;
import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.end.EndCause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadListController {
    static String BASE_URL = "https://api.2dland.cn";
    static final String UPLOAD_TOKEN = BASE_URL + "/v3/file/uploadToken";
    private List<UploadCall> callList = new ArrayList<>();

    private UploaderCallback uploaderCallback;
    private CallBack callBack;

    void setCallBack(final CallBack callBack) {
        this.callBack = callBack;
    }

    List<UploadCall> initTasks(String file) {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZGVudGl0eSI6MTc5ODQwLCJuYW1lIjoidGVzdGVyMzAxIiwidmVyc2lvbiI6MSwic3NpZCI6Ijg2NmRlNjNkLTE3MTAtNDcxYy04NTY2LWU0YTk5Y2QxMjQyOCIsImRldmljZSI6IkFuZHJvaWQgQ2hyb21lIFdlYlZpZXcgODEuMC40MDQ0LjEzOCIsInBlcm1pc3Npb24iOjMsInNpZ25UaW1lIjoxNjE0OTQxNjA4NDEyLCJsb2dpblRpbWUiOjE2MTQ5NDE2MDg0MDcsImxvZ2luQWRkciI6IjEwMy4xMjEuMTY0LjE5NCIsInJlZnJlc2hUaW1lIjowLCJjayI6IjE3OTg0MF81YWQ2NTZhMWNhYmEiLCJpYXQiOjE2MTQ5NDE2MDgsImV4cCI6MTYxNzUzMzYwOH0.ORulQc5FCvbDwCPf41EgEoE3abFVS37Dat6UigHNjlg");
        final UploadTask uploadTask = new UploadTask.Builder("/", file)
                .tokenGetUrl(UPLOAD_TOKEN)
                .serverFileName("test.db")
                .tokenGetHeader(header)
                .build();
        Log.d("initTask--->", uploadTask.getId() + "");
        final UploadCall uploadCall = new UploadCall(uploadTask);
        uploaderCallback = new UploaderCallback() {
            @Override
            public void onStart(UploadTask uploadTask) {
                callList.add(uploadCall);
                callBack.onPull();
            }

            @Override
            public void onBlockUploaded(UploadTask uploadTask, List<Block> blocks) {
                callBack.onPull();
            }

            @Override
            public void onProgress(UploadTask uploadTask, long total, long current) {
                callBack.onPull();
            }

            @Override
            public void onEnd(UploadTask uploadTask, EndCause cause, Exception errorCause) {
                if (errorCause != null) {
                    errorCause.printStackTrace();
                }
                callBack.onPull();
            }
        };
        uploadCall.enqueue(uploaderCallback);
        return callList;
    }


    void bindToAdapter(BaseViewHolder baseViewHolder) {

    }

    interface CallBack {
        void onPull();
    }
}

