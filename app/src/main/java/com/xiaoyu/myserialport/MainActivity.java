package com.xiaoyu.myserialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android_serialport_api.SerialPort;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    protected SerialPort mSerialPort;
    protected InputStream mInputStream;
    protected OutputStream mOutputStream;

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                Log.i(TAG, "接收线程已经开启");
                try {
                    byte[] buffer = new byte[64];

                    if (mInputStream == null) {
                        return;
                    }

                    size = mInputStream.read(buffer);

                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mSerialPort = new SerialPort(new File("/dev/ttyS2"), 9600, 0);
            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                mOutputStream.write("abcdefghijklmn".getBytes());
                Log.i(TAG, "发送成功" + i);
            }
            ReadThread mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "发送失败");
            e.printStackTrace();
        }

        Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOutputStream.write("abcdefghijklmn".getBytes());
                    Log.i(TAG, "发送成功");
                } catch (IOException e) {
                    Log.i(TAG, "发送失败");
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String recInfo = new String(buffer, 0, size);
                Log.i(TAG, "接收到串口信息======" + Arrays.toString(recInfo.getBytes()));
            }
        });
    }
}