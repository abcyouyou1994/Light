package com.qdjk.bluet.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.qdjk.bluet.myapplication.Community.BluetoothService;
import com.qdjk.bluet.myapplication.Community.BluetoothService.AcceptThread;
import com.qdjk.bluet.myapplication.Community.communityTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class communityBLT extends AppCompatActivity implements View.OnClickListener {


    private AcceptThread mAccept;
    private BluetoothService mBluetoothService;
    private String mConnectedDeviceName=null;
    public static final int MESSAGE_STATE_CHANGE = 1; // 状态改变
    public static final int MESSAGE_READ = 2;          // 读取数据
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;         // Toast

    private BluetoothDevice mDevice;
    private   BluetoothSocket socket;// 获取到客户端的接口


    public static TextView datainfo;
    public  Button settingc,readingc,settingp,readingp,settingl,readingll,cleartest,readingl;

    public static EditText controlh,frequenc,llvel,xuvoltage,xumode,guvoltage,gumode,limode,livoltage,lielec,today,total;
    public  ScrollView scrollView1;

    private OutputStream outputStream=null;
    public final    UUID Myuuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isConnected=false;
    public InputStream is=null;

//    public communityBLT(BluetoothService.ConnectedThread mconnected) {
//        this.mconnected = mconnected;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_blt);



        datainfo=(TextView)findViewById(R.id.data);

       settingc=(Button)findViewById(R.id.settingc);
       readingc=(Button)findViewById(R.id.readingc);
       settingp=(Button)findViewById(R.id.settingp);
       readingp=(Button)findViewById(R.id.readingp);
       settingl=(Button)findViewById(R.id.settingl);
       readingll=(Button)findViewById(R.id.readingll);
        cleartest=(Button)findViewById(R.id.clears);
        readingl=(Button)findViewById(R.id.readingli);

        controlh=(EditText)findViewById(R.id.controlaH);

        frequenc=(EditText)findViewById(R.id.frequency);
        llvel=(EditText)findViewById(R.id.llevel);
        xuvoltage=(EditText)findViewById(R.id.xuvoltage);
        xumode=(EditText)findViewById(R.id.xumode);
        guvoltage=(EditText)findViewById(R.id.guvoltage);
        gumode=(EditText)findViewById(R.id.gumode);
        lielec=(EditText)findViewById(R.id.lightelec);
        livoltage=(EditText)findViewById(R.id.lighvoltage);
        limode=(EditText)findViewById(R.id.lightmode);
        today=(EditText)findViewById(R.id.today);
        total=(EditText)findViewById(R.id.total);

        scrollView1=(ScrollView)findViewById(R.id.scrollView2);

        settingc.setOnClickListener(this);
        settingl.setOnClickListener(this);
        settingp.setOnClickListener(this);
        readingll.setOnClickListener(this);
        readingl.setOnClickListener(this);
        readingp.setOnClickListener(this);
        readingc.setOnClickListener(this);

        initData();
        initConnection();

    }
    @SuppressLint("WrongConstant")
    private void initData() {
        Intent intent=getIntent();
        BluetoothDevice device=intent.getParcelableExtra("device");
          if(device==null){
            Toast.makeText(this,"没有获取到数据",0).show();
            finish();
        }
        this.mDevice=device;
        Toast.makeText(this,"获取到数据",0).show();
    }


    @SuppressLint("WrongConstant")
    private void initConnection() {
        Toast.makeText(this,"蓝牙开始连接",0).show();

        if(mBluetoothService==null){
            mBluetoothService=new BluetoothService(communityBLT.this,mHandler);
        }if (mBluetoothService!=null){
            if(mDevice!=null){
                mBluetoothService.connect(mDevice);
            }
        }
            }
    long time=SystemClock.uptimeMillis();
    @SuppressLint({"WrongConstant", "ShowToast"})
    @Override
    public void onClick(View v) {
        if(!isConnected){
            Toast.makeText(this,"蓝牙正在连接中……",0).show();
            return;
        }
        String s,s1;
        byte[]con;
        int length,jyh;
        switch (v.getId())
        {
            case R.id.settingc:
                //设置控制器
                s1=String.format("%02x",cmd.fanh)+returnCaddress(controlh.getText().toString());
                con=MainActivity.hexString2Byte(controlh.getText().toString());
                 length=cmd.basiclength+s1.length()/2;
                 jyh=sum(s1);
                s=String.format("%02x",cmd.head)+String.format("%02x",length)+String.format("%02x",cmd.head)+String.format("%x",cmd.centrel)
                        +String.format("%02x",cmd.settingcon)+s1+String.format("%02x",jyh)+String.format("%02x",cmd.tail);
                datainfo.append("send:"+s+"\r\n");
                try {
                        senddata(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.readingli:
                //读取灯信息
                if(controlh.getText().toString().equals("")){
                    Toast.makeText(communityBLT.this,"控制器地址为空，请先输入控制器地址！",0).show();
                    break;
                }
                Toast.makeText(communityBLT.this,"已填入控制器地址",0).show();

                s1=String.format("%02x",cmd.head)+returnCaddress(controlh.getText().toString())+String.format("%x",cmd.luyou);

                length=cmd.basiclength+s1.length()/2;
                jyh=sum(s1);
                s=String.format("%02x",cmd.head)+String.format("%02x",length)+String.format("%02x",cmd.head)+String.format("%x",cmd.centrel)+
                        String.format("%02x",cmd.readinglidata)+s1+String.format("%02x",jyh)+String.format("%02x",cmd.tail);
                datainfo.append("send:"+s+"\r\n");
                try {
                    senddata(s);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.clears:
                //清除
                controlh.append("");

                break;
        }

    }

    private String returnCaddress(String s) {
        String s1=s.substring(4)+s.substring(2,4)+s.substring(0,2);
                return s1;
    }

    private void senddata(final String hexString){

        mAccept= new AcceptThread();
        mAccept.start();
      if(mAccept!=null){
          mAccept.sendData(hexString);
      }else {
          Log.e("Error","线程为空！数据发送错误！");
      }
    }

    protected  void onDestroy() {

        super.onDestroy();
        if (outputStream!=null){
            try {
                outputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }if (socket!=null){
            if (socket.isConnected()){
                try {
                    socket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            socket=null;
            isConnected=false;
        }
    }
    @SuppressLint({"WrongConstant", "ShowToast"})
    private int sum(String s1) {
        int i=0;
        byte temp[];
        if(s1==null){
            Toast.makeText(communityBLT.this,"消息错误！",0).show();
        }else {


            temp = MainActivity.hexString2Byte(s1);
            i = cmd.centrel + cmd.settingcon;
            for (int a = 0; a < temp.length; a++) {
                i = i + a;
            }
        }
            return i%256;
    }
    public synchronized void onResume() {

        super.onResume();
        if (mBluetoothService != null) {
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                mBluetoothService.start();
            }
        }
    }
    // 创建handler，因为我们接收是采用线程来接收的，在线程中无法操作UI，所以需要handler
        private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:

                   try {

                       Bundle data=msg.getData();
                      byte[] temdata=data.getByteArray("dtz");
                        byte[]infodata=communityTool.checkPackage(temdata);

                       String s=communityTool.printHexString(temdata);
                               //data.getString("dtz");
                       if(infodata==null){
                           datainfo.append("receive:"+"数据接收错误！"+s+"\r\n");
                           break;
                       }
                        if(s!=null) {

                            datainfo.append("receive:"+s+"\r\n");
                            byte[] info = MainActivity.hexString2Byte(s);
                            communityTool.encoderMessage(info);
                        }else {
                           Log.e("Error","没有接收到数据！");
                       }

                    } catch (Exception e) {
                        e.printStackTrace();
                   }
                    break;
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                            break;
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    String s1=msg.getData().toString();
                    Toast.makeText(communityBLT.this,s1+"已经成功连接！",Toast.LENGTH_SHORT).show();
                    isConnected=true;
                    break;
                case MESSAGE_TOAST:
                    if (mBluetoothService != null) {
                        mBluetoothService.connect(mDevice);
                    }
                    break;
            }
            return false;
        }
    });










}
