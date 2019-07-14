package io.github.yedaxia.musicnote.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间处理实用类
 *
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/4/25.
 */

public final class TimeUtils {

    public static final String YYMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYMD = "yyyy-MM-dd";
    public static final String MD = "MM-dd";

    private static TimeZone GMT8 = TimeZone.getTimeZone("GMT+8");

    private TimeUtils() {
    }

    /**
     * 给dateStr加上days天后返回的日期字符串
     *
     * @param dateStr
     * @param days
     * @return
     */
    public static String addDays(String dateStr, int days) {
        if (dateStr == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat(YYMD);
        try {
            Date date = df.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, days);
            return df.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取timeStr时间字符串中的年份
     *
     * @param timeStr
     * @return
     */
    public static int getYearOfTime(String timeStr) {
        Date date = parseTime(timeStr);
        if (date == null) {
            return -1;
        }
        Calendar cal = Calendar.getInstance(GMT8);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取两个时间相差的天数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int getDiffDays(String beginTime, String endTime) {
        final Date beginDate = parseTime(beginTime);
        final Date endDate = parseTime(endTime);
        if (beginDate == null || endDate == null) {
            return -1;
        }
        final long diffMillSec = endDate.getTime() - beginDate.getTime();
        return (int) diffMillSec / (60 * 60 * 24 * 1000);
    }

    /**
     * 获取两个时间相差的天数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int getDiffDays(String beginTime, Date endTime) {
        final Date beginDate = parseTime(beginTime);
        if (beginDate == null || endTime == null) {
            return 0;
        }
        final long diffMillSec = endTime.getTime() - beginDate.getTime();
        return (int) diffMillSec / (60 * 60 * 24 * 1000);
    }

    /**
     * 格式化日期显示
     *
     * @param timeStr
     * @param format
     * @return
     */
    public static String formatDateStr(String timeStr, String format) {
        Date date = parseTime(timeStr);
        if (date == null) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.CHINA);
        df.setTimeZone(GMT8);
        return df.format(date);
    }

    /**
     * 把时间字符串转化成Date类型
     *
     * @param timeStr
     * @return
     */
    public static Date parseTime(String timeStr) {
        SimpleDateFormat df;
        if (timeStr.contains(":")) {
            df = new SimpleDateFormat(YYMD_HMS, Locale.CHINA);
        } else {
            df = new SimpleDateFormat(YYMD, Locale.CHINA);
        }
        try {
            df.setTimeZone(GMT8);
            return df.parse(timeStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isExpire(String startBorrowDay, int days) {
        SimpleDateFormat df = new SimpleDateFormat(YYMD,Locale.CHINA);
        df.setTimeZone(GMT8);
        Date starBorrowDay = null;
        try {
            starBorrowDay = df.parse(startBorrowDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endBorrowDay = new Date();
        long diff = endBorrowDay.getTime() - starBorrowDay.getTime();
        if(diff < days *24L * 60L * 60L * 1000L){
            return false;
        }
        return true;
    }

    public static String getReturnDay(String startBorrowDay, int days) {
        SimpleDateFormat df = new SimpleDateFormat(YYMD,Locale.CHINA);
        df.setTimeZone(GMT8);
        Date starBorrowDay = null;
        try {
            starBorrowDay = df.parse(startBorrowDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long returnTime = starBorrowDay.getTime()+ days *24 * 3600000L;
        Date date = new Date(returnTime);
        return  df.format(date);
    }

    public static int getDiffFromDay(String startBorrowDay, int days){
        SimpleDateFormat df = new SimpleDateFormat(YYMD,Locale.CHINA);
        df.setTimeZone(GMT8);
        Date starBorrowDay = null;
        try {
            starBorrowDay = df.parse(startBorrowDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endBorrowDay = new Date();
        long diff = endBorrowDay.getTime() - starBorrowDay.getTime();
        int day = (int) (diff/(24 *3600000L));
        return days - day;
    }

}
