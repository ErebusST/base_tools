/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.situ.enumeration.DateFormatEnum;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateUtils，时间操作类
 *
 * @author 司徒彬
 * @date 2016年10月27日13 :50:56
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {

    /**
     * Gets date string.
     *
     * @param date the date
     * @return the date string
     */
    public static String getDateString(Timestamp date) {
        return getDateString(date, null);
    }

    /**
     * Gets date string.
     *
     * @param date           the date
     * @param dateFormatEnum the date format enum
     * @return the date string
     */
    public static String getDateString(Timestamp date, DateFormatEnum dateFormatEnum) {
        if (ObjectUtils.isNull(date)) {
            return "";
        }
        dateFormatEnum = dateFormatEnum == null ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatEnum.getValue());
        return simpleDateFormat.format(date);
    }

    /**
     * 获得当前时间的字符串 默认格式：yyyy-MM-dd HH:mm:ss
     *
     * @return the date string
     */
    public static String getDateString() {
        return getDateString(new Date(), null);
    }

    /**
     * 根据制指定的格式，获得当前时间的字符串
     *
     * @param dateFormatEnum the date format enum
     * @return the date string
     */
    public static String getDateString(DateFormatEnum dateFormatEnum) {
        return getDateString(new Date(), dateFormatEnum);
    }

    /**
     * 根据指定的格式，得到指定时间的字符串
     *
     * @param date           the date
     * @param dateFormatEnum the date format enum
     * @return the date string
     */
    public static String getDateString(Date date, DateFormatEnum dateFormatEnum) {
        Timestamp timestamp = new Timestamp(date.getTime());
        return getDateString(timestamp, dateFormatEnum);
    }

    /**
     * 获取日期 天数差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return the days count
     * @return：相差天数
     */
    public static int getDaysCount(Date startDate, Date endDate) {
        int dayInt = 0;
        long date1Time = startDate.getTime();
        long date2Time = endDate.getTime();
        if (date2Time > date1Time) {
            dayInt = (int) ((date2Time - date1Time) / 1000 / 60 / 60 / 24);
        }
        return dayInt;
    }

    /**
     * 获取两个时间的时间差 秒~
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the seconds count
     */
    public static int getSecondsCount(Date startDate, Date endDate) {
        long time = endDate.getTime() - startDate.getTime();
        int totalS = new Long(time / 1000).intValue();
        return totalS;
    }

    /**
     * 获取当前时间字符串(年月日时分秒)
     *
     * @return current year str
     */
    public static String getCurrentYearStr() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        return year + "";
    }

    /**
     * @return current year str
     */
    public static int getYear(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        int year = cal.get(Calendar.YEAR);
        return year;
    }

    public static int getMonth(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        int month = cal.get(Calendar.MONTH);
        return month + 1;
    }


    public static int getDayOfYear(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        int day = cal.get(Calendar.DAY_OF_YEAR);
        return day;
    }

    public static int getWeekOfYear(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        int day = cal.get(Calendar.WEEK_OF_YEAR);
        return day;
    }

    public static int getWeekOfMonth(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        int day = cal.get(Calendar.WEEK_OF_MONTH);
        return day;
    }


    /**
     * 获得当前时间
     *
     * @return the date
     */
    public static Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 根据指定的字符串获得时间
     *
     * @param dateStr 时间字符串 yyyy-MM-dd HH:mm:ss
     * @return date
     * @throws ParseException the parse exception
     */
    public static Date getDate(String dateStr) throws ParseException {
        return getDate(dateStr, null);
    }

    /**
     * Gets date.
     *
     * @param dateStr        the date str
     * @param dateFormatEnum the date format enum
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date getDate(String dateStr, DateFormatEnum dateFormatEnum) throws ParseException {
        dateFormatEnum = dateFormatEnum == null ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatEnum.getValue());
        return simpleDateFormat.parse(dateStr);
    }

    /**
     * Gets timestamp.
     *
     * @param dateStr the date str
     * @return the timestamp
     * @throws ParseException the parse exception
     */
    public static Timestamp getTimestamp(Object dateStr) {
        return getTimestamp(DataSwitch.convertObjectToString(dateStr), 0, null);
    }

    /**
     * Gets timestamp.
     *
     * @param dateStr the date str
     * @param day     the day
     * @return the timestamp
     * @throws ParseException the parse exception
     */
    public static Timestamp getTimestamp(String dateStr, int day) throws ParseException {
        return getTimestamp(dateStr, day, null);
    }

    /**
     * Gets timestamp.
     *
     * @param dateStr        the date str
     * @param day            the day
     * @param dateFormatEnum the date format enum
     * @return the timestamp
     * @throws ParseException the parse exception
     */
    public static Timestamp getTimestamp(String dateStr, int day, DateFormatEnum dateFormatEnum) {
        try {
            dateFormatEnum = dateFormatEnum == null ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
            Timestamp result = new Timestamp(getDate(dateStr, dateFormatEnum).getTime());
            result = addDay(result, day);
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 获取时间对象
     *
     * @param dateStr   时间字符串
     * @param formatStr 时间格式字符串 yyyy-MM-dd
     * @return date by format
     * @throws ParseException the parse exception
     */
    public static Date getDateByFormat(String dateStr, String formatStr) throws ParseException {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.parse(dateStr);
    }

    /**
     * 获取当前时间字符串
     *
     * @param date      时间对象
     * @param formatStr 时间格式字符串
     * @return date format str
     */
    public static String getDateFormatStr(Date date, String formatStr) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(date);
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @return second timestamp
     */
    public static int getSecondTimestamp() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0, length - 3));
        } else {
            return 0;
        }
    }

    /**
     * 获取数据查询日期，格式是：2014-04-02，day是天差，如果传-1是昨天的日期
     *
     * @param day the day
     * @return the timestamp
     * @throws ParseException the parse exception
     * @author：司徒彬 @date：2017/4/25 14:19
     */
    public static Timestamp addDay(int day) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, day);

        Timestamp currentTimestamp = new Timestamp(cal.getTimeInMillis());
        return currentTimestamp;//formatter.format(currentTime);
    }


    /**
     * Add day timestamp.
     *
     * @param date the date
     * @param day  the day
     * @return the timestamp
     * @throws ParseException the parse exception
     */
    public static Timestamp addDay(Timestamp date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.add(Calendar.DATE, day);
        //		int year = cal.get(Calendar.YEAR);
        //		int month = cal.get(Calendar.MONTH);
        //		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        //		String dateStr = year + "-" + (month + 1) + "-" + (dayOfMonth);
        Date result = cal.getTime();// DataSwitch.convertObjectToDate(dateStr);
        Timestamp currentTimestamp = new Timestamp(result.getTime());
        //Timestamp currentTimestamp = new Timestamp(cal.getTime().getTime());
        //		Date currentTime = cal.getTime();
        //		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return currentTimestamp;//formatter.format(currentTime);
    }

    /**
     * Add year timestamp.
     *
     * @param date the date
     * @param year the year
     * @return the timestamp
     * @throws ParseException the parse exception
     */
    public static Timestamp addYear(Timestamp date, int year) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.add(Calendar.YEAR, year);
        //		int year = cal.get(Calendar.YEAR);
        //		int month = cal.get(Calendar.MONTH);
        //		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        //		String dateStr = year + "-" + (month + 1) + "-" + (dayOfMonth);
        Date result = cal.getTime();// DataSwitch.convertObjectToDate(dateStr);
        Timestamp currentTimestamp = new Timestamp(result.getTime());
        //Timestamp currentTimestamp = new Timestamp(cal.getTime().getTime());
        //		Date currentTime = cal.getTime();
        //		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return currentTimestamp;//formatter.format(currentTime);
    }

    public static Timestamp addSeconds(Timestamp date, int second) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.add(Calendar.SECOND, second);
        //		int year = cal.get(Calendar.YEAR);
        //		int month = cal.get(Calendar.MONTH);
        //		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        //		String dateStr = year + "-" + (month + 1) + "-" + (dayOfMonth);
        Date result = cal.getTime();// DataSwitch.convertObjectToDate(dateStr);
        Timestamp currentTimestamp = new Timestamp(result.getTime());
        //Timestamp currentTimestamp = new Timestamp(cal.getTime().getTime());
        //		Date currentTime = cal.getTime();
        //		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return currentTimestamp;//formatter.format(currentTime);
    }


    public static String getSpendTime(Long start, Long end) {
        long spendTime = Math.abs(end - start);

        long second = spendTime / 1000;

        long minute = second / 60;
        second = second % 60;

        long hour = minute / 60;
        minute = minute % 60;

        StringBuilder result = new StringBuilder();
        if (hour != 0) {
            result = result.append(hour).append(" 小时 ");
        }
        if (minute != 0) {
            result = result.append(minute).append(" 分钟 ");
        }

        result = result.append(second).append(" 秒 ");

        return result.toString();
    }

    /**
     * Format spend time string.
     *
     * @param start the start
     * @param end   the end
     * @return the string
     */
    public static String getSpendTime(Timestamp start, Timestamp end) {
        long startTime = start.getTime();
        long endTime = end.getTime();
        return getSpendTime(startTime, endTime);

    }

    /**
     * Is first day of week boolean.
     *
     * @param now the now
     * @return the boolean
     */
    public static boolean isFirstDayOfWeek(Timestamp now) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        return ObjectUtils.equals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK));
    }


    /**
     * Gets first day of week.
     *
     * @param now  the now
     * @param week the week
     * @return the first day of week
     */
    public static Timestamp getFirstDayOfWeek(Timestamp now, int week) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.add(Calendar.WEEK_OF_MONTH, week);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp currentTimestamp = new Timestamp(cal.getTimeInMillis());
        return currentTimestamp;
    }


    public static Timestamp getLastDayOfWeek(Timestamp now, int week) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.add(Calendar.WEEK_OF_MONTH, week + 1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Timestamp currentTimestamp = new Timestamp(cal.getTimeInMillis());
        return currentTimestamp;
    }

    public static boolean isFirstDayOfMonth(Timestamp now) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        return ObjectUtils.equals(1, cal.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * Gets first day of month.
     *
     * @param now   the now
     * @param month the month
     * @return the first day of month
     */
    public static Timestamp getFirstDayOfMonth(Timestamp now, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.add(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp currentTimestamp = new Timestamp(cal.getTimeInMillis());
        return currentTimestamp;
    }

    public static Timestamp getLastDayOfMonth(Timestamp now, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.add(Calendar.MONTH, month + 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Timestamp currentTimestamp = new Timestamp(cal.getTimeInMillis());
        return currentTimestamp;
    }

}
