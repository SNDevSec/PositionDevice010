package com.s_k.devsec.positiondevice010;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    String ipAddress = "192.168.1.2";
    String portNumber = "5000";

    String dist;
    String angle;

    View mButtonClicked;

    Handler mHandler;

    TextView tvDist;
    TextView tvAngle;
    EditText etDist;
    EditText etAngle;
    Button btDemo1;
    Button btDemo2;
    Button btSend;
    EditText etInput;
    Button btSetPortNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TAG="MainActivity.onCreate()";

        mHandler = new Handler();

        tvDist = findViewById(R.id.tvDest);
        tvAngle = findViewById(R.id.tvAngle);
        etDist = findViewById(R.id.etDist);
        etAngle = findViewById(R.id.etAngle);
        etInput = findViewById(R.id.etIpAddress);
        etInput.setText(ipAddress);

        btDemo1 = findViewById(R.id.btDemo1);
        btDemo1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:" + view.getId());
                dist = "30";
                angle = "40";
                tvDist.setText(dist);
                tvAngle.setText(angle);
                mButtonClicked = view;
                UDPSenderThread mUDPSender = new UDPSenderThread();
                mUDPSender.start();
                btDemo1.setEnabled(false);
            }
        });

        btDemo2 = findViewById(R.id.btDemo2);
        btDemo2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:" + view.getId());
                dist = "10";
                angle = "-50";
                tvDist.setText(dist);
                tvAngle.setText(angle);
                mButtonClicked = view;
                UDPSenderThread mUDPSender = new UDPSenderThread();
                mUDPSender.start();
                btDemo2.setEnabled(false);
            }
        });

        btSend = findViewById(R.id.btSend);
        btSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:" + view.getId());
                dist = etDist.getText().toString();
                angle = etAngle.getText().toString();
                mButtonClicked = view;
                UDPSenderThread mUDPSender = new UDPSenderThread();
                mUDPSender.start();
                btSend.setEnabled(false);
            }
        });

        btSetPortNumber = findViewById(R.id.btIpSetting);
        btSetPortNumber.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ipAddress = etInput.getText().toString();
                Toast.makeText(MainActivity.this, ipAddress + " を送信先IPアドレスに設定しました", Toast.LENGTH_SHORT).show();
            }
        });

    }

    class UDPSenderThread extends Thread{
        private static final String TAG="UDPReceiverThread";

        public UDPSenderThread(){
            super();
        }

        @Override
        public void start() {
            Log.d(TAG,"start()");
            super.start();
        }


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run(){
            final int button_id = mButtonClicked.getId();
            Object obj = Arrays.asList(dist, angle); // 適当なデータを用意
            try {
                UDPObjectTransfer.send(obj, ipAddress, Integer.parseInt(portNumber));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "送信済", Toast.LENGTH_SHORT).show();
                    switch (button_id) {
                        case R.id.btDemo1:
                            btDemo1.setEnabled(true);
                            break;
                        case R.id.btDemo2:
                            btDemo2.setEnabled(true);
                            break;
                        case R.id.btSend:
                            btSend.setEnabled(true);
                            break;
                    }

                }
            });
        }
    }
}
