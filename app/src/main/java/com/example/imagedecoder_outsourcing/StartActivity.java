package com.example.imagedecoder_outsourcing;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //퍼미션 체크
        setPermission();
        //권한요청 누르면 퍼미션요청 ( 처음 나오는 퍼미션 거절 시 대처방안 )
        findViewById(R.id.permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPermission();
            }
        });
    }

    /**
     * TedPermmision이라는 라이브러리로 작성한 코드입니다.
     */
    void setPermission()
    {
        //퍼미션 리스너
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "권한 허가", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);//퍼미션 확인 후 메인화면으로 넘어갑니다.
                startActivity(intent);
                finish();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        //퍼미션체크
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("어플을 사용하기 위해선 권한이 필요합니다.") //퍼미션 내용.
                .setDeniedMessage("권한요청을 누르시거나\n[설정] > [권한] 에서 권한을 허용할 수 있어요.") //퍼미션 거부시 내용
                .setPermissions(Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA) //퍼미션 할 내용
                .check();
    }
}