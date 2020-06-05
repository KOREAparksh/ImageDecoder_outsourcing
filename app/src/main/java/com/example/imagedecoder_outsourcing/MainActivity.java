package com.example.imagedecoder_outsourcing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton cameraButton; //카메라버튼
    private ImageButton galleryButton; //갤러리버튼
    private ImageButton fileButton; //file 버튼
    private TextView opensource;

    ImageView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = (ImageView)findViewById(R.id.testImage);

        initView(); //뷰 초기화
        setListener(); //리스너 달기

    }

    //리스너 달기
    private void setListener() {
        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        fileButton.setOnClickListener(this);
        opensource.setOnClickListener(this);
    }

    // 뷰 초기화
    private void initView() {
        cameraButton = (ImageButton)findViewById(R.id.cameraButton);
        galleryButton = (ImageButton)findViewById(R.id.galleryButton);
        fileButton = (ImageButton)findViewById(R.id.fileButon);
        opensource = (TextView)findViewById(R.id.opensource);
    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()){
            case R.id.cameraButton:
                intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.galleryButton:
                break;
            case R.id.fileButon:
                break;
            case R.id.opensource:
                intent = new Intent(MainActivity.this, OpensourceActivity.class);
                startActivity(intent);
                break;
        }
    }
}