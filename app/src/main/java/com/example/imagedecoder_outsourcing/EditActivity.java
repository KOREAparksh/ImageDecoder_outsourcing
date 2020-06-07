package com.example.imagedecoder_outsourcing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 편집액티비티, 소캣액티비티 코드들은 넘어갈때 자기가 카메라에서 왔는지 갤러리에서 왔는지 intent로 값을 가지고 갑니다.
 *  위 액티비티들은 하나이니 카메라에서 넘어갈때, 갤러리에서 넘어갈 때를 잘 구분하셔서 코드를 작성하세요.
 */

public class EditActivity extends AppCompatActivity {

    final static String EDIT_CODE = "editCode";
    ImageView imageView;
    Button button;

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
        button = (Button)findViewById(R.id.but);


    }

}