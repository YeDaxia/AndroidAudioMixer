package io.github.yedaxia.musicnote.app;

import io.github.yedaxia.musicnote.BuildConfig;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/1.
 */

public final class BuildInfo {

    /**
     * they are not final, so they won't change with the BuildConfig values!
     */
    //CHECKSTYLE:OFF
    public  static String  VERSION_NAME = BuildConfig.VERSION_NAME;
    /**version code*/
    //CHECKSTYLE:OFF
    public  static int     VERSION_CODE = BuildConfig.VERSION_CODE;
    /**application_id*/
    //CHECKSTYLE:OFF
    public  static String APPLICATION_ID = BuildConfig.APPLICATION_ID;

    /**debug 环境*/
    public static final int ENV_DEBUG =0x1;
    /**release 环境*/
    public static final int ENV_RELEASE =0x2;
    /**运行测试用例环境*/
    public static final int ENV_MOCK = 0x3;
    /**开发环境*/
    public static final int ENV = ENV_RELEASE;

    public static final String PROVIDER_AUTHORITY = APPLICATION_ID+ ".fileProvider";
}
