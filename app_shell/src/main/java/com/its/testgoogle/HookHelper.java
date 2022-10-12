package com.its.testgoogle;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by yds
 * on 2020/2/14.
 */
public class HookHelper {
    public static final String PLUGIN_INTENT = "plugin_intent";

    public static void hookInstrumentation(Context context, String className) throws Exception {
        Log.i("HookHelper", String.format("--- HookHelper.hookInstrumentation, className: %s", className));

        Class<?> clazz = Class.forName("android.app.ActivityThread");
        Field sCurrentActivityThreadField = RefInvoke.getField(clazz, "sCurrentActivityThread");
        Field mInstrumentationField = RefInvoke.getField(clazz, "mInstrumentation");
        Object currentActivityThread = sCurrentActivityThreadField.get(clazz);
        Instrumentation instrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
        PackageManager packageManager = context.getPackageManager();
        InstrumentationProxy instrumentationProxy = new InstrumentationProxy(instrumentation, packageManager, className);
        RefInvoke.setFieldOjbect(clazz, "mInstrumentation", currentActivityThread, instrumentationProxy);
    }
}
