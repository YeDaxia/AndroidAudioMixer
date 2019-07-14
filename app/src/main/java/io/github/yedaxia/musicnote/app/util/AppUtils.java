package io.github.yedaxia.musicnote.app.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import io.github.yedaxia.musicnote.R;
import io.github.yedaxia.musicnote.app.AppContext;
import io.github.yedaxia.musicnote.media.AudioUtils;

/**
 * 和App相关的工具类
 *
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/4/24.
 */

public final class AppUtils {

    private AppUtils() {
    }

    /**
     * 获取当前 APP 的包名
     *
     * @return 当前 APP 的包名
     */
    public static String getPackageName() {
        return AppContext.getApplication().getPackageName();
    }

    /**
     * 显示软键盘
     *
     * @param context
     */
    public static void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param attachView
     */
    public static void hideSoftInput(Context context, View attachView) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(attachView.getWindowToken(), 0);
        }
    }


    /**
     * 获取当前进程名
     *
     * @param ac
     * @return
     */
    public static String getCurProcessName(Context ac) {
        int pid = android.os.Process.myPid();//获取进程pid
        String processName = "";
        ActivityManager am = (ActivityManager) ac.getSystemService(Context.ACTIVITY_SERVICE);//获取系统的ActivityManager服务
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        return processName;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static byte[][] loadBeatSoundData() throws IOException {
        byte[][] beatSoundData = new byte[2][];
        beatSoundData[0] = AudioUtils.readWavData(ResUtils.openRawResource(R.raw.beat_strong));
        beatSoundData[1] = AudioUtils.readWavData(ResUtils.openRawResource(R.raw.beat_weak));
        return beatSoundData;
    }
}
