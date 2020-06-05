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



        imageView = (ImageView)findViewById(R.id.testImage);

    }
}