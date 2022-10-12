package com.its.testgoogle;

import android.content.Context;
import android.util.Log;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.its.testgoogle.model.JsonBean.CTips;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class InstrumentedTest {

    private final String TAG = "--- aut";
    private Context mCtx = null;

    @Before
    public void initContext() {
        mCtx = getInstrumentation().getTargetContext();
//        Intent intent = new Intent(mCtx, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mCtx.startActivity(intent);
    }

    @Test
    public void useAppContext() {
        Log.d(TAG, "--- package name 111:" + mCtx.getPackageName());
        assertEquals("com.its.testgoogle", mCtx.getPackageName());
    }
}
