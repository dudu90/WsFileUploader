package com.jojo.wsfileuploader;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jojo.ws.uploader.UploadCall;
import com.jojo.ws.uploader.UploadTask;
import com.jojo.ws.uploader.UploaderCallback;
import com.jojo.ws.uploader.WsFileUploader;
import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.end.EndCause;

import java.util.ArrayList;
import java.util.List;

public class UploadListController {
    private List<UploadCall> callList = new ArrayList<>();

    private UploaderCallback uploaderCallback;
    private CallBack callBack;

    void setCallBack(final CallBack callBack) {
        this.callBack = callBack;
    }

    List<UploadCall> initTasks(String file) {
        final UploadTask uploadTask = new UploadTask.Builder("/", file).build();
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

