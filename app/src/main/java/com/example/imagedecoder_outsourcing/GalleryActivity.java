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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 편집액티비티, 소캣액티비티 코드들은 넘어갈때 자기가 카메라에서 왔는지 갤러리에서 왔는지 intent로 값을 가지고 갑니다.
 * 위 액티비티들은 하나이니 카메라에서 넘어갈때, 갤러리에서 넘어갈 때를 잘 구분하셔서 코드를 작성하세요.
 */

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {


    final static String EDIT_CODE = "gallery";
    final static String SOCKET_CODE = "gallery";

    private Button exit, edit, choose;
    private ImageButton rotateLeft, rotateRight;
    static CropImageView imageView;

    private Bitmap bitmap_temp = null; // 회전 시 회전 전의 bitmap 저장.
    int rotateDegree=0;

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

                    bitmap_temp = img;// 회전 시 기존 위치를 알기위해
                    imageView.setImageBitmap(img);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    void setRotateDegree(int angle)
    {
        rotateDegree = angle;
        if(rotateDegree >= 360) rotateDegree =0;
        else if (rotateDegree <= -360) rotateDegree=0;
    }

    public Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        setRotateDegree(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void initView() {
        imageView = (CropImageView) findViewById(R.id.cropImageView_Gallery);
        exit = (Button) findViewById(R.id.exit);
        edit = (Button) findViewById(R.id.edit);
        rotateLeft = (ImageButton) findViewById(R.id.rotateLeft);
        rotateRight = (ImageButton) findViewById(R.id.rotateRight);
        choose = (Button) findViewById(R.id.choose);

        setListener();
    }

    private void setListener() {
        edit.setOnClickListener(this);
        exit.setOnClickListener(this);
        choose.setOnClickListener(this);
        rotateLeft.setOnClickListener(this);
        rotateRight.setOnClickListener(this);
    }

    void saveCropImage(){

        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getCroppedImage();
        //File file = new File("/DCIM/Camera/image.jpg");

        //시간으로 파일명 생성
        long time = System.currentTimeMillis();  //시간 받기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        //포멧 변환  형식 만들기
        Date dd = new Date(time);  //받은 시간을 Date 형식으로 바꾸기
        String strTime = sdf.format(dd); //Data 정보를 포멧 변환하기

        File root = Environment.getExternalStorageDirectory();
        File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/image"+strTime+".jpg");
        try
        {
            cachePath.createNewFile();
            FileOutputStream ostream = new FileOutputStream(cachePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
            Toast.makeText(this, "이미지 저장 완료", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit:
                finish();
                break;
            case R.id.edit:
                Intent intent_edit = new Intent(GalleryActivity.this, EditActivity.class);
                intent_edit.putExtra(EditActivity.EDIT_CODE, EDIT_CODE);

                startActivity(intent_edit);
                break;
            case R.id.rotateLeft:
                //imageView.rotateImage(90);
                imageView.setImageBitmap(rotateImage(bitmap_temp, rotateDegree - 10));
                break;
            case  R.id.rotateRight:
                imageView.setImageBitmap(rotateImage(bitmap_temp, rotateDegree + 10));
                break;
            case R.id.choose:
                saveCropImage();
                Intent intent_choose = new Intent(GalleryActivity.this, SocketActivity.class);
                intent_choose.putExtra(SocketActivity.SOCKET_CODE, SOCKET_CODE);
                startActivity(intent_choose);
                finish();
                break;
        }
    }
}