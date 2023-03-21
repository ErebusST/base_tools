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
import java.util.regex.Pattern;

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
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static String getDateString(Object date) {
        return getDateString(date, DateFormatEnum.YYYY_MM_DD_HH_MM_SS);
    }


    /**
     * Gets date string.
     *
     * @param date           the date
     * @param dateFormatEnum the date format enum
     * @return the date string
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static String getDateString(Object date, DateFormatEnum dateFormatEnum) {
        dateFormatEnum = dateFormatEnum == null ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
        return getDateString(date, dateFormatEnum.getValue());
    }

    /**
     * Get date string string.
     *
     * @param date   the date
     * @param format the format
     * @return the date string
     * @author ErebusST
     * @since 2022 -04-07 19:35:31
     */
    public static String getDateString(Object date, String format) {
        if (ObjectUtils.isNull(date)) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }


    /**
     * 根据制指定的格式，获得当前时间的字符串
     *
     * @param dateFormatEnum the date format enum
     * @return the date string
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static String getDateString(DateFormatEnum dateFormatEnum) {
        return getDateString(getNow(), dateFormatEnum);
    }


    /**
     * 获取日期 天数差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return the days count
     * @author ErebusST
     * @return：相差天数
     * @since 2022 -01-07 15:35:59
     */
    public static int getDaysCount(Timestamp startDate, Timestamp endDate) {
        int dayInt = 0;
        long date1Time = startDate.getTime();
        long date2Time = endDate.getTime();
        if (date2Time > date1Time) {
            dayInt = (int) ((date2Time - date1Time) / 1000 / 60 / 60 / 24);
        }
        return Math.abs(dayInt);
    }

    /**
     * 获取两个时间的时间差 秒~
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the seconds count
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static int getSecondsCount(Timestamp startDate, Timestamp endDate) {
        long time = endDate.getTime() - startDate.getTime();
        int totalS = new Long(time / 1000).intValue();
        return Math.abs(totalS);
    }

    /**
     * 获取当前时间字符串(年月日时分秒)
     *
     * @return current year str
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static String getCurrentYearStr() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int year = cal.get(Calendar.YEAR);
        return year + "";
    }

    /**
     * Get year int.
     *
     * @param timestamp the timestamp
     * @return current year str
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static int getYear(Timestamp timestamp) {
        Calendar cal = toCalendar(timestamp);
        int year = cal.get(Calendar.YEAR);
        return year;
    }

    /**
     * Get month int.
     *
     * @param timestamp the timestamp
     * @return the month
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static int getMonth(Timestamp timestamp) {
        Calendar cal = toCalendar(timestamp);
        int month = cal.get(Calendar.MONTH);
        return month + 1;
    }


    /**
     * Get day of year int.
     *
     * @param timestamp the timestamp
     * @return the day of year
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static int getDayOfYear(Timestamp timestamp) {
        Calendar cal = toCalendar(timestamp);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        return day;
    }


    /**
     * Get day of week int.
     *
     * @param timestamp the timestamp
     * @return the int
     * @author ErebusST
     * @since 2022 -08-12 10:16:29
     */
    public static int getDayOfWeek(Timestamp timestamp) {
        Calendar cal = toCalendar(timestamp);

        boolean firstDayIsSunday = cal.getFirstDayOfWeek() == Calendar.SUNDAY;
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (firstDayIsSunday) {
            day = day - 1;
            if (day == 0) {
                day = 7;
            }
        }
        return day;
    }

    /**
     * Get day of month int.
     *
     * @param timestamp the timestamp
     * @return the int
     * @author ErebusST
     * @since 2022 -08-12 10:15:54
     */
    public static int getDayOfMonth(Timestamp timestamp) {
        return getValue(timestamp,Calendar.DAY_OF_MONTH);
    }

    /**
     * Get week of year int.
     *
     * @param timestamp the timestamp
     * @return the week of year
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static int getWeekOfYear(Timestamp timestamp) {
        return getValue(timestamp,Calendar.WEEK_OF_YEAR);
    }

    /**
     * Get week of month int.
     *
     * @param timestamp the timestamp
     * @return the week of month
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static int getWeekOfMonth(Timestamp timestamp) {
        return getValue(timestamp,Calendar.WEEK_OF_MONTH);
    }

    public static int getHour(Timestamp timestamp){
        return getValue(timestamp,Calendar.HOUR_OF_DAY);
    }

    public static int getValue(Timestamp timestamp,int field){
        Calendar cal = toCalendar(timestamp);
        int value = cal.get(field);
        return value;
    }


    /**
     * 获得当前时间
     *
     * @return the date
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * In time range boolean.
     *
     * @param start the start
     * @param end   the end
     * @return the boolean
     * @author ErebusST
     * @since 2022 -09-15 14:54:35
     */
    public static boolean inTimeRange(Timestamp start, Timestamp end) {
        Timestamp now = DateUtils.getNow();
        if (ObjectUtils.isNull(start) && ObjectUtils.isNull(end)) {
            return true;
        } else if (ObjectUtils.isNotNull(start) && ObjectUtils.isNull(end)) {
            return start.before(now);
        } else if (ObjectUtils.isNull(start) && ObjectUtils.isNotNull(end)) {
            return end.after(now);
        } else {
            return start.before(now) && end.after(now);
        }
    }


    /**
     * Gets timestamp.
     *
     * @param dateStr the date str
     * @return the timestamp
     * @throws ParseException the parse exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static Timestamp getTimestamp(Object dateStr) {
        return getTimestamp(DataSwitch.convertObjectToString(dateStr), 0);
    }




    private final static Pattern YYYY_MM_DD = Pattern.compile("^\\d{4}-[01][0-9]-[0-3][0-9]$");
    private final static Pattern YYYY_MM_DD_HH = Pattern.compile("^\\d{4}-[01][0-9]-[0-3][0-9]\\s[0-5][0-9]$");
    private final static Pattern YYYY_MM_DD_HH_MM = Pattern.compile("^\\d{4}-[01][0-9]-[0-3][0-9]\\s[0-5][0-9]:[0-5][0-9]$");

    /**
     * Gets timestamp.
     *
     * @param dateStr the date str
     * @param day     the day
     * @return the timestamp
     * @throws ParseException the parse exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static Timestamp getTimestamp(String dateStr, int day) {
        try {
            if (StringUtils.match(dateStr, YYYY_MM_DD)) {
                dateStr = dateStr + " 00:00:00";
            } else if (StringUtils.match(dateStr, YYYY_MM_DD_HH)) {
                dateStr = dateStr + ":00:00";
            } else if (StringUtils.match(dateStr, YYYY_MM_DD_HH_MM)) {
                dateStr = dateStr + ":00";
            }
            Timestamp result = Timestamp.valueOf(dateStr);
            result = addDay(result, day);
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Timestamp getTimestamp(String dateStr, DateFormatEnum dateFormatEnum) {
        try {
            dateFormatEnum = dateFormatEnum == null ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
            Timestamp result = new Timestamp(getDate(dateStr, dateFormatEnum).getTime());
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    private static Date getDate(String dateStr, DateFormatEnum dateFormatEnum) throws ParseException {
        dateFormatEnum = dateFormatEnum == null ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatEnum.getValue());
        return simpleDateFormat.parse(dateStr);
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @return second timestamp
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static int getSecondTimestamp() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0, length - 3));
        } else {
            return 0;
        }
    }


    /**
     * Add seconds timestamp.
     *
     * @param second the second
     * @return the timestamp
     * @author ErebusST
     * @since 2022 -04-17 09:57:07
     */
    public static Timestamp addSeconds(int second) {
        return addSeconds(DateUtils.getNow(), second);
    }

    /**
     * Add seconds timestamp.
     *
     * @param date   the date
     * @param second the second
     * @return the timestamp
     * @throws ParseException the parse exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static Timestamp addSeconds(Timestamp date, int second) {
        return add(date, Calendar.SECOND, second);
    }

    /**
     * Add hours timestamp.
     *
     * @param hour the hour
     * @return the timestamp
     * @author ErebusST
     * @since 2022 -08-30 10:31:00
     */
    public static Timestamp addHours(int hour) {
        return addHours(DateUtils.getNow(), hour);
    }

    /**
     * Add hours timestamp.
     *
     * @param date the date
     * @param hour the hour
     * @return the timestamp
     * @author ErebusST
     * @since 2022 -08-30 10:31:02
     */
    public static Timestamp addHours(Timestamp date, int hour) {
        return add(date, Calendar.HOUR_OF_DAY, hour);
    }


    /**
     * 获取数据查询日期，格式是：2014-04-02，day是天差，如果传-1是昨天的日期
     *
     * @param day the day
     * @return the timestamp
     * @throws ParseException the parse exception
     * @author ErebusST
     * @author：司徒彬 @date：2017/4/25 14:19
     * @since 2022 -01-07 15:35:59
     */
    public static Timestamp addDay(int day) {
        return addDay(DateUtils.getNow(), day);
    }


    /**
     * Add day timestamp.
     *
     * @param date the date
     * @param day  the day
     * @return the timestamp
     * @throws ParseException the parse exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static Timestamp addDay(Timestamp date, int day) {
        return add(date, Calendar.DATE, day);
    }

    /**
     * Add month timestamp.
     *
     * @param month the month
     * @return the timestamp
     * @author ErebusST
     * @since 2022 -04-17 09:58:46
     */
    public static Timestamp addMonth(int month) {
        return addMonth(DateUtils.getNow(), month);
    }

    /**
     * Add month timestamp.
     *
     * @param date  the date
     * @param month the month
     * @return the timestamp
     * @author ErebusST
     * @since 2022 -04-17 09:58:13
     */
    public static Timestamp addMonth(Timestamp date, int month) {
        return add(date, Calendar.MONTH, month);
    }

    /**
     * Add year timestamp.
     *
     * @param year the year
     * @return the timestamp
     * @author ErebusST
     * @since 2022 -04-17 09:57:05
     */
    public static Timestamp addYear(int year) {
        return addYear(DateUtils.getNow(), year);
    }

    /**
     * Add year timestamp.
     *
     * @param date the date
     * @param year the year
     * @return the timestamp
     * @throws ParseException the parse exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static Timestamp addYear(Timestamp date, int year) {
        return add(date, Calendar.YEAR, year);
    }


    public static Timestamp add(Timestamp date, int filed, int value) {
        Calendar cal = toCalendar(date);
        cal.add(filed, value);
        return Timestamp.from(cal.toInstant());
    }


    /**
     * Get spend time string.
     *
     * @param start the start
     * @param end   the end
     * @return the spend time
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static String getSpendTime(Long start, Long end) {
        long spendTime = Math.abs(end - start);
        long millis = spendTime % 1000;
        long second = spendTime / 1000;

        long minute = second / 60;
        second = second % 60;

        long hour = minute / 60;
        minute = minute % 60;

        StringBuilder result = new StringBuilder();
        if (hour != 0) {
            result = result.append(hour).append(" h ");
        }
        if (minute != 0) {
            result = result.append(minute).append(" m ");
        }

        result = result.append(second).append(" s ");
        result = result.append(millis).append(" ms ");


        return result.toString();
    }

    /**
     * Format spend time string.
     *
     * @param start the start
     * @param end   the end
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
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
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static boolean isFirstDayOfWeek(Timestamp now) {
        Calendar cal = toCalendar(now);
        return ObjectUtils.equals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK));
    }


    /**
     * Gets first day of week.
     *
     * @param now  the now
     * @param week the week
     * @return the first day of week
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static Timestamp getFirstDayOfWeek(Timestamp now, int week) {
        Calendar cal = toCalendar(now);
        cal.add(Calendar.WEEK_OF_MONTH, week);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp currentTimestamp = new Timestamp(cal.getTimeInMillis());
        return currentTimestamp;
    }


    /**
     * Get last day of week timestamp.
     *
     * @param now  the now
     * @param week the week
     * @return the last day of week
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static Timestamp getLastDayOfWeek(Timestamp now, int week) {
        Calendar cal = toCalendar(now);
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

    /**
     * Is first day of month boolean.
     *
     * @param now the now
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static boolean isFirstDayOfMonth(Timestamp now) {
        Calendar cal = toCalendar(now);
        return ObjectUtils.equals(1, cal.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * Gets first day of month.
     *
     * @param now   the now
     * @param month the month
     * @return the first day of month
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static Timestamp getFirstDayOfMonth(Timestamp now, int month) {
        Calendar cal = toCalendar(now);
        cal.add(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp currentTimestamp = new Timestamp(cal.getTimeInMillis());
        return currentTimestamp;
    }

    /**
     * Get last day of month timestamp.
     *
     * @param now   the now
     * @param month the month
     * @return the last day of month
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static Timestamp getLastDayOfMonth(Timestamp now, int month) {
        Calendar cal = toCalendar(now);
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


    /**
     * To calendar calendar.
     *
     * @param now the now
     * @return the calendar
     * @author ErebusST
     * @since 2022 -09-23 11:25:11
     */
    public static Calendar toCalendar(Timestamp now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now.getTime());
        return calendar;
    }

}
