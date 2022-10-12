package com.its.testgoogle;

import android.util.Log;

import com.its.testgoogle.Define.ELogLevel;

public class LogUtil {

    private static final String TAG = "--- LogUtil";
    private static int logLevel = ELogLevel.Debug;

    public static void SetLv(@ELogLevel int lv) {
        logLevel = lv;
    }

    public static void SetLv(String strLv) {
        int lv = strLv == null ? ELogLevel.Error : Tools.ConvStr2Int(strLv, ELogLevel.Error);
        SetLv(lv);
    }

    public static void D(String fmt, Object... args) {
        TD(TAG, fmt, args);
    }

    public static void W(String fmt, Object... args) {
        TW(TAG, fmt, args);
    }

    public static void E(String fmt, Object... args) {
        TE(TAG, fmt, args);
    }

    public static void A(boolean b, String fmt, Object... args) {
        TA(TAG, b, fmt, args);
    }

    public static void TD(String tag, String fmt, Object... args) {
        if (ELogLevel.Debug < logLevel) return;
        Log.d(tag, String.format(fmt, args));
    }

    public static void TW(String tag, String fmt, Object... args) {
        if (ELogLevel.Warn < logLevel) return;
        Log.w(tag, String.format(fmt, args));
    }

    public static void TE(String tag, String fmt, Object... args) {
        if (ELogLevel.Error < logLevel) return;
        Log.e(tag, String.format(fmt, args));
    }

    public static void TA(String tag, boolean b, String fmt, Object... args) {
        if (ELogLevel.Error < logLevel) return;
        if (!b) {
            throw new AssertionError(String.format(tag + " : " + fmt, args));
        }
    }
}
