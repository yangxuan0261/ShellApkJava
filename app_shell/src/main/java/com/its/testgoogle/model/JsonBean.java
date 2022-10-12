package com.its.testgoogle.model;

import java.util.HashMap;
import java.util.Map;

/*
 * json 用的 数据结构, 需要防止被混淆代码
 * */

public class JsonBean {

    public static class CTips {
        public String title = "Tips";
        public String msg = "null";
        public String yes = "yes";
        public String no = "no";
    }


    public static class CReport {
        public int Plat;
        public int Os;
        public int Appid;
        public String Deviceid;
        public String Afdata;
        public String Ggdata;
        public String Addata;
    }

    // 透传数据
    public static class CTransfer {
        public String AfJson;
        public String GgJson;
        public String AdJson;
        public String ExtA;
    }

    public static class CPackDB {
        public int PlatId;
        public int AppId;
        public int Os;
        public String ReportUrl;
    }


    public static class CPhoneInfo {
        public String SystemLanguage;
        public String SystemVersion;
        public int SystemSdk;
        public String SystemModel;
        public String DeviceBrand;
        public String PackgeName;
        public String DeviceID;
        public int ThirdSdk;
    }

    public static class CLocReqInfo {
        public int timeout;
    }
}