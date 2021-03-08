package com.jojo.wsfileuploader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jojo.ws.uploader.UploadTask;
import com.jojo.ws.uploader.UploaderCallback;
import com.jojo.ws.uploader.core.breakstore.Block;
import com.thl.filechooser.FileChooser;


import java.io.File;
import java.util.List;

public class BlockUploadActivity extends AppCompatActivity {
    private Button upload;
    private TextView uploadFile;
    private RecyclerView blockList;
    private FileController fileController;
    private BlockAdapter blockAdapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_upload);
        blockList = findViewById(R.id.blockList);
        uploadFile = findViewById(R.id.uploadFile);
        upload = findViewById(R.id.upload);
        fileController = new FileController();
        blockAdapter = new BlockAdapter();
        blockList.setAdapter(blockAdapter);
        upload.setOnClickListener(view -> requestPermissins(new PermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                FileChooser fileChooser = new FileChooser(BlockUploadActivity.this, filePath -> {
                    uploadFile.setText(filePath);
                    fileController.run(new File(filePath), new UploaderCallback() {

                        @Override
                        public void onStart(UploadTask uploadTask) {
                            blockAdapter.setNewInstance(uploadTask.getBreakInfo().getBlockList());
                        }

                        @Override
                        public void onBlockUploaded(List<Block> blocks) {
                            blockAdapter.setNewInstance(blocks);
                            blockAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onProgress(int total, int current) {

                        }

                        @Override
                        public void onEnd() {

                        }
                    });
                });
                fileChooser.open();
            }

            @Override
            public void onPermissionDenied(String[] deniedPermissions) {
                Toast.makeText(BlockUploadActivity.this, "未获取到存储权限", Toast.LENGTH_SHORT).show();
            }
        }));
        blockList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 20));
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