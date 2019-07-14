package io.github.yedaxia.musicnote.app.util;

import android.content.Context;
import androidx.annotation.StringRes;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * Toast 提示实用类
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/3/15.
 */

public final class ToastUtils {

    private ToastUtils() {
    }

    /**
     * 显示成功的Toast信息
     * @param context
     * @param message
     */
    public static void showSuccessToast(Context context, String message){
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();
    }

    /**
     * 显示成功的Toast信息
     * @param context
     * @param resId
     */
    public static void showSuccessToast(Context context, @StringRes int resId) {
        Toasty.success(context, ResUtils.getString(resId), Toast.LENGTH_SHORT, true).show();
    }

    /**
     * 显示警告的Toast信息
     * @param context
     * @param message
     */
    public static void showWarnToast(Context context, String message){
        Toasty.warning(context, message, Toast.LENGTH_SHORT, true).show();
    }

    /**
     * 显示警告的Toast信息
     * @param context
     * @param resId
     */
    public static void showWarnToast(Context context, @StringRes int resId) {
        Toasty.warning(context, ResUtils.getString(resId), Toast.LENGTH_SHORT, true).show();
    }

    /**
     * 显示错误的Toast信息
     * @param context
     * @param message
     */
    public static void showErrorToast(Context context, String message){
        Toasty.error(context, message, Toast.LENGTH_SHORT, true).show();
    }

    /**
     * 显示错误的Toast信息
     * @param context
     * @param resId
     */
    public static void showErrorToast(Context context, @StringRes int resId) {
        Toasty.error(context, ResUtils.getString(resId), Toast.LENGTH_SHORT, true).show();
    }

    /**
     * 显示一般的Toast信息
     * @param context
     * @param message
     */
    public static void showInfoToast(Context context, String message){
        Toasty.info(context,message).show();
    }

    /**
     * 显示一般的Toast信息
     *
     * @param context
     * @param resId
     */
    public static void showInfoToast(Context context, @StringRes int resId) {
        Toasty.info(context, ResUtils.getString(resId)).show();
    }
}
