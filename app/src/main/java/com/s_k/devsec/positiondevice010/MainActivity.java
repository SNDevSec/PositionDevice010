package com.s_k.devsec.positiondevice010;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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

    String naviIpAddress = "192.168.1.1";
    String naviPortNumber = "5000";

    String dist = "";
    String angle = "";

    View mButtonClicked;

    Handler mHandler;

    TextView tvDist;
    TextView tvAngle;
    EditText etDist;
    EditText etAngle;
    Button btDemo1;
    Button btDemo2;
    Button btSend;
    Button btIpSetting;
    Button btPortSetting;
    EditText etIpAddress;
    EditText etPortNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TAG="MainActivity.onCreate()";

        mHandler = new Handler();

        tvDist = findViewById(R.id.tvDest);
        tvAngle = findViewById(R.id.tvAngle);

        String ip = getWifiIPAddress(MainActivity.this);
        TextView tvDeviceIP = findViewById(R.id.tvDeviceIP);
        tvDeviceIP.setText(ip);

        etDist = findViewById(R.id.etDist);
        etAngle = findViewById(R.id.etAngle);
        etIpAddress = findViewById(R.id.etIpAddress);
        etIpAddress.setText(naviIpAddress);
        etPortNumber = findViewById(R.id.etPortNumber);
        etPortNumber.setText(naviPortNumber);

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
                Log.d(TAG,"dist is:"+dist);
                if(dist.length() != 0){
                    angle = etAngle.getText().toString();
                    Log.d(TAG,"angle is:"+angle);
                    if(angle.length() != 0){
                        mButtonClicked = view;
                        UDPSenderThread mUDPSender = new UDPSenderThread();
                        mUDPSender.start();
                        btSend.setEnabled(false);
                    }else {
                        Toast.makeText(MainActivity.this, "Angle値が入力されていません", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Distance値が入力されていません", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btIpSetting = findViewById(R.id.btIpSetting);
        btIpSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String getString = etIpAddress.getText().toString();
                Log.d(TAG,"naviIpAddress is:"+naviIpAddress);
                if(getString.length() != 0){
                    naviIpAddress = getString;
                    Toast.makeText(MainActivity.this, naviIpAddress + " を送信先IPアドレスに設定しました", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "IPアドレスが入力されていません", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btPortSetting = findViewById(R.id.btPortSetting);
        btPortSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getString = etPortNumber.getText().toString();
                Log.d(TAG,"naviIpAddress is:"+naviPortNumber);
                if(getString.length() != 0){
                    naviPortNumber = getString;
                    Toast.makeText(MainActivity.this, naviPortNumber + " を送信先ポート番号に設定しました", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "ポート番号が入力されていません", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    class UDPSenderThread extends Thread{
        private static final String TAG="UDPReceiverThread";

        private UDPSenderThread(){
            super();
        }

        @Override
        public void start() {
            super.start();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run(){
            Log.d(TAG,"In run(): thread start.");
            final int button_id = mButtonClicked.getId();
            Object obj = Arrays.asList(dist, angle); // 適当なデータを用意
            try {
                UDPObjectTransfer.send(obj, naviIpAddress, Integer.parseInt(naviPortNumber));
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
            Log.d(TAG,"In run(): thread end.");

        }
    }

    private static String getWifiIPAddress(Context context) {
        WifiManager manager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        int ipAddr = info.getIpAddress();
        String ipString = String.format("%d.%d.%d.%d",
                (ipAddr>>0)&0xff, (ipAddr>>8)&0xff, (ipAddr>>16)&0xff, (ipAddr>>24)&0xff);
        return ipString;
    }


}
