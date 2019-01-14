package com.s_k.devsec.positiondevice010;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private String ipAddress = "192.168.1.4";
    private String portNumber = "5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btDemo1 = findViewById(R.id.btDemo1);
        btDemo1.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String dest = "30";
                String angle = "40";
                TextView tvDist = findViewById(R.id.tvDest);
                tvDist.setText(dest);
                TextView tvAngle = findViewById(R.id.tvAngle);
                tvAngle.setText(angle);
                Object obj = Arrays.asList(dest, angle); // 適当なデータを用意
                String address = ipAddress; // 受信側端末の実際のアドレスに書き換える
                int port = Integer.parseInt(portNumber);                // 受信側と揃える
                try {
                    UDPObjectTransfer.send(obj, address, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Demo1送信済", Toast.LENGTH_SHORT).show();
            }
        });

        Button btDemo2 = findViewById(R.id.btDemo2);
        btDemo2.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String dest = "10";
                String angle = "-50";
                TextView tvDist = findViewById(R.id.tvDest);
                tvDist.setText(dest);
                TextView tvAngle = findViewById(R.id.tvAngle);
                tvAngle.setText(angle);
                Object obj = Arrays.asList(dest, angle); // 適当なデータを用意
                String address = ipAddress; // 受信側端末の実際のアドレスに書き換える
                int port = Integer.parseInt(portNumber);                // 受信側と揃える
                try {
                    UDPObjectTransfer.send(obj, address, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Demo2送信済", Toast.LENGTH_SHORT).show();
            }
        });

        Button btSend = findViewById(R.id.btSend);
        btSend.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String dist = "0";
                String angle = "0";
                EditText etDist = findViewById(R.id.etDist);
                dist = etDist.getText().toString();
                EditText etAngle = findViewById(R.id.etAngle);
                angle = etAngle.getText().toString();
                Object obj = Arrays.asList(dist, angle); // 適当なデータを用意
                String address = ipAddress; // 受信側端末の実際のアドレスに書き換える
                int port = Integer.parseInt(portNumber);                // 受信側と揃える
                try {
                    UDPObjectTransfer.send(obj, address, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Demo2送信済", Toast.LENGTH_SHORT).show();
            }
        });

        Button btSetPortNumber = findViewById(R.id.btIpSetting);
        btSetPortNumber.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText input = findViewById(R.id.etIpAddress);
                String inputStr = input.getText().toString();
                ipAddress = inputStr;
                Toast.makeText(MainActivity.this, ipAddress + " を送信先IPアドレスに設定しました", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
