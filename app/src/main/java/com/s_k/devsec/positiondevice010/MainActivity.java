package com.s_k.devsec.positiondevice010;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener {

    String naviIpAddress = "";
    String naviPortNumber = "5000";

    LocationManager locationManager;
    double latitude = 0; //緯度フィールド
    double longitude = 0; //経度フィールド
    boolean isMeasStart = true;

    String dist = "";
    String angle = "";

    View mButtonClicked;

    Handler mHandler;

    UDPContSenderThread mUDPCSThread;

    TextView tvLatitude;
    TextView tvLongitude;
    TextView tvProvider;
    TextView tvDist;
    TextView tvAngle;
    EditText etDist;
    EditText etAngle;
    Button btDemo1;
    Button btDemo2;
    Button btContStart;
    Button btContStop;
    Button btSend;
    Button btIpSetting;
    Button btPortSetting;
    Button btMaesStart;
    Button btMaesStop;
    EditText etIpAddress;
    EditText etPortNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TAG="MainActivity.onCreate()";

        mHandler = new Handler();

        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvProvider = findViewById(R.id.tvProvider);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1000);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        tvDist = findViewById(R.id.tvDest);
        tvAngle = findViewById(R.id.tvAngle);

        TextView tvDeviceSSID = findViewById(R.id.tvDeviceSSID);
        tvDeviceSSID.setText(getWifiSSID(MainActivity.this));

        naviIpAddress = getWifiIPAddress(MainActivity.this);
        TextView tvDeviceIP = findViewById(R.id.tvDeviceIP);
        tvDeviceIP.setText(naviIpAddress);

        etDist = findViewById(R.id.etDist);
        etAngle = findViewById(R.id.etAngle);
        etIpAddress = findViewById(R.id.etIpAddress);
        etIpAddress.setText(getWifiIPAddress3octet(MainActivity.this));
        etPortNumber = findViewById(R.id.etPortNumber);
        etPortNumber.setText(naviPortNumber);

        btMaesStart = findViewById(R.id.btMeasStart);
        btMaesStart.setEnabled(false);
        btMaesStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:");
                isMeasStart = true;
                Toast.makeText(MainActivity.this, "位置情報取得開始", Toast.LENGTH_SHORT).show();
            }
        });

        btMaesStop = findViewById(R.id.btMeasStop);
        btMaesStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:");
                isMeasStart = false;
                tvLatitude.setText("");
                tvLongitude.setText("");
                btMaesStart.setEnabled(true);
                btMaesStop.setEnabled(false);
                Toast.makeText(MainActivity.this, "位置情報取得停止", Toast.LENGTH_SHORT).show();
            }
        });

        btDemo1 = findViewById(R.id.btDemo1);
        btDemo1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:");
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
                Log.d(TAG, "onClick:");
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

        btContStart = findViewById(R.id.btContStart);
        btContStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:");
                mUDPCSThread = new UDPContSenderThread();
                mUDPCSThread.start();
                btContStart.setEnabled(false);
                btContStop.setEnabled(true);
                Toast.makeText(MainActivity.this, "連続送信開始", Toast.LENGTH_SHORT).show();
            }
        });

        btContStop = findViewById(R.id.btContStop);
        btContStop.setEnabled(false);
        btContStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:");
                mUDPCSThread.onStop();
                btContStop.setEnabled(false);
                Toast.makeText(MainActivity.this, "連続送信停止", Toast.LENGTH_SHORT).show();
            }
        });

        btSend = findViewById(R.id.btSend);
        btSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:");
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
                Log.d(TAG, "onClick:");
                String getString = etIpAddress.getText().toString();
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
                Log.d(TAG, "onClick:");
                String getString = etPortNumber.getText().toString();
                if(getString.length() != 0){
                    naviPortNumber = getString;
                    Toast.makeText(MainActivity.this, naviPortNumber + " を送信先ポート番号に設定しました", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "ポート番号が入力されていません", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity", "onPause()");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.d("MainActivity", "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "onDestroy()");
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("MainActivity", "onLocationChanged():" + location.getProvider() + " " + String.valueOf(location.getAccuracy()) + " " + location.getTime());
        if (isMeasStart) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            tvLatitude.setText(Double.toString(latitude));
            tvLongitude.setText(Double.toString(longitude));
            tvProvider.setText(location.getProvider());
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
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
            Map<String, String> map = new HashMap<>(); // 適当なデータを用意
            map.put("dist", dist);
            map.put("angle", angle);
            try {
                UDPObjectTransfer.send(map, naviIpAddress, Integer.parseInt(naviPortNumber));
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

    class UDPContSenderThread extends Thread{
        private static final String TAG="UDPContSenderThread";
        boolean mIsArive= false;

        private UDPContSenderThread(){
            super();
        }

        @Override
        public void start() {
            mIsArive= true;
            Log.d(TAG,"mIsArive status:"+ mIsArive);
            super.start();
        }

        public void onStop() {
            Log.d(TAG,"onStop()");
            mIsArive= false;
            Log.d(TAG,"mIsArive status:"+ mIsArive);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run(){
            Log.d(TAG,"In run(): thread start.");
            int cnt = -60;
            boolean reverse = false;
            try {
                while(mIsArive){
                    dist = String.valueOf(cnt);
                    angle= String.valueOf(cnt);
                    Map<String, String> map = new HashMap<>(); // 適当なデータを用意
                    map.put("dist", dist);
                    map.put("angle", angle);
                    UDPObjectTransfer.send(map, naviIpAddress, Integer.parseInt(naviPortNumber));
                    if(!reverse) {
                        if (cnt != 60) {
                            cnt += 10;
                        } else {
                            reverse = true;
                            cnt -= 10;
                        }
                    }else {
                        if (cnt != -60) {
                            cnt -= 10;
                        } else {
                            reverse = false;
                            cnt += 10;
                        }
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvDist.setText(dist);
                            tvAngle.setText(angle);
                        }
                    });
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    btContStart.setEnabled(true);
                    Toast.makeText(MainActivity.this, "連続送信スレッド停止", Toast.LENGTH_SHORT).show();
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

    private static String getWifiIPAddress3octet(Context context) {
        WifiManager manager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        int ipAddr = info.getIpAddress();
        String ipString = String.format("%d.%d.%d.",
                (ipAddr>>0)&0xff, (ipAddr>>8)&0xff, (ipAddr>>16)&0xff);
        return ipString;
    }

    private static String getWifiSSID(Context context) {
        WifiManager manager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String  ssid = info.getSSID();
        return ssid;
    }
}
