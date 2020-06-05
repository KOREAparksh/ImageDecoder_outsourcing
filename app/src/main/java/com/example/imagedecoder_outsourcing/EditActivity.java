package com.example.imagedecoder_outsourcing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    final static String EDIT_CODE = "editCode";
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        String str = intent.getExtras().getString(EDIT_CODE);

        /**
         * 갤러리에서 오면 str은 gallery, 카메라에서 오면 str은 camera로 나옵니다.
         * 각 액티비티에서 이미지를 받아오시거나 링크 얻어와서 이미지 여시면 됩니다.
         */
        imageView = (ImageView)findViewById(R.id.testImage);

    }
}