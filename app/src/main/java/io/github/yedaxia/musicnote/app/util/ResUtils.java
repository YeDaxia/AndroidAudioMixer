package io.github.yedaxia.musicnote.app.util;

import android.graphics.drawable.Drawable;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.io.InputStream;

import io.github.yedaxia.musicnote.app.AppContext;

/**
 * 资源实用类
 *
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/3/29.
 */

public final class ResUtils {

    private ResUtils() {
    }

    /**
     * 根据id获取字符串
     *
     * @param resId 资源id
     * @return
     */
    public static String getString(@StringRes int resId) {
        return AppContext.getApplication().getString(resId);
    }

    /**
     * 根据id获取格式化填充的字符串
     * @param resId
     * @param values
     * @return
     */
    public static String getString(@StringRes int resId, Object... values) {
        return String.format(AppContext.getApplication().getString(resId), values);
    }

    /**
     * 根据id获取颜色
     *
     * @param resId
     * @return
     */
    public static int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(AppContext.getApplication(), resId);
    }

    /**
     * 根据id获取drawable
     *
     * @param resId
     * @return
     */
    public static Drawable getDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(AppContext.getApplication(), resId);
    }

    /**
     * 根据id获取dimen的像素值
     *
     * @param resId
     * @return
     */
    public static int getDimenPixel(@DimenRes int resId) {
        return AppContext.getApplication().getResources().getDimensionPixelSize(resId);
    }

    public static InputStream openRawResource(@RawRes int resId){
        return AppContext.getApplication().getResources().openRawResource(resId);
    }
}
