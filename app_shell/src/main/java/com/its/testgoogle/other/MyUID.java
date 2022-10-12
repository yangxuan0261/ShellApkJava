package com.its.testgoogle.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.its.testgoogle.EncryptTool;
import com.its.testgoogle.FileTool;
import com.its.testgoogle.LogUtil;

import java.util.UUID;

/**
 * uid 逻辑
 */

public class MyUID {
    private static final String TAG = "--- MyUID";

    public static String get(Context context) {

        String file = "adruid.db";
        String uid = load(context, file); // 读缓存
        if (uid != null && uid.length() > 0) {
            LogUtil.TD(TAG, "--- cache uid: %s", uid);
            return uid;
        }

        uid = getAndroidUid(context);
        if (uid != null && uid.length() > 0) {
            LogUtil.TD(TAG, "--- getAndroidUid uid: %s", uid);
            save(context, file, uid);
            return uid;
        }

        uid = getCustomUid(context);
        if (uid != null && uid.length() > 0) {
            LogUtil.TD(TAG, "--- getCustomUid uid: %s", uid);
            save(context, file, uid);
            return uid;
        }

        LogUtil.A(uid != null, "--- uid is null");
        return null;
    }

    /**
     * android 自带 uid
     */
    @SuppressLint("HardwareIds")
    private static String getAndroidUid(Context context) {
        try {
            // 旧方式, 已废弃
            // String uid = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
            String uid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return EncryptTool.md5Str(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 自定义 uid 逻辑
     */
    private static String getCustomUid(Context context) {
        String uid = UUID.randomUUID().toString(); // 简单使用 唯一 id
        return EncryptTool.md5Str(uid);
    }

    private static void save(Context context, String path, String uid) {
        FileTool.writeFileEncrypt(context, path, uid);
    }

    private static String load(Context context, String path) {
        return FileTool.readFileEncrypt(context, path);
    }
}
