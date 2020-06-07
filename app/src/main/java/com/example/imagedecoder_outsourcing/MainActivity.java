package com.example.imagedecoder_outsourcing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton cameraButton; //카메라버튼
    private ImageButton galleryButton; //갤러리버튼
    private ImageButton fileButton; //file 버튼
    private TextView opensource; // 오픈소스 버


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView(); //뷰 초기화
        setListener(); //리스너 달기
        checkExternalStorage(); //파일 열기 전 퍼미션 마지막으로 확인.
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
                intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
                break;
            case R.id.fileButon:
                openFolder();
                break;
            case R.id.opensource:
                intent = new Intent(MainActivity.this, OpensourceActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 아래 두 메소드(getSaveFolder, openFolder)는 기존에 주신 메소드 참고하였습니다. 바뀐것들 좀 있습니다.
     */
    private File getSaveFolder(){

        File dir = new File(Environment.getExternalStorageDirectory() + "/imageDecoderDownload");// 공개파일

        if(!dir.exists()){
            dir.mkdirs();
            Toast.makeText(this,"폴더 생성 성공 : 휴대폰 내장메모리/imageDecoderDownload", Toast.LENGTH_SHORT).show();
        }else{

        }
        return dir;
    }

    private void openFolder(){
        getSaveFolder();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse("storage");
        intent.setDataAndType(uri, "text/*");
        startActivity(Intent.createChooser(intent, "Open folder"));
    }


    /**
            * 외부메모리 상태 확인 메서드
     */
    boolean checkExternalStorage() {
        String state = Environment.getExternalStorageState();
        // 외부메모리 상태
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // 읽기 쓰기 모두 가능
            Log.d("FILE", "외부메모리 읽기 쓰기 모두 가능");
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            //읽기전용
            Log.d("FILE", "외부메모리 읽기만 가능");
            return false;
        } else {
            // 읽기쓰기 모두 안됨
            Log.d("FILE", "외부메모리 읽기쓰기 모두 안됨 : "+ state);
            return false;
        }
    }

}