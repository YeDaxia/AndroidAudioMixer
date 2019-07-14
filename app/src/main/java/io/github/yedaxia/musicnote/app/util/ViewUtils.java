package io.github.yedaxia.musicnote.app.util;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

/**
 *
 * @author Darcy https://yedaxia.github.io/
 * @version 2016/12/2
 */

public final class ViewUtils {

    private ViewUtils(){}

    /**
     * Calculate the font height of a specific TextPaint
     *
     * @throws NullPointerException
     */
    public static float calculateFontHeight(TextPaint textPaint) {
        if (textPaint == null) {
            throw new NullPointerException("textPaint is null");
        }
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        return fontMetrics.bottom - fontMetrics.top;
    }


    /**
     * calculate the width of a string with a specific TextPaint
     *
     * @throws NullPointerException
     */
    public static int calculateStringWidth(Paint paint, String s) {
        if (paint == null || s == null) {
            throw new NullPointerException("paint or s is null");
        }
        int iRet = 0;
        int len = s.length();
        if (len > 0) {
            float[] widths = new float[len];
            paint.getTextWidths(s, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }


    /**
     * 设置View的背景
     */
    public static void setViewBackground(View v, Drawable background) {
        if (v == null || background == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(background);
        } else {
            v.setBackgroundDrawable(background);
        }
    }


    /**
     * parent是否包含child
     *
     * @throws NullPointerException
     */
    public static boolean containChild(ViewGroup parent, View child) {

        if (parent == null || child == null) {
            throw new NullPointerException("parent view group or child view is null");
        }

        if (parent == child) {
            return true;
        }

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            if (parent.getChildAt(i) == child) {
                return true;
            }
        }

        return false;
    }


    /**
     * 滚动ListView到第一条
     */
    public static void scrollListViewToTop(final ListView listView) {
        if (listView != null && listView.getChildCount() > 0) {
            listView.smoothScrollToPositionFromTop(0, 0, 100);
            listView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.smoothScrollToPositionFromTop(0, 0, 0);
                    listView.setSelection(0);
                }
            }, 100);
        }
    }


    /**
     * 获取忽略两端空白的字符串
     */
    public static String trimText(TextView textView) {
        if (textView == null) {
            return null;
        }
        return textView.getText().toString().trim();
    }


    /**
     * 设置TextView左边的drawable
     */
    public static void setTextViewLeftDrawable(TextView tv, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(tv.getContext(), drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tv.setCompoundDrawables(drawable, null, null, null);
    }


    /**
     * 设置TextView左边的drawable
     */
    public static void setTextViewRightDrawable(TextView tv, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(tv.getContext(), drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tv.setCompoundDrawables(null, null, drawable, null);
    }


    /**
     * 清楚TextView的drawable
     */
    public static void clearTextViewDrawable(TextView tv) {
        tv.setCompoundDrawables(null, null, null, null);
    }

    /**
     * 解决ViewPager在SwipeRefreshLayout中的事件冲突
     * @param viewPager
     * @param refreshLayout
     */
    public static void resolveViewPagerInSwipeRefresh(ViewPager viewPager, final SwipeRefreshLayout refreshLayout){
        if(viewPager == null || refreshLayout == null){
            return;
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                refreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    /**
     * position位置是否在当前屏幕的listView显示
     */
    public static boolean isVisibleInListView(ListView listView, int position) {
        int headerCount = listView.getHeaderViewsCount();
        int footerCount = listView.getFooterViewsCount();
        int firstPos = listView.getFirstVisiblePosition();
        int lastPos = listView.getLastVisiblePosition();
        return firstPos <= position+headerCount &&
                position+footerCount <= lastPos;
    }


    /**
     * position位置是否在当前屏幕的recyclerview显示
     */
    public static boolean isVisibleInRecyclerView(LinearLayoutManager layoutManager, int position) {
        int fristPos = layoutManager.findFirstVisibleItemPosition();
        int lastPos = layoutManager.findLastVisibleItemPosition();

        return position >= fristPos && position <= lastPos;
    }
}
