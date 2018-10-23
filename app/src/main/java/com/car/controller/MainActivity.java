package com.car.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import java.io.*;

public class MainActivity extends AppCompatActivity {
    private Button topbt,leftbt,rightbt,endbt,connectbt;
    private Switch sw;
    private EditText ip,distance,degrees;
    private TextView tv,fps,timev;
    private ImageView IV;
    private String socket_host;
    private int socket_timeout = 5000;
    private NetTool netTool = new NetTool();
    private VideoTool videoTool = new VideoTool();
    private SendStrTool sendStrTool = new SendStrTool();
    private One_s_Timer one_s_timer = new One_s_Timer();
    private AlertDialog pd;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv.setText(String.format("服务器:%s", SendStrTool.server_ret));
                    break;
                case -1:
                    disconn();
                    break;
                case 1:
                    IV.setImageBitmap(videoTool.ivmap);
                    break;
                case 1000:
                    if(videoTool.int_fps!=0){
                        fps.setText(String.format("%sFPS", Integer.toString(videoTool.int_fps)));//fps
                        timev.setText(one_s_timer.time);
                    }
                    videoTool.int_fps=0;
                    break;
                case 5:
                    server_connect_succeed();
                    break;
                default:
                    break;
            }
        }
    };

    public void doClick(View btv){
        switch (btv.getId()){
            case R.id.top:
                    top();
                break;
            case R.id.left:
                    left();
                break;
            case R.id.right:
                    right();
                break;
            case R.id.end:
                    end();
                break;
            case R.id.connect:
                if(sw.isChecked()){
                    connect_server();
                }else {
                    if(ip.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this,"请输入ip地址",Toast.LENGTH_LONG).show();
                    }else {
                       socket_host =  ip.getText().toString();
                       get_video();
                    }
                }
                break;
            case R.id.degrees_et:
                degrees.setText("");
                break;
            case R.id.distance_et:
                distance.setText("");
                break;
            case R.id.ipet:
                ip.setText("");
                break;
        }
    }
    private void top(){
        if(!distance.getText().toString().isEmpty()){
            new Thread(){
                public void run(){
                    SendString("W"+distance.getText().toString());
                }
            }.start();
        }else {
            Toast.makeText(MainActivity.this,"距离不能为空",Toast.LENGTH_SHORT).show();
        }
    }
    private void end(){
        if(!distance.getText().toString().isEmpty()){
            new Thread(){
                public void run(){
                    SendString("S"+distance.getText().toString());
                }
            }.start();
        }else {
            Toast.makeText(MainActivity.this,"距离不能为空",Toast.LENGTH_SHORT).show();
        }
    }
    private void left(){
        if(!degrees.getText().toString().isEmpty()){
            new Thread(){
                public void run(){
                    SendString("A"+degrees.getText().toString());
                }
            }.start();
        }else {
            Toast.makeText(MainActivity.this,"角度不能为空",Toast.LENGTH_SHORT).show();
        }
    }
    private void right(){
        if(!degrees.getText().toString().isEmpty()){
            new Thread(){
                public void run(){
                    SendString("D"+degrees.getText().toString());
                }
            }.start();
        }else {
            Toast.makeText(MainActivity.this,"角度不能为空",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        index();
        IV.setImageResource(R.drawable.ic_launcher_background);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    ip.setEnabled(false);
                }else {
                    ip.setEnabled(true);
                }
            }
        });
    }
    private void get_video(){
        new Thread(){//视频流线程
            public void run(){
                SendString("2");
                new Thread(){public void run(){video_socket();}}.start();
                new Thread(){public void run(){video_socket();}}.start();
            }
        }.start();
    }
    private void video_socket(){
        while (true){
            try {
                videoTool.RecImg(socket_host,socket_timeout);
                Message.obtain(handler,1).sendToTarget();
            }catch (IOException e){
                e.printStackTrace();
                Message.obtain(handler,-1).sendToTarget();
                break;
            }
        }
    }
    //连接服务器
    private void connect_server(){
        pd.show();
        new Thread() {
            public void run() {
                socket_host = netTool.scan();
                if(TextUtils.equals(netTool.server_ret,"已连接")){
                    Message.obtain(handler,5).sendToTarget();
                    get_video();
                }else {
                    Message.obtain(handler,-1).sendToTarget();
                }
            }
        }.start();
    }
    private void SendString(String input){
        try {
            sendStrTool.SendString(input,socket_host,socket_timeout);
            Message.obtain(handler,5).sendToTarget();
            Message.obtain(handler,0).sendToTarget();
        } catch (IOException e){
            e.printStackTrace();
            Message.obtain(handler,-1).sendToTarget();
        }catch (Exception e){
            System.out.print("不知道什么鬼错误");
            e.printStackTrace();
        }
    }
    class Time_Thread extends Thread {
        public void run(){
            while (true){
                try {
                    one_s_timer.Timer();
                    Message.obtain(handler,1000).sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
    private void disconn(){
        topbt.setEnabled(false);
        leftbt.setEnabled(false);
        rightbt.setEnabled(false);
        endbt.setEnabled(false);
        connectbt.setEnabled(true);
        sw.setEnabled(true);
        tv.setText("");
        ip.setText(R.string.failed_conn);
        fps.setText("");
        timev.setText("");
        IV.setImageBitmap(null);
        pd.dismiss();
        videoTool.int_fps=0;
        tv.setBackgroundColor(Color.WHITE);
        IV.setImageResource(R.drawable.ic_launcher_background);
    }
    private void index(){
        topbt = findViewById(R.id.top);
        leftbt = findViewById(R.id.left);
        rightbt = findViewById(R.id.right);
        endbt = findViewById(R.id.end);
        connectbt = findViewById(R.id.connect);
        tv = findViewById(R.id.text_tv);
        timev = findViewById(R.id.timeview);
        IV = findViewById(R.id.vd);
        ip = findViewById(R.id.ipet);
        fps = findViewById(R.id.fps_tv);
        distance = findViewById(R.id.distance_et);
        degrees = findViewById(R.id.degrees_et);
        sw= findViewById(R.id.connect_mod);
        if(sw.isChecked()){
            ip.setEnabled(false);
        }else {
            ip.setEnabled(true);
        }
        topbt.setEnabled(false);
        leftbt.setEnabled(false);
        rightbt.setEnabled(false);
        endbt.setEnabled(false);
        new Thread(new Time_Thread()).start();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        pd = builder.create();
        pd.setView(View.inflate(this, R.layout.pg, null));
        pd.setCancelable(false);
    }
    private void server_connect_succeed(){
        tv.setText(String.format("服务器回应:%s", netTool.server_ret));
        topbt.setEnabled(true);
        leftbt.setEnabled(true);
        rightbt.setEnabled(true);
        endbt.setEnabled(true);
        ip.setText(socket_host);
        sw.setEnabled(false);
        connectbt.setEnabled(false);
        netTool.server_ret="";
        tv.setBackgroundColor(Color.YELLOW);
        pd.dismiss();
    }
}
