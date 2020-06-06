package com.example.imagedecoder_outsourcing;

import androidx.appcompat.app.AppCompatActivity;

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

public class SocketActivity extends AppCompatActivity {

    InputStream reciver;
    DataOutputStream os;
    Socket s;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

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
            Bitmap img = CameraActivity.cropImage.getCroppedImage();

            img.compress(Bitmap.CompressFormat.PNG, 100, baos);

            //byte로 변환
            byte[] bImage = baos.toByteArray();
            //base64로 변환
            String base64 = Base64.encodeToString(bImage, 0);

            //16자 길이를 맞추기 위한 코드
            String len = "" + base64.length();
            for (int i = 0; i < 16 - Math.log10(base64.length()); i++)
                len += " ";

            Log.d("Socket", "len : " + len);

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
                        int readByteCount = reciver.read(byteArr);
                        String data = new String(byteArr, 0, readByteCount, "UTF-8");
                        byte[] binary = Base64.decode(data, 0);

                        str += new String(binary);
                    } catch (IOException e) {
                        Log.d("asdf", "bbb");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SocketActivity.this, "연결 종료됨", Toast.LENGTH_SHORT).show();
                            }
                        });
                        WriteTextFile("/imageDecoderDownload", "Conversion complete.txt", str);
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
