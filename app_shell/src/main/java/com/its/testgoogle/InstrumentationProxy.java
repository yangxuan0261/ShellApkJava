package com.its.testgoogle;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class InstrumentationProxy extends Instrumentation {
    private Instrumentation mInstrumentation;
    private PackageManager mPackageManager;
    private String className;

    public InstrumentationProxy(Instrumentation mInstrumentation, PackageManager mPackageManager, String className) {
        this.mInstrumentation = mInstrumentation;
        this.mPackageManager = mPackageManager;
        this.className = className;
    }

    // 执行启动 的 activity
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        Log.i("InstrumentationProxy", "--- InstrumentationProxy.execStartActivity");

//        List<ResolveInfo> infos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
//        if (infos == null || infos.size() == 0) {
//            Log.i("InstrumentationProxy", String.format("--- InstrumentationProxy.execStartActivity, className1: %s, className1: %s", intent.getComponent().getClassName(), className));
//            intent.putExtra(HookHelper.PLUGIN_INTENT, intent.getComponent().getClassName());
//            intent.setClassName(who, className);
//        }

        Log.i("InstrumentationProxy", "--- InstrumentationProxy.execStartActivity, 注入的 Hook 前执行的业务逻辑");

        // 1. 反射执行 Instrumentation orginalInstrumentation 成员的 execStartActivity 方法
        Method execStartActivity_Method = null;
        try {
            execStartActivity_Method = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // 2. 设置方法可访问性
        execStartActivity_Method.setAccessible(true);

        // 3. 执行 Instrumentation orginalInstrumentation 的 execStartActivity 方法
        //    使用 Object 类型对象接收反射方法执行结果
        ActivityResult activityResult = null;
        try {
            activityResult = (ActivityResult) execStartActivity_Method.invoke(mInstrumentation, who, contextThread, token, target, intent, requestCode, options);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        Log.i("InstrumentationProxy", "--- InstrumentationProxy.execStartActivity, 注入的 Hook 后执行的业务逻辑");

        return activityResult;
    }


    private boolean mTestFlag = true;
    public static String ApkFileName = "";

    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws
            InstantiationException, IllegalAccessException, ClassNotFoundException {
        Log.i("InstrumentationProxy", String.format("--- InstrumentationProxy.newActivity, className: %s", className));

//        String intentName = intent.getStringExtra(HookHelper.PLUGIN_INTENT);
//        if (!TextUtils.isEmpty(intentName)) {
//            Log.i("InstrumentationProxy", String.format("--- InstrumentationProxy.newActivity, intentName: %s", intentName));
//            return super.newActivity(cl, intentName, intent);
//        }

        if (mTestFlag) {
//            className = "com.its.testgoogle.ProxyActivity";
        }

        Activity actIns = super.newActivity(cl, className, intent);


//        if (!mTestFlag) {
//            Log.i("InstrumentationProxy", String.format("--- InstrumentationProxy.newActivity, set resource, actIns null: %b", actIns == null));
//
//            AssetManager assetManager = null;
//            try {
//                assetManager = AssetManager.class.newInstance();
//                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
//                addAssetPath.invoke(assetManager, ApkFileName);
//                LogUtil.D("--- addAssetPath: %s", ApkFileName);
//            } catch (Exception e) {
//                LogUtil.D("--- loadResources err: " + Log.getStackTraceString(e));
//                e.printStackTrace();
//            }
//            Resources superRes = actIns.getResources();
//            Resources newRes = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
//            RefInvoke.setFieldOjbect("android.app.Activity", "mResources", actIns, newRes);
//        }

        mTestFlag = false;

        return actIns;
    }
//
//    public void callActivityOnCreate(Activity activity, Bundle icicle) {
//        Log.i("InstrumentationProxy", String.format("--- InstrumentationProxy.callActivityOnCreate"));
//
//        if (!mTestFlag) {
//            Log.i("InstrumentationProxy", String.format("--- InstrumentationProxy.callActivityOnCreate, set resource, actIns null: %b", activity == null));
//
//            AssetManager assetManager = null;
//            try {
//                assetManager = AssetManager.class.newInstance();
//                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
//                addAssetPath.invoke(assetManager, ApkFileName);
//                LogUtil.D("--- addAssetPath: %s", ApkFileName);
//            } catch (Exception e) {
//                LogUtil.D("--- loadResources err: " + Log.getStackTraceString(e));
//                e.printStackTrace();
//            }
//            Resources superRes = activity.getResources();
//            Resources newRes = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
//            RefInvoke.setFieldOjbect("android.view.ContextThemeWrapper", "mResources", activity, newRes);
//        }
//
//        super.callActivityOnCreate(activity, icicle);
//    }

}
