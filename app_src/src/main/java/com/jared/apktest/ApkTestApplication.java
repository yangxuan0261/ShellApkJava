package com.jared.apktest;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

// Android基础之自定义Application - https://www.jianshu.com/p/98324e5d67ae

public class ApkTestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("ApkTestApplication", "--- onCreate");
    }
}