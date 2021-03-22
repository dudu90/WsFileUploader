package com.jojo.wsfileuploader;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jojo.ws.uploader.UploadCall;
import com.jojo.ws.uploader.WsFileUploader;

import org.jetbrains.annotations.NotNull;

public class UploadListAdapter extends BaseQuickAdapter<UploadCall, BaseViewHolder> {
    private UploadListController uploadListController;

    public UploadListAdapter(UploadListController uploadListController) {
        super(R.layout.item_layout_upload);
        this.uploadListController = uploadListController;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, UploadCall uploadCall) {
        final Button start = baseViewHolder.findView(R.id.action);
        final Button end = baseViewHolder.findView(R.id.actionEnd);
        final ProgressBar contentProgress = baseViewHolder.findView(R.id.contentProgress);
        double progress = ((double) uploadCall.uploadTask().getProgress().doubleValue() / (double) uploadCall.uploadTask().getUploadFile().length()) * 100;
        contentProgress.setProgress((int) progress);
        end.setVisibility(View.GONE);
//        baseViewHolder.getView(R.id.actionEnd).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("bindToAdapter--->", uploadCall.uploadTask().getId() + "");
//                WsFileUploader.with().uploadDispatcher().cancel(uploadCall.uploadTask().getId());
//            }
//        });
    }
}
