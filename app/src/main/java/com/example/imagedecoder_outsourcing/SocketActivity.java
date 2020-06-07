package com.example.imagedecoder_outsourcing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.imagedecoder_outsourcing.Const.Const;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 편집액티비티, 소캣액티비티 코드들은 넘어갈때 자기가 카메라에서 왔는지 갤러리에서 왔는지 intent로 값을 가지고 갑니다.
 *  위 액티비티들은 하나이니 카메라에서 넘어갈때, 갤러리에서 넘어갈 때를 잘 구분하셔서 코드를 작성하세요.
 */

public class SocketActivity extends AppCompatActivity {

    final static String SOCKET_CODE = "socket";

    InputStream reciver;
    DataOutputStream os;
    Socket s;
    Handler handler;

    Intent intent;
    String pre_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        handler = new Handler();

        //카메라, 갤러리 어디서 왔는지
        intent = getIntent();
        pre_activity = intent.getExtras().getString(SOCKET_CODE);

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //소켓 객체 생성
                    s = new Socket(Const.ip, 9999);
                    os = new DataOutputStream(s.getOutputStream());
                    reciver = s.getInputStream();

                    //수신을 위한 쓰레드 생성
                    new Reciver().start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        imageEncoding();

    }

    private void imageEncoding() {
        try {
            //이미지를 bitmap으로 불러옴
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //이미지가 어디서 왔는지에 따라 각 액티비티에서 이미지 비트맵 가져오기
            //이때 크롭되어 날라옴.
            Bitmap img;
            if(pre_activity.equals(CameraActivity.SOCKET_CODE) ) // 카메라에서 오면
            {
                img = CameraActivity.cropImage.getCroppedImage();
                Log.d("test","camera Activity come in");
            }
            else //카메라가 아니라면
            {
                Log.d("test","gallery Activity come in");
                img = GalleryActivity.imageView.getCroppedImage();
            }

            img.compress(Bitmap.CompressFormat.PNG, 100, baos);

            //byte로 변환
            byte[] bImage = baos.toByteArray();
            //base64로 변환
            String base64 = Base64.encodeToString(bImage, 0);

            //16자 길이를 맞추기 위한 코드
            String len = "" + base64.length();
            for (int i = 0; i < 16 - Math.log10(base64.length()); i++)
                len += " ";

            SendMessage(len);
            SendMessage(base64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SendMessage(final String msg) {//메시지 전송을 위한 함수
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    os.write(msg.getBytes(), 0, msg.length());
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private class Reciver extends Thread {
        @Override
        public void run() {
            super.run();
            String str = "";
            while (true) {
                if (reciver != null) {
                    try {
                        //데이터 수신부
                        byte[] byteArr = new byte[10000];
                        Log.d("test", "1");
                        int readByteCount = reciver.read(byteArr);
                        Log.d("test", "12" + reciver.toString());
                        String data = new String(byteArr, 0, readByteCount, "UTF-8");
                        Log.d("test", "13");
                        byte[] binary = Base64.decode(data, 0);

                        Log.d("test", "14");
                        str += new String(binary);
                        Log.d("test", "22222");
                    } catch (IOException e) {
                        Log.d("test", "bbb5");
                        Log.d("test", " error : " + e.getMessage());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("test", "16");
                                Toast.makeText(SocketActivity.this, "연결 종료됨", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("test", "17");

                        //시간으로 파일명 생성
                        long time = System.currentTimeMillis();  //시간 받기
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
                        //포멧 변환  형식 만들기
                        Date dd = new Date(time);  //받은 시간을 Date 형식으로 바꾸기
                        String strTime = sdf.format(dd); //Data 정보를 포멧 변환하기

                        String filename = "Result "+strTime+".txt";

                        WriteTextFile("/imageDecoderDownload", filename, str);
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }

    public void WriteTextFile(String foldername, String filename, String contents) {
        foldername = Environment.getExternalStorageDirectory().getAbsolutePath() + foldername;
        try {
            File dir = new File(foldername);
            //디렉토리 폴더가 없으면 생성함
            if (!dir.exists()) {
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername + "/" + filename, false);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();
            writer.close();
            fos.close();

            /**
             *
             */
            finish(); // 추가한부분
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SocketActivity.this, "저장됨", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
