package com.jojo.wsfileuploader;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jojo.ws.uploader.WsFileUploader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.blockUpload).setOnClickListener(view -> {
            final Intent intent = new Intent(MainActivity.this, BlockUploadActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.listUpload).setOnClickListener(view -> {
            final Intent intent = new Intent(MainActivity.this, UploadListActivity.class);
            startActivity(intent);
        });
        WsFileUploader.with().interceptorDispatcher().addInterceptor(new RequestUploadUrlInterceptor());
    }

}
