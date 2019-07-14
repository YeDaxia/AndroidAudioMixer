package io.github.yedaxia.musicnote.util;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2016/11/30
 */

public interface ILog {
    /**
     * verbose log
     * @param tag
     * @param msg
     * @param args
     */
    void v(String tag, String msg, Object... args);

    /**
     * debug log
     * @param tag
     * @param msg
     * @param args
     */
    void d(String tag, String msg, Object... args);
    /**
     * info log
     * @param tag
     * @param msg
     * @param args
     */
    void i(String tag, String msg, Object... args);

    /**
     * warn log
     * @param tag
     * @param msg
     * @param args
     */
    void w(String tag, String msg, Object... args);

    /**
     * error log
     * @param tag
     * @param msg
     * @param args
     */
    void e(String tag, String msg, Object... args);
}
