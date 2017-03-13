
package com.tools.cit;

import com.tools.util.FTLog;

import android.app.Application;
import android.content.res.Configuration;

public class FTApp extends Application {
    public static final int REQUEST_START_MAIN = 1;

    public static final int RESULT_MAIN_BACK = 100;

    private int testID = 0;

    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        // FTLog.i(this, "Application: "+i);
        super.onCreate();
        FTLog.d(this, "SFTT FTApp onCreate");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        FTLog.d(this, "SFTT configuration changed");
    }

}
