package com.qdjk.bluet.myapplication.Community;

import android.util.Log;
import android.widget.Toast;

import com.qdjk.bluet.myapplication.cmd;
import com.qdjk.bluet.myapplication.communityBLT;

import java.nio.ByteBuffer;

public class communityTool {
    private ByteBuffer tempbuffer=ByteBuffer.allocate(1024);
    public static void encoderMessage(byte[] returninfo) {
      byte temp=0;
      int i=0;
     while(i<returninfo.length){
         temp=returninfo[i];
         if (temp==0x68){
             break;
         }
         else {
             i++;
         }
     }int rlen=returninfo[i+1];

       String returndata=printHexString(returninfo);


      String s1=returndata.replace(" ","").toLowerCase();
      int rcmd=Integer.parseInt(s1.substring(10,12),16);
        if(rlen==3){
            communityBLT.datainfo.setText("信息错误！");
        }else{
            String s3=s1.substring(12,12+(rlen-3)*2);
            getResponse(rcmd,s3);
        }

    }

    private static void getResponse(int rcmd, String s3) {
        switch (rcmd&0xff){
            case cmd.returncona1:
                getReturnconal(s3);
                break;
            case cmd.returncona2:
                getReturnconal(s3);
                break;
            case cmd.returnld1:
                getLightdata(s3);
                break;
            case cmd.returnld2:
                getLightdata(s3);
                break;
            case cmd.returnld3:
                getLightdata(s3);
                break;

        }
    }

    private static void getLightdata(String s3) {
        String conAddress=s3.substring(2,8);
        communityBLT.controlh.setText(conAddress);
        int a=Integer.valueOf(s3.substring(8,12),16)/10;
        int b=Integer.valueOf(s3.substring(8,12),16)%10;
        String s=String.valueOf(a)+"."+String.valueOf(b);
        communityBLT.xuvoltage.setText(s);
        String ls=getlightmode(s3.substring(12,14));
        communityBLT.limode.setText(ls);
        String le=String.valueOf(Integer.valueOf(s3.substring(14,16))/10)
                +"."+String.valueOf(Integer.valueOf(s3.substring(14,16))%10);
        communityBLT.lielec.setText(le);
        String todaye=String.valueOf(Integer.valueOf(s3.substring(16,20),16));
        communityBLT.today.setText(todaye);
        String lv=String.valueOf(Integer.valueOf(s3.substring(20,22))/10)
                +"."+String.valueOf(Integer.valueOf(s3.substring(20,22))%10);
        communityBLT.livoltage.setText(lv);
        String xum=getxumode(s3.substring(22,24));
        communityBLT.xumode.setText(xum);
        String gv=String.valueOf(Integer.valueOf(s3.substring(24,26)));
        communityBLT.guvoltage.setText(gv);
        String totale=String.valueOf(Integer.valueOf(s3.substring(26,30)));
        communityBLT.total.setText(totale);
        String gmode=getgmode(s3.substring(30,32));
        communityBLT.gumode.setText(gmode);


    }

    private static String getgmode(String substring) {
        String s=null;
        switch (substring){
            case "00":
                s="电压低";
                break;
            case "01":
                s="电压高";
                break;
            case "02":
                s="电压正常";
                break;
        }
        return s;
    }

    private static String getxumode(String substring) {
        String s=null;
        switch (substring){
            case "00":
                s="过放";
                break;
            case "10":
                s="欠压";
                break;
            case "20":
                s="正常";
                break;
            case "30":
                s="充电限制";
                break;
            case "40":
                s="超压";
                break;
            case "50":
                s="被盗";
                break;
        }
        return s;
    }

    private static String getlightmode(String substring) {
       String s=null;
      // int i=Integer.valueOf(substring);
        switch (substring){
            case "0":
                s="关";
                break;
            case "64":
                s="开";
                break;
            case "82":
                s="超压保护";
                break;
            case "83":
                s="短路保护";
                break;
            case "84":
                s="RS485故障";
                break;

        }
        return s;
    }

    private static void getReturnconal(String s3) {

    }

    public static String printHexString(byte[] data) {
        // TODO Auto-generated method stub
        StringBuffer sbf=new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sbf.append(hex.toUpperCase()+"  ");
        }
        return sbf.toString().trim();

    }

    public static byte[] checkPackage(byte[] temdata) {
        byte[] infodata=null;
        byte[]temdata1;
        int i=0;
        int len=0;


            while (i<temdata.length) {
                    if(temdata[i]==cmd.head) {

                        len = temdata[i + 1];
                        temdata1=new byte[len+cmd.basiclength];
                        if (temdata[i + len + cmd.basiclength] == cmd.tail) {

                            for (int j = 0; j < len + cmd.basiclength; j++) {
                                temdata1[j] = temdata[j + i];
                            }
                            infodata=temdata1;
                            break;
                        }
                    }
                    i++;
            }

        return infodata;
    }

    public static byte[] intToBytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
