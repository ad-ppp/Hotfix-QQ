package com.jacky.hitfix.qq;

import android.app.Application;
import android.content.Context;
import android.util.Log;


public class MyApplication extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i("Application", String.format("String classLoader:%s", String.class.getClassLoader()));
        Log.i("Application", String.format("MyApplication classLoader:%s", MyApplication.class.getClassLoader()));

        Hotfix.fix(this, "/sdcard/patch.jar");
    }
}
