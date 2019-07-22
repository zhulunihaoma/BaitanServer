package com.xll.baitaner.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期类
 */
public class DateUtils {

    /**
     * 获取当前24小时前时间
     *
     * @return
     */
    public static Date dayBefore24HoursFromNow() {
        ZoneId zoneId = ZoneId.systemDefault();
        //当前时间的24小时前
        ZonedDateTime zonedDateTime = LocalDateTime.now().minusHours(24).atZone(zoneId);
        //转成Date
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 当前时间。前7个工作日
     *
     * @return
     */
    public static Date dayBefore7WorkDaysFromNow() {
        LocalDateTime nowTime = LocalDateTime.now();
        int inc = 1;
        int workDay = 0;
        while (workDay < 7) {
            if (!nowTime.minusDays(inc).getDayOfWeek().equals(DayOfWeek.SUNDAY) &&
                    !nowTime.minusDays(inc).getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                workDay++;
            }
            inc++;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = nowTime.minusDays(inc).atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * Date转换成String。
     *
     * @param date
     * @return
     */
    public static String toString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(date);
    }

    /**
     * Date转换成String yyyy-MM-dd。
     *
     * @param date
     * @return
     */
    public static String toStringtime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(date);
    }

    /**
     * 供工单用，格式：YYMMDD
     *
     * @param date
     * @return
     */
    public static String toOrderString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

        return sdf.format(date);
    }

    /**
     * 字符串转换到时间格式
     *
     * @param dateStr 需要转换的字符串
     * @return Date 返回转换后的时间
     * @throws ParseException 转换异常
     */
    public static Date stringToYearMonth(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 字符串转换到时间格式
     *
     * @param dateStr 需要转换的字符串(yyyy-MM-dd)
     * @return Date 返回转换后的时间
     * @throws ParseException 转换异常
     */
    public static Date stringToYearMonthDay(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            System.out.println("[WARN:]" + e.getMessage());
        }
        return null;
    }

    /**
     * 字符串转换到时间格式
     *
     * @param dateStr 需要转换的字符串
     * @return Timestamp 返回转换后的时间戳
     * @throws ParseException 转换异常
     */
    public static Timestamp stringToYearMonthDayTimestamp(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            String timeStampString = sdf.format(sdf.parse(dateStr)) + " 00:00:00"; //补全时间格式
            return Timestamp.valueOf(timeStampString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取当前时间yyyy-MM-dd
     */
    public static String getCurrentDateymd() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf.format(date);
        return time;
    }

    /**
     * 获取当前时间YYMMDD
     */
    public static String getNowDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String time = sdf.format(date);
        return time;
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        return time;
    }

    /**
     * 获取当前时间 YYYY-MM-DD
     * format: YYYY-MM-DD or YYYY-MM-DD HH:mm:ss
     */
    public static String getCurrentDateByFormat(String format) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String time = sdf.format(date);
        return time;
    }


    /**
     * 格式化时间
     */
    public static String formatTimeYMD(Timestamp t) {
        Date date = new Date(t.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        return time;
    }

    /**
     * 格式化时间
     */
    public static String formatTimeYMDHMS(Timestamp t) {
        Date date = new Date(t.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(date);
        return time;
    }

    /**
     * 获取当前时间戳
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 输出年月：yyyy-MM
     *
     * @param date
     * @return
     */
    public static String toYearMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        return sdf.format(date);
    }

    /**
     * 输出年月日：yyyyMMdd
     *
     * @param date
     * @return
     */
    public static String toYearMonthDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    /**
     * 输出字符串(如果date参数为null，则以当日为默认值）
     *
     * @param date   日期
     * @param format 格式
     * @return
     */
    public static String format(Date date, String format) {
        if (date == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 输出时分秒 毫秒：hhmmssfff
     *
     * @param date
     * @return
     */
    public static String toHourMinSecM(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");
        return sdf.format(date);
    }

    /**
     * 根据这个月Date获取下个月Date。
     *
     * @param date
     * @return
     */
    public static Date getNextDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, +1);

        return cal.getTime();
    }

    /**
     * 获取明天
     *
     * @return 返回明天
     */
    public static String tom() {
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 获取后天date
     *
     * @return 返回后天date
     */
    public static Date tomtom() {
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 2);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        return date;
    }

    /**
     * 获取一年之后的今天
     *
     * @return 返回一年之后的今天Timestamp
     */
    public static Timestamp afterYear() {
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 365);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        return new Timestamp(date.getTime());
    }

    /*
     * 获取2个日期之间的天数
     */
    public static int getDaySub(String beginDateStr, String endDateStr) {
        long day = 0;
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:SS");
        Date beginDate;
        Date endDate;
        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
            day = (endDate.getTime() - beginDate.getTime())
                    / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) day;
    }

    /**
     * 获取某天后的n天后date
     *
     * @return 返回某天后的n天后date
     */
    public static String getAfterNDay(String dateStr, int n) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:SS");
            Date date = format.parse(dateStr);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, n);// 把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAfterNDayYMD(String dateStr, int n) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd");
            Date date = format.parse(dateStr);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, n);// 把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前年份
     *
     * @param date 日期
     * @return
     */
    public static String getYearOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    /**
     * 获取当前年份
     *
     * @param date 日期
     * @return
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
}
