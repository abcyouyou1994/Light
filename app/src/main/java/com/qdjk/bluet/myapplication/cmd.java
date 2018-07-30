package com.qdjk.bluet.myapplication;

public interface cmd {
    byte settingcon=0x01&0xff;
    byte readingcon=0x02&0xff;
    byte readinglidata=0x03&0xff;
    byte settingfrequency=0x07;
    byte readingfrequency=0x08;
    int returncona1=0x81;
    int returncona2=0x82;
    int returnld1=0x83;
    int returnld2=0x84;
    int returnld3=0x85;


    byte head =0x68;
    byte tail=0x16;
    int basiclength=5;
    int centrel=0xffff;
    int fanh=0x00;
    long luyou=0xffffff;




}
