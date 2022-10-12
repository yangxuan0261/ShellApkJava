package com.its.testgoogle.timer;

public class Timer {
    private int mId;
    private long mTotalTime;
    private Runnable mCallback;
    private boolean mIsTimeout = false;

    private long mCurrTime = 0;

    public Timer(int id, long interval, Runnable fn) {
        mId = id;
        mTotalTime = interval;
        mCallback = fn;
    }

    public boolean call(long dt) {
        if (mCallback == null) {
            return true;
        }

        mCurrTime += dt;
        if (mCurrTime >= mTotalTime) {
            mCallback.run();

            if (mIsTimeout) {
                mCallback = null;
                return true;
            } else {
                mCurrTime = 0;
            }
        }
        return false;
    }

    public void setTimeout(boolean b) {
        mIsTimeout = b;
    }

    public void destroy() {
        mCallback = null;
    }
}
