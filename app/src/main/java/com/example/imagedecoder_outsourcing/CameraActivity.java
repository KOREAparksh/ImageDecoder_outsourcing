package com.example.imagedecoder_outsourcing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 편집액티비티, 소캣액티비티 코드들은 넘어갈때 자기가 카메라에서 왔는지 갤러리에서 왔는지 intent로 값을 가지고 갑니다.
     *  위 액티비티들은 하나이니 카메라에서 넘어갈때, 갤러리에서 넘어갈 때를 잘 구분하셔서 코드를 작성하세요.
     */

    final static int REQUEST_TAKE_PHOTO = 1; // 사진찍기위한 REQUEST_CODE
    final static String EDIT_CODE = "camera"; //편집액티비티 코드.
    final static String SOCKET_CODE = "camera";

    private String mCurrentPhotoPath;
    static CropImageView cropImage;
    private Button exit, edit, choose;
    private ImageButton rotateLeft, rotateRight;
    private Bitmap bitmap_temp = null; // 회전 시 회전 전의 bitmap 저장.
    int rotateDegree=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initView();
        setListener();

        dispatchTakePictureIntent();
    }

    //이미지를 파일로 저장.
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //카메라에서 원본사진을 가져오기 위한 인텐트
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.imagedecoder_outsourcing.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)  finish();
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

                            Bitmap rotatedBitmap = null;
                            switch(orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }

                            bitmap_temp = rotatedBitmap;// 회전 시 기존 위치를 알기위해
                            cropImage.setImageBitmap(rotatedBitmap);
                        }
                    }
                    break;
                }
                default:
            }

        } catch (Exception error) {
            error.printStackTrace();
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

    private void initView()
    {
        //크롭이미지뷰는 사진 크기를 조절할 수 있는 이미지뷰로 간단한 방법으로 인기가 많음.
        cropImage = (CropImageView) findViewById(R.id.cropImageView_Camera);
        exit = (Button) findViewById(R.id.exit);
        choose = (Button) findViewById(R.id.choose);
        edit = (Button)findViewById(R.id.edit);
        rotateLeft = (ImageButton) findViewById(R.id.rotateLeft);
        rotateRight = (ImageButton) findViewById(R.id.rotateRight);

    }

    private void setListener() {
        exit.setOnClickListener(this);
        choose.setOnClickListener(this);
        edit.setOnClickListener(this);
        rotateLeft.setOnClickListener(this);
        rotateRight.setOnClickListener(this);
    }

    void saveCropImage(){

        cropImage.setDrawingCacheEnabled(true);
        Bitmap bitmap = cropImage.getCroppedImage();
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
        switch (v.getId()){
            case R.id.exit:
                finish();
                break;
            case R.id.rotateLeft:
                //imageView.rotateImage(90);
                cropImage.setImageBitmap(rotateImage(bitmap_temp, rotateDegree - 10));
                break;
            case  R.id.rotateRight:
                cropImage.setImageBitmap(rotateImage(bitmap_temp, rotateDegree + 10));
                break;
            case R.id.edit: //편집버튼
                Intent intent_edit = new Intent(CameraActivity.this, EditActivity.class);
                intent_edit.putExtra(EditActivity.EDIT_CODE, EDIT_CODE);
                startActivity(intent_edit);
                break;
            case R.id.choose: // 인식버튼
                saveCropImage();
                Intent intent_choose = new Intent(CameraActivity.this, SocketActivity.class);
                intent_choose.putExtra(SocketActivity.SOCKET_CODE, SOCKET_CODE);
                startActivity(intent_choose);
                finish();
                break;

        }
    }
}