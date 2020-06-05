package com.example.imagedecoder_outsourcing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {


    final static int CAMERA_REQ = 100;

    private CropImageView cropImage;
    private Button exit, choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initView();
        setListener();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQ);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQ){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                cropImage.setImageBitmap(imageBitmap);
            }
            else if(resultCode == RESULT_CANCELED)
                finish();
        }
    }

    private void initView()
    {
        //크롭이미지뷰는 사진 크기를 조절할 수 있는 이미지뷰로 간단한 방법으로 인기가 많음.
        cropImage = (CropImageView) findViewById(R.id.cropImageView_Camera);
        exit = (Button) findViewById(R.id.exit);
        choose = (Button) findViewById(R.id.choose);
    }

    private void setListener() {
        exit.setOnClickListener(this);
        choose.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exit:
                finish();
                break;
            case R.id.choose:
                Intent intent = new Intent();
                intent.putExtra("Image", cropImage.getCroppedImage());
                Log.d("CameraActivity_", "image : " + cropImage.getCroppedImage());
                setResult(RESULT_OK, intent);
                finish();
        }
    }
}