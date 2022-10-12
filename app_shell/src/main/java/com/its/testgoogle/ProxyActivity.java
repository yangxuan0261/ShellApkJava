package com.its.testgoogle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sss.demo.testgoogle.R;

public class ProxyActivity extends AppCompatActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_proxy);


        LogUtil.D("--- ProxyActivity onCreate");
//        Log.d("--- ProxyActivity stack", Log.getStackTraceString(new Throwable()));

        new Handler(Looper.getMainLooper()).post(() -> {
            new Handler(Looper.getMainLooper()).post(() -> {
//                try {
//                    Thread.sleep(1000 * 3);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }


//                String srcName = Tools.getAppMeta(this, "SRC_NAME");
//                LogUtil.D("--- ProxyActivity onCreate SRC_NAME: %s", srcName);


                // 加入一些逻辑判断, 启动真正的游戏启动 activity
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), "com.yang.androidaar.MainActivity"));
                startActivity(intent);
            });
        });
    }
}