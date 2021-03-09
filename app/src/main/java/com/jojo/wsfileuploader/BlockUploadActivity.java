package com.jojo.wsfileuploader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jojo.ws.uploader.UploadCall;
import com.jojo.ws.uploader.UploadTask;
import com.jojo.ws.uploader.UploadToken;
import com.jojo.ws.uploader.UploaderCallback;
import com.jojo.ws.uploader.WsFileUploader;
import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.end.EndCause;
import com.thl.filechooser.FileChooser;


import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockUploadActivity extends AppCompatActivity {
    private Button upload;
    private Button cancel;
    private TextView uploadFile;
    private RecyclerView blockList;
    private ProgressBar contentProgress;
    private FileController fileController;
    private BlockAdapter blockAdapter;
    private UploadTask uploadTask;
    private UploadCall uploadCall;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AtomicInteger integer = new AtomicInteger(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_upload);
        blockList = findViewById(R.id.blockList);
        uploadFile = findViewById(R.id.uploadFile);
        upload = findViewById(R.id.upload);
        cancel = findViewById(R.id.cancel);
        contentProgress = findViewById(R.id.contentProgress);
        fileController = new FileController();
        blockAdapter = new BlockAdapter();
        blockList.setAdapter(blockAdapter);
        contentProgress.setMax(100);
        cancel.setOnClickListener(view -> {
            WsFileUploader.with().uploadDispatcher().cancel(uploadTask.getId());
        });
        upload.setOnClickListener(view -> {
            requestPermissins(new PermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    FileChooser fileChooser = new FileChooser(BlockUploadActivity.this, filePath -> {
                        uploadFile.setText(filePath);
                        fileController.startBlockUpload(new File(filePath), uploadToken -> {
                            runOnUiThread(() -> {
                                if (uploadToken == null) return;
                                uploadTask = new UploadTask.Builder(uploadToken, filePath).build();
                                uploadCall = new UploadCall(uploadTask);
                                uploadCall.enqueue(new UploaderCallback() {

                                    @Override
                                    public void onStart(UploadTask uploadTask) {
                                        blockAdapter.setNewInstance(uploadTask.getBreakInfo().getBlockList());
                                    }

                                    @Override
                                    public void onBlockUploaded(UploadTask uploadTask, List<Block> blocks) {
                                        blockAdapter.setNewInstance(blocks);
                                        blockAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onProgress(UploadTask uploadTask, long total, long current) {
                                        double progress = ((double) current / (double) total) * 100;
                                        contentProgress.setProgress((int) progress);
                                        Log.d("onProgress--->", total + "," + current + "," + progress);
                                    }

                                    @Override
                                    public void onEnd(UploadTask uploadTask, EndCause endCause, Exception exception) {
                                            
                                    }
                                });
                            });
                        });
                    });
                    fileChooser.open();
                }

                @Override
                public void onPermissionDenied(String[] deniedPermissions) {
                    Toast.makeText(BlockUploadActivity.this, "未获取到存储权限", Toast.LENGTH_SHORT).show();
                }
            });
        });
        blockList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 30));
    }

    private void requestPermissins(PermissionUtils.OnPermissionListener mOnPermissionListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mOnPermissionListener.onPermissionGranted();
            return;
        }
        String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        PermissionUtils.requestPermissions(this, 0
                , permissions, mOnPermissionListener);
    }
}