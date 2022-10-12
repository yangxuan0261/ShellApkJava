package com.jared.apktest;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Method;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        File odex = this.getDir("payload_odex", MODE_PRIVATE);
        String apkFileName = odex.getAbsolutePath() + "/payload.apk";
        File dexFile = new File(apkFileName);
        Log.i("BaseActivity", String.format("--- dexFile exist: %b", dexFile.exists()));
        if (dexFile.exists()) {
            loadResources(apkFileName);
        }
        super.onCreate(savedInstanceState);
    }


    //以下是加载资源
    protected AssetManager mAssetManager;//资源管理器
    protected Resources mResources;//资源
    protected Resources.Theme mTheme;//主题

    protected void loadResources(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            Log.i("BaseActivity", String.format("--- addAssetPath: %s", dexPath));
            mAssetManager = assetManager;
        } catch (Exception e) {
            Log.i("BaseActivity", "--- loadResources err: " + Log.getStackTraceString(e));
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

//    @Override
//    public Resources.Theme getTheme() {
//        return mTheme == null ? super.getTheme() : mTheme;
//    }
}