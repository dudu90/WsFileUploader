package com.jojo.wsfileuploader;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thl.filechooser.FileChooser;

public class UploadListActivity extends AppCompatActivity {
    private RecyclerView taskList;
    private UploadListController uploadListController;
    private UploadListAdapter uploadListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_list);
        findViewById(R.id.action).setOnClickListener(view -> {
            requestPermissins(new PermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    FileChooser fileChooser = new FileChooser(UploadListActivity.this, new FileChooser.FileChoosenListener() {
                        @Override
                        public void onFileChoosen(String s) {
                            uploadListAdapter.setNewInstance(uploadListController.initTasks(s));
                            uploadListAdapter.notifyDataSetChanged();
                        }
                    });
                    fileChooser.open();
                }

                @Override
                public void onPermissionDenied(String[] deniedPermissions) {
                    Toast.makeText(UploadListActivity.this, "未获取到存储权限", Toast.LENGTH_SHORT).show();
                }
            });
        });
        taskList = findViewById(R.id.taskList);
        taskList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        uploadListController = new UploadListController();
        uploadListController.setCallBack(() -> {
            uploadListAdapter.notifyDataSetChanged();
        });
        uploadListAdapter = new UploadListAdapter(uploadListController);
        taskList.setAdapter(uploadListAdapter);
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
