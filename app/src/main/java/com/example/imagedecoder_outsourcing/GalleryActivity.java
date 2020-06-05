package com.example.imagedecoder_outsourcing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {


    final static String EDIT_CODE = "gallery";

    private Button exit, edit, choose;
    private CropImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initView();

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    imageView.setImageBitmap(img);
            } catch(Exception e){

            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void initView() {
        imageView = (CropImageView) findViewById(R.id.cropImageView_Gallery);
        exit = (Button) findViewById(R.id.exit);
        edit = (Button) findViewById(R.id.edit);
        choose = (Button) findViewById(R.id.choose);

        setListener();
    }

    private void setListener() {
        edit.setOnClickListener(this);
        exit.setOnClickListener(this);
        choose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit:
                finish();
                break;
            case R.id.edit:
                Intent intent_choose = new Intent(GalleryActivity.this, EditActivity.class);
                intent_choose.putExtra(EditActivity.EDIT_CODE, EDIT_CODE);
                startActivity(intent_choose);
                break;
            case R.id.choose:
                finish();
                break;
        }
    }
}