package com.qdjk.bluet.myapplication.Community;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.qdjk.bluet.myapplication.cmd;
import com.qdjk.bluet.myapplication.communityBLT;

import java.io.IOException;
import java.io.InputStream;

public class ServerThread extends Thread {
    private   BluetoothSocket sbsocket;

    public ServerThread(BluetoothSocket socket) {
        this.sbsocket=socket;
    }
    public void run(){

        int PacketLength=1024;
        int count,len=0,flag=-1,newData=0;
        byte[] bytes=new byte[PacketLength];
        byte[] temp=bytes;
        InputStream sis;
        try{
            sis=sbsocket.getInputStream();

            while (true){
                count=sis.read(bytes);
                 byte[] contents=new byte[count];


                    for(int i = 0; i < PacketLength; i++) {
                        if((newData=sis.read())!=-1){
                            bytes[i]=(byte)newData;
                            if (bytes[i] == cmd.head&&len==0) {
                                flag = i;
                            }
                            if(flag!=-1){
                                len=bytes[i];
                                flag=-1;
                                break;
                            }

                        }

                    }
                    for (int j=0;j<len+3;j++){
                        if ((newData=sis.read())!=-1){
                            contents[j]=(byte)newData;
                        }
                    }


                String s= communityTool.printHexString(contents);
                communityBLT.datainfo.append("receive:"+s+"\r\n");
                communityTool.encoderMessage(contents);

            }
        }catch (IOException e){
            Log.e("TAG",e.toString());
            e.printStackTrace();
        }
    }
}
