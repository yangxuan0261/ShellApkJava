package com.its.testgoogle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.its.testgoogle.model.JsonBean.CTips;

public class Tools {

    private static final String TAG = "--- Tools";

    /**
     * 主线程跑任务
     */
    public static void runOnUiThread(Runnable action) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            action.run();
        } else {
            new Handler(Looper.getMainLooper()).post(action);
        }
    }

    public static void tips02(Context context, CTips tps, final Runnable task) {
        Log.d(TAG, "tips02: 111");
        runOnUiThread(() -> {
            Log.d(TAG, "tips02: 222");

            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(context);
            normalDialog.setTitle(tps.title);
            normalDialog.setMessage(tps.msg);
            normalDialog.setPositiveButton(tps.yes,
                    (dialog, which) -> {
                        if (task != null) {
                            task.run();
                        }
                    });
            normalDialog.setCancelable(false);
            normalDialog.show();
        });
    }


    /**
     * unity 的 systeminfo.deviceuniqueidentifier 接口就是这样实现的
     */
    public static String getDeviceId(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return MD5(ANDROID_ID);
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception ex) {
            Log.e(TAG, "--- MD5 error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return "";
    }


    // ------------------ convert variable
    public static int ConvStr2Int(String str, int fallback) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
            return fallback;
        }
    }


}