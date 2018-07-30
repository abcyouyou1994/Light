package com.qdjk.bluet.myapplication.Community;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.qdjk.bluet.myapplication.MainActivity;
import com.qdjk.bluet.myapplication.communityBLT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;


public class BluetoothService {
    private static final UUID Myuuid=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    private static BluetoothAdapter mAdapter;
    private Handler mHandler;
    public  static final int SENSEOR_NUM=1;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;

    private int mState;
    public static final int STATE_NONE = 0;         //没有连接
    public static final int STATE_LISTEN = 1;       //等待连接
    public static final int STATE_CONNECTING = 2;  //正在连接
    public static final int STATE_CONNECTED = 3;   //已经连接

    public BluetoothService(Context context,  Handler mHandler) {
        this.context = context;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mHandler = mHandler;
        this.mState = STATE_NONE;
    }

    public synchronized void connect( BluetoothDevice device){
        mConnectThread=new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTED);
    }

    private synchronized void setState(int stateConnected) {
        mState=stateConnected;
        mHandler.obtainMessage(communityBLT.MESSAGE_STATE_CHANGE,stateConnected,-1).sendToTarget();
    }

    public static class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

      public   AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothData", Myuuid);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmServerSocket = tmp;
        }

        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();
        }

        public void sendData(String hexString){
          if(mConnectedThread!=null){
              mConnectedThread.write(hexString);
          }
        }
        private void cancel() {
            try {
                if (mmServerSocket != null) {
                    mmServerSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public synchronized int getState(){
        return mState;
    }
    public synchronized void stop(){
        if (mConnectedThread!=null){
           mConnectedThread.cancel();
           mConnectedThread=null;
        }if(mAcceptThread!=null){
            mAcceptThread.cancel();
            mAcceptThread=null;
        }
        setState(STATE_NONE);
    }
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmsocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(Myuuid);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", e.toString());
            }
            mmsocket = tmp;

        }

        public void run() {
            mAdapter.cancelDiscovery();
            try {
                mmsocket.connect();
                Log.d("TAG", "连接成功");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "连接失败" + e.toString());
                BluetoothService.this.start();
                return;
            }
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            connected(mmsocket, mmDevice);
        }

        public void cancle() {
            try {
                mmsocket.close();
            } catch (IOException e) {

            }
        }

    }
    public class ConnectedThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private byte[] packBuffer = new byte[128];
        private  ByteBuffer buffer=ByteBuffer.allocate(1024);
        private Queue<Byte> queueBuffer = new LinkedList<Byte>();

        private ConnectedThread(BluetoothSocket socket) {
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] tempInputBuffer=new byte[1024] ;
            byte [] bytes;
            int acceptedLen = 0;
            String readMessage;
            int len=0;
            int flag=-1;
            while (true) {
                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    acceptedLen=mmInStream.available();
                    if(acceptedLen>0){
                        len=mmInStream.read(tempInputBuffer);
                        readMessage=communityTool.printHexString(tempInputBuffer);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        //data.putString("dtz",readMessage);
                        data.putByteArray("dtz", tempInputBuffer);
                        msg.what = communityBLT.MESSAGE_READ;
                        msg.setData(data);
                        mHandler.sendMessage(msg);
                    }
//                if(mmInStream.available()<0){
//                    continue;
//                }else {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }acceptedLen=mmInStream.read(tempInputBuffer);
//                int readcount=0;
//                bytes=new byte[acceptedLen];
//                while (readcount<acceptedLen){
//                    readcount+=mmInStream.read(bytes,readcount,acceptedLen-readcount);
//                }
//                    Message msg = new Message();
//                        Bundle data = new Bundle();
//                        //data.putString("dtz",readMessage);
//                        data.putByteArray("dtz", tempInputBuffer);
//                        msg.what = communityBLT.MESSAGE_READ;
//                        msg.setData(data);
//                        mHandler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Error", e.toString());

                }

            }


        }

        private void provessBuffer(byte[] tempInputBuffer, int size) {
            int length=0;
            for (int i=0;i<size;i++){
                if (tempInputBuffer[i]>00){
                    length++;
                }else {
                    break;
                }
            }
            byte []newbuff=new byte[length];
            for (int j=0;j<length;j++){
                newbuff[j]=tempInputBuffer[j];
            }
            Message msg = mHandler.obtainMessage(communityBLT.MESSAGE_READ, newbuff);
            mHandler.sendMessage(msg);
        }

        public void write(String hexString){
            byte[] data_send=MainActivity.hexString2Byte(hexString);

            try {
                mmOutStream.write(data_send);
                mmOutStream.flush();
            }catch (IOException e){
                e.printStackTrace();
                setState(communityBLT.MESSAGE_WRITE);
            }
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
    public synchronized void start() {
        if (mConnectThread != null) {
            mConnectThread.cancle();
            mConnectThread = null;
        }
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }
    private void connectionFailed() {

        setState(STATE_LISTEN);
        Message msg=mHandler.obtainMessage(communityBLT.MESSAGE_TOAST);
        Bundle bundle=new Bundle();
        bundle.putString("toast","未能连接设备");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private void connectionLost() {
        setState(STATE_LISTEN);
        Message msg=mHandler.obtainMessage(communityBLT.MESSAGE_TOAST);
        Bundle bundle=new Bundle();
        bundle.putString("toast","设备丢失");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancle();
            mConnectThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        Message msg = mHandler.obtainMessage(communityBLT.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString("device_name", device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

}
