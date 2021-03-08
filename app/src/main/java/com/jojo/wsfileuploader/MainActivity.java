package com.jojo.wsfileuploader;


import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.thl.filechooser.FileChooser;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    TextView path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = findViewById(R.id.path);
        findViewById(R.id.choseFile).setOnClickListener(view -> {
            requestPermissins(new PermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    FileChooser fileChooser = new FileChooser(MainActivity.this, new FileChooser.FileChoosenListener() {
                        @Override
                        public void onFileChoosen(String filePath) {
                            path.setText(filePath);
                            new FileController().run(new File(filePath));
                        }
                    });
                    fileChooser.open();
                }

                @Override
                public void onPermissionDenied(String[] deniedPermissions) {
                    Toast.makeText(MainActivity.this, "未获取到存储权限", Toast.LENGTH_SHORT).show();
                }
            });
        });
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
