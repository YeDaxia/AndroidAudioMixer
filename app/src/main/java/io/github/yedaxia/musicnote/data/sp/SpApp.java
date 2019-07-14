package io.github.yedaxia.musicnote.data.sp;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.yedaxia.musicnote.app.AppContext;
import io.github.yedaxia.musicnote.app.BuildInfo;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/12.
 */

public class SpApp {

    private static final String SP_FILE_NAME = String.format("%s_app", BuildInfo.APPLICATION_ID);

    private static final String KEY_APP_ACTIVATE = "app_activate";

    private SharedPreferences mSp;

    public SpApp(Context context) {
        mSp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SpApp sp() {
        return new SpApp(AppContext.getApplication());
    }

    public boolean isAppActivate(){
        return mSp.getBoolean(KEY_APP_ACTIVATE, false);
    }

    public void saveAppActivate(boolean activate){
        mSp.edit().putBoolean(KEY_APP_ACTIVATE, activate).apply();
    }
}
