package com.its.testgoogle;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHelper {
    // 日志登等级
    public @interface EHttpCode {
        int Ok = 200;
        int Fail = -1;
        int Exception = -2;
        int UrlError = -3;
    }

    public static class SHttp {
        public int code;
        public String msg;
        public boolean isRsp;
        public byte[] bytes;

        public String toString() {
            return String.format("--- code: %d, msg: %s", code, msg);
        }
    }

    public interface HttpRunnable {
        void run(final SHttp rsp);
    }

    public static SHttp okhttpPostSync(final String url, final String jsonMsg) {
        SHttp rsp = new SHttp();
        new Thread(() -> { // 防止主线程调用
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonMsg);
            Request post = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(post);
            try {
                Response response = call.execute();
                rsp.code = response.code();
                rsp.bytes = response.body().bytes();
                rsp.msg = new String(rsp.bytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                rsp.code = -2;
                rsp.msg = e.getMessage();
            } finally {
                rsp.isRsp = true;
            }
        }).start();

        while (!rsp.isRsp) { // 阻塞等待返回
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return rsp;
    }
}
