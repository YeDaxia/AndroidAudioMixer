package io.github.yedaxia.musicnote.data.sp;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.yedaxia.musicnote.app.AppContext;
import io.github.yedaxia.musicnote.app.BuildInfo;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/4.
 */

public class SpConfig {

    private static final String SP_FILE_NAME = String.format("%s_config", BuildInfo.APPLICATION_ID);

    private static final String KEY_RECORD_ADJUST_BYTES_LEN = "record_adjust_bytes_len";

    private SharedPreferences mSp;

    public SpConfig(Context context) {
        mSp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SpConfig sp() {
        return new SpConfig(AppContext.getApplication());
    }

    public void saveRecordAdjustLen(int len){
        mSp.edit().putInt(KEY_RECORD_ADJUST_BYTES_LEN,len).apply();
    }

    public int getRecordAdjustLen(){
        return mSp.getInt(KEY_RECORD_ADJUST_BYTES_LEN, 0);
    }
}
