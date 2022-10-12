package com.its.testgoogle.timer;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class TimerMgr {

    private static TimerMgr instance = null;

    public static TimerMgr getIns() {
        if (instance == null) {
            instance = new TimerMgr();
            instance.init();
        }
        return instance;
    }

    private int mAutoId = 1;
    private Map<Integer, Timer> mTimerMap = new ConcurrentHashMap<>();

    public int addTimer(long interval, Runnable fn) {
        int id = mAutoId++;
        Timer tmr = new Timer(id, interval, fn);
        mTimerMap.put(id, tmr);
        return id;
    }

    public int setTimeout(long interval, Runnable fn) {
        int id = addTimer(interval, fn);
        mTimerMap.get(id).setTimeout(true);
        return id;
    }

    public void removeTimer(int id) {
        Timer tmr = mTimerMap.get(id);
        if (tmr != null) {
            mTimerMap.remove(id);
            tmr.destroy();
        }
    }

    private void update(long dt) {
        for (Map.Entry<Integer, Timer> entry : mTimerMap.entrySet()) {
            if (entry.getValue().call(dt)) {
                mTimerMap.remove(entry.getKey());
            }
        }
    }

    // ----------- Android 相关
    private java.util.Timer mTimer = new java.util.Timer();
    private long mFrameRate = 16; // 帧
    private TimerTask mTask;

    private void init() {
        mTask = new TimerTask() {
            @Override
            public void run() {
                update(mFrameRate);
            }
        };
        mTimer.schedule(mTask, 1, mFrameRate); //60 帧
    }

    public void destroy() {
        mTimer.cancel();
        mTask.cancel();

        for (Map.Entry<Integer, Timer> entry : mTimerMap.entrySet()) {
            entry.getValue().destroy();
        }
        mTimerMap.clear();
        instance = null;
    }
}
