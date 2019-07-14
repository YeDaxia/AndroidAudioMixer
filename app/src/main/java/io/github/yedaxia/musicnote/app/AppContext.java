package io.github.yedaxia.musicnote.app;

import android.app.Application;
import android.os.Environment;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.File;

import io.github.yedaxia.musicnote.data.entity.DaoMaster;
import io.github.yedaxia.musicnote.data.entity.DaoSession;
import io.github.yedaxia.musicnote.util.FileUtils;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/4.
 */

public class AppContext {

    private static final String SD_ROOT_PATH = "MusicNotePlus";

    private static Application sApplication;

    private static DaoSession daoSession;

    static void initContext(Application app){
        sApplication = app;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(app, "music-note-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDb());
        ZXingLibrary.initDisplayOpinion(app);
        daoSession = daoMaster.newSession();
    }
    /**
     * 获取音频的临时存放目录
     * @return
     */
    public static File getAudioTempPath(){
        return FileUtils.mkDirs(new File(rootSdPath(), "Audio"));
    }

    /**
     * 音频输出目录
     * @return
     */
    public static File getAudioOutPath(){
        return FileUtils.mkDirs(new File(rootSdPath(), "Output"));

    }

    private static File rootSdPath(){
        return FileUtils.mkDirs(new File(Environment.getExternalStorageDirectory(), SD_ROOT_PATH));
    }

    /**
     * 获取Application对象
     * @return
     */
    public static Application getApplication(){
        return sApplication;
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
