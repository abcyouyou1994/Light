package com.qdjk.bluet.myapplication.Community;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ClientThread extends Thread {
    public final UUID Myuuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice bdevice;

    public ClientThread(BluetoothDevice bdevice) {
        this.bdevice = bdevice;
    }
    public void run(){
        BluetoothSocket socket=null;
        try {
            socket=bdevice.createRfcommSocketToServiceRecord(Myuuid);
            Log.d("TAG","连接服务端");
            socket.connect();
            Log.d("TAG","连接成功");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
