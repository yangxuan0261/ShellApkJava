package com.its.testgoogle;


import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;

// TODO: 将 脱壳 代码隐藏到 so 中
public class ProxyApplication extends Application {
    private String mApkFileName;
    private String mOdexPath;
    private String mLibPath;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LogUtil.D("--- ProxyApplication.attachBaseContext");


        try {
            //创建两个文件夹payload_odex，payload_lib 私有的，可写的文件目录
            File odex = this.getDir("payload_odex", MODE_PRIVATE);
            File libs = this.getDir("payload_lib", MODE_PRIVATE);
            mOdexPath = odex.getAbsolutePath();
            mLibPath = libs.getAbsolutePath();
            mApkFileName = odex.getAbsolutePath() + "/payload.apk";

            InstrumentationProxy.ApkFileName = mApkFileName;

            LogUtil.D("--- odexPath: %s ", mOdexPath);
            LogUtil.D("--- libPath: %s ", mLibPath);
            LogUtil.D("--- apkFileName: %s ", mApkFileName);

            File dexFile = new File(mApkFileName);
            LogUtil.D("--- apkFileName: %s ", mApkFileName);
            LogUtil.D("--- apkFile size: %d ", dexFile.length());
            if (!dexFile.exists()) {
                dexFile.createNewFile();  //在payload_odex文件夹内，创建payload.apk
                // 读取程序classes.dex文件
//                byte[] dexdata = this.readDexFileFromApk();
//                LogUtil.D("--- dexdata.length: %d ", dexdata.length);
                byte[] apkBts = this.getAssetsApk();
                // 分离出解壳后的apk文件已用于动态加载
                this.splitPayLoadFromDex(apkBts);
            }
            // 配置动态加载环境
            Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread", new Class[]{}, new Object[]{}); //获取主线程对象 http://blog.csdn.net/myarrow/article/details/14223493
            String packageName = this.getPackageName();//当前apk的包名

            //下面两句不是太理解
            ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mPackages");
            WeakReference wr = (WeakReference) mPackages.get(packageName);
            //创建被加壳apk的DexClassLoader对象  加载apk内的类和本地代码（c/c++代码）

            ClassLoader parentLoader = (ClassLoader) RefInvoke.getFieldOjbect("android.app.LoadedApk", wr.get(), "mClassLoader");
            DexClassLoader dLoader = new DexClassLoader(mApkFileName, mOdexPath, mLibPath, parentLoader);
            //base.getClassLoader(); 是不是就等同于 (ClassLoader) RefInvoke.getFieldOjbect()? 有空验证下//?
            //把当前进程的DexClassLoader 设置成了被加壳apk的DexClassLoader  ----有点c++中进程环境的意思~~


            RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader", wr.get(), dLoader);

            LogUtil.D("--- set new DexClassLoader: " + dLoader);

            try {
                String activityClassName = getAppMeta("ACTIVITY_CLASS_NAME");
                Object actObj = dLoader.loadClass(activityClassName); // 源 apk 里的 activity
                LogUtil.D("--- activity ok: " + actObj);

//                HookHelper.hookInstrumentation(base, ""); // 拦击 启动 activity 并 替换

            } catch (Exception e) {
                LogUtil.D("--- activity err:" + Log.getStackTraceString(e));
            }


        } catch (Exception e) {
            LogUtil.D("--- deshell  err:" + Log.getStackTraceString(e));
            e.printStackTrace();
        }
    }


    // 从 assets 目录读取 源 apk
    private byte[] getAssetsApk() {
        String srcName = getAppMeta("SRC_NAME");
        return FileTool.getAssetsFileBts(this, srcName); // 读取 apk 并解密
//        return decrypt(FileTool.getAssetsFileBts(this, srcName)); // 读取 apk 并解密
    }

    // 获取  manifest.xml 的 application meta 值
    private String getAppMeta(String key) {
        String value = null;
        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            if (bundle != null && bundle.containsKey(key)) {
                value = bundle.getString(key);//className 是配置在xml文件中的。
                LogUtil.D("--- getAppMeta success, key: %s, value: %s: ", key, value);
            } else {
                LogUtil.D("--- getAppMeta fail, key: %s", key);
            }
        } catch (NameNotFoundException e) {
            LogUtil.D("--- get ApplicationInfo info err: " + Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.D("--- ProxyApplication onCreate");

//        Intent intent = new Intent(this, ProxyActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

//        if (true) // TODO: aaa 测试
//            return;


        // 如果源应用配置有 Appliction 对象，则替换为源应用 Applicaiton，以便不影响源程序逻辑。
        String appClassName = getAppMeta("APPLICATION_CLASS_NAME");
        if (appClassName == null) {
            return;
        }

        //有值的话调用该Applicaiton
        Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread", new Class[]{}, new Object[]{});
        Object mBoundApplication = RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mBoundApplication");
        Object loadedApkInfo = RefInvoke.getFieldOjbect("android.app.ActivityThread$AppBindData", mBoundApplication, "info");

        //把当前进程的mApplication 设置成了null
        RefInvoke.setFieldOjbect("android.app.LoadedApk", "mApplication", loadedApkInfo, null);
        Object oldApplication = RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mInitialApplication");
        //http://www.codeceo.com/article/android-context.html

        ArrayList<Application> mAllApplications = (ArrayList<Application>) RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mAllApplications");
        mAllApplications.remove(oldApplication);//删除oldApplication
        ApplicationInfo appinfo_In_LoadedApk = (ApplicationInfo) RefInvoke.getFieldOjbect("android.app.LoadedApk", loadedApkInfo, "mApplicationInfo");
        ApplicationInfo appinfo_In_AppBindData = (ApplicationInfo) RefInvoke.getFieldOjbect("android.app.ActivityThread$AppBindData", mBoundApplication, "appInfo");
        appinfo_In_LoadedApk.className = appClassName;
        appinfo_In_AppBindData.className = appClassName;
        Application app = (Application) RefInvoke.invokeMethod("android.app.LoadedApk", "makeApplication", loadedApkInfo, new Class[]{boolean.class, Instrumentation.class}, new Object[]{false, null});//执行 makeApplication（false,null）
        RefInvoke.setFieldOjbect("android.app.ActivityThread", "mInitialApplication", currentActivityThread, app);

        LogUtil.D("--- set ProviderMap");
        ArrayMap mProviderMap = (ArrayMap) RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mProviderMap");
        Iterator it = mProviderMap.values().iterator();
        while (it.hasNext()) {
            Object providerClientRecord = it.next();
            Object localProvider = RefInvoke.getFieldOjbect("android.app.ActivityThread$ProviderClientRecord", providerClientRecord, "mLocalProvider");
            LogUtil.D("--- localProvider null: %b, app null: %b", localProvider == null, app == null);
            RefInvoke.setFieldOjbect("android.content.ContentProvider", "mContext", localProvider, app);
        }

        LogUtil.D("--- makeApplication: " + app);

        app.onCreate();
        loadResources(mApkFileName);
    }

    /**
     * 释放被加壳的apk文件，so文件
     *
     * @throws IOException
     */
    private void splitPayLoadFromDex(byte[] apkdata) throws IOException {
//        int ablen = apkdata.length;
//        //取被加壳apk的长度   这里的长度取值，对应加壳时长度的赋值都可以做些简化
//        byte[] dexlen = new byte[4];
//        System.arraycopy(apkdata, ablen - 4, dexlen, 0, 4);
//        ByteArrayInputStream bais = new ByteArrayInputStream(dexlen);
//        DataInputStream in = new DataInputStream(bais);
//        int readInt = in.readInt();
//        LogUtil.D("--- readInt, val: %d, hex: %s ", readInt, Integer.toHexString(readInt));
//
//        System.out.println(Integer.toHexString(readInt));
//        byte[] newdex = new byte[readInt];
//        //把被加壳apk内容拷贝到newdex中
//        System.arraycopy(apkdata, ablen - 4 - readInt, newdex, 0, readInt);
//        //这里应该加上对于apk的解密操作，若加壳是加密处理的话
//        //?
//
//        //对源程序Apk进行解密
//        newdex = decrypt02(newdex);

        //写入apk文件
        File file = new File(mApkFileName);
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(file);
            localFileOutputStream.write(apkdata);
            localFileOutputStream.close();
        } catch (IOException localIOException) {
            throw new RuntimeException(localIOException);
        }

        //分析被加壳的 apk 文件, 拷贝 so 库并放到指定位置
        ZipInputStream localZipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
        while (true) {
            ZipEntry localZipEntry = localZipInputStream.getNextEntry();
            if (localZipEntry == null) {
                localZipInputStream.close();
                break;
            }

            //取出被加 源 apk 用到的 so 文件，放到 mLibPath 中（data/data/包名/app_payload_lib)
            String name = localZipEntry.getName();
            if (name.startsWith("lib/") && name.endsWith(".so")) {
                File storeFile = new File(mLibPath + "/" + name.substring(name.lastIndexOf('/')));
                storeFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(storeFile);
                byte[] arrayOfByte = new byte[1024];
                while (true) {
                    int i = localZipInputStream.read(arrayOfByte);
                    if (i == -1)
                        break;
                    fos.write(arrayOfByte, 0, i);
                }
                fos.flush();
                fos.close();
            }
            localZipInputStream.closeEntry();
        }
        localZipInputStream.close();
    }

    // 从apk包里面获取dex文件内容（byte）
    private byte[] readDexFileFromApk() throws IOException {
        LogUtil.D("--- this.getApplicationInfo().sourceDir: %s", this.getApplicationInfo().sourceDir);
        ByteArrayOutputStream dexByteArrayOutputStream = new ByteArrayOutputStream();
        ZipInputStream localZipInputStream = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(
                        this.getApplicationInfo().sourceDir)));
        while (true) {
            ZipEntry localZipEntry = localZipInputStream.getNextEntry();
            if (localZipEntry == null) {
                localZipInputStream.close();
                break;
            }
            ;
//            LogUtil.D("--- localZipEntry.getName(): %s", localZipEntry.getName());
            if (localZipEntry.getName().equals("classes.dex")) {
                byte[] arrayOfByte = new byte[1024];
                while (true) {
                    int i = localZipInputStream.read(arrayOfByte);
                    if (i == -1)
                        break;
                    dexByteArrayOutputStream.write(arrayOfByte, 0, i);
                }
            }
            localZipInputStream.closeEntry();
        }
        localZipInputStream.close();
        return dexByteArrayOutputStream.toByteArray();
    }

    // 直接返回数据，读者可以添加自己解密方法
    private byte[] decrypt02(byte[] srcdata) {
        for (int i = 0; i < srcdata.length; i++) {
            srcdata[i] = (byte) (0xFF ^ srcdata[i]);
        }
        return srcdata;
    }


    // -----------------------------------------------------
    //以下是 context 的加载资源重写
    protected AssetManager mAssetManager;//资源管理器
    protected Resources mResources;//资源
    protected Theme mTheme;//主题

    protected void loadResources(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            LogUtil.D("--- addAssetPath: %s", dexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            LogUtil.D("--- loadResources err: " + Log.getStackTraceString(e));
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        superRes.getDisplayMetrics();
        superRes.getConfiguration();
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

    @Override
    public Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }

}