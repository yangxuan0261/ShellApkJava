package com.its.testgoogle.other;

import android.content.Context;
import android.os.Build;

import com.its.testgoogle.Tools;
import com.its.testgoogle.model.JsonBean.CPhoneInfo;

import java.util.Locale;

/**
 * 系统工具类 - https://blog.csdn.net/zhuwentao2150/article/details/51946387
 * Android 手机信息获取详解 - https://www.jianshu.com/p/ca869aa2fd72
 */
public class SystemInfoUtil {

    private static String TAG = "--- SystemInfoUtil";


    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static int getSystemSdk() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    public static String getPackgeName(Context ctx) {
        return ctx.getPackageName();
    }


    public static CPhoneInfo getSysInfo(Context context) {
        CPhoneInfo pi = new CPhoneInfo();
        pi.SystemLanguage = getSystemLanguage();
        pi.SystemVersion = getSystemVersion();
        pi.SystemSdk = getSystemSdk();
        pi.SystemModel = getSystemModel();
        pi.DeviceBrand = getDeviceBrand();
        pi.PackgeName = getPackgeName(context);
        pi.DeviceID = Tools.getDeviceId(context);
        return pi;
    }

//    /**
//     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
//     *
//     * @return 手机IMEI
//     */
//    public static String getIMEI(Context ctx) {
//        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
//        if (tm != null) {
//            return tm.getDeviceId();
//        }
//        return null;
//    }
}