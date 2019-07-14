package io.github.yedaxia.musicnote.app;

import android.app.Application;
import android.os.Process;

import io.github.yedaxia.musicnote.app.util.AppUtils;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/23.
 */

public class OurApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (getPackageName().equals(AppUtils.getProcessName(Process.myPid()))) {
            AppContext.initContext(this);
        }
    }

}