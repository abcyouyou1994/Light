package com.qdjk.bluet.myapplication;

import android.app.Application;

class App extends Application {
   public static App app;
    public void onCreate() {
        app=this;
        super.onCreate();
    }
}
