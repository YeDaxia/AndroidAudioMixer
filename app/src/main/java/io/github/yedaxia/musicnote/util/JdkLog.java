package io.github.yedaxia.musicnote.util;

/**
 * @author wufei
 * @version v1.0
 */
public class JdkLog implements ILog {

    @Override public void v(String tag, String msg, Object... args) {
        System.out.printf("[%s--v]\t%s\n", tag, msg);
    }

    @Override public void d(String tag, String msg, Object... args) {
        System.out.printf("[%s--d]\t%s\n", tag, msg);
    }


    @Override public void i(String tag, String msg, Object... args) {
        System.out.printf("[%s--i]\t%s\n", tag, msg);
    }


    @Override public void w(String tag, String msg, Object... args) {
        System.out.printf("[%s--w]\t%s\n", tag, msg);
    }


    @Override public void e(String tag, String msg, Object... args) {
        System.out.printf("[%s--e]\t%s\n", tag, msg);
    }
}
