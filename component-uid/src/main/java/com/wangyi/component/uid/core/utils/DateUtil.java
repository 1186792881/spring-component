package com.wangyi.component.uid.core.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * 日期格式
     **/
    public interface DATE_PATTERN {
        String YYYY = "yyyy";
        String YYYYMM = "yyyyMM";
        String MMDD = "MMdd";
        String MM = "MM";
        String HHMMSS = "HHmmss";
        String HH_MM_SS = "HH:mm:ss";
        String YYYYMMDD = "yyyyMMdd";
        String YYYY_MM_DD = "yyyy-MM-dd";
        String YYYY_MM_01 = "yyyy-MM-01";
        String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
        String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
        String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    }

    public static final String getDate() {
        return date2String( new Date() );
    }

    public static final String getBizMonth() {
        return date2String( new Date(), DATE_PATTERN.YYYY_MM_01 );
    }

    public static final String timestamp2String(Timestamp timestamp, String pattern) {
        if (timestamp == null) {
            throw new IllegalArgumentException( "timestamp null illegal" );
        }
        if (pattern == null || pattern.equals( "" )) {
            pattern = DATE_PATTERN.YYYY_MM_DD_HH_MM_SS;
        }
        SimpleDateFormat sdf = new SimpleDateFormat( pattern );
        return sdf.format( new Date( timestamp.getTime() ) );
    }

    public static final String date2String(Object date) {
        return date2String( date, DATE_PATTERN.YYYY_MM_DD );
    }

    public static final String date2String(Object date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException( "timestamp null illegal" );
        }
        if (pattern == null || pattern.equals( "" )) {
            return date2String( date );
        }
        return new SimpleDateFormat( pattern ).format( date );
    }

    public static final Timestamp currentTimestamp() {
        return new Timestamp( new Date().getTime() );
    }

    public static final String currentTimestamp2String(String pattern) {
        return timestamp2String( currentTimestamp(), pattern );
    }

    public static final Timestamp string2Timestamp(String strDateTime, String pattern) {
        if (strDateTime == null || strDateTime.equals( "" )) {
            throw new IllegalArgumentException( "Date Time Null Illegal" );
        }
        if (pattern == null || pattern.equals( "" )) {
            pattern = DATE_PATTERN.YYYY_MM_DD_HH_MM_SS;
        }
        SimpleDateFormat sdf = new SimpleDateFormat( pattern );
        Date date = null;
        try {
            date = sdf.parse( strDateTime );
        } catch (ParseException e) {
            throw new RuntimeException( e );
        }
        return new Timestamp( date.getTime() );
    }

    public static final Timestamp string2Timestamp(String strDateTime) {
        return string2Timestamp( strDateTime, null );
    }

    public static final Date string2Date(String strDate) {
        return string2Date( strDate, DATE_PATTERN.YYYY_MM_DD );
    }

    public static final Date string2Date(String strDate, String pattern) {
        if (strDate == null || strDate.equals( "" )) {
            throw new RuntimeException( "str date null" );
        }
        if (pattern == null || pattern.equals( "" )) {
            return string2Date( strDate );
        }
        SimpleDateFormat sdf = new SimpleDateFormat( pattern );
        Date date = null;
        try {
            date = sdf.parse( strDate );
        } catch (ParseException e) {
            throw new RuntimeException( e );
        }
        return date;
    }

    public static final String string2Year(String strDest) {
        if (strDest == null || strDest.equals( "" )) {
            throw new IllegalArgumentException( "str dest null" );
        }
        Date date = string2Date( strDest, DATE_PATTERN.YYYY_MM_DD );
        Calendar c = Calendar.getInstance();
        c.setTime( date );
        return String.valueOf( c.get( Calendar.YEAR ) );
    }

    public static final String string2Month(String strDest) {
        if (strDest == null || strDest.equals( "" )) {
            throw new IllegalArgumentException( "str dest null" );
        }
        Date date = string2Date( strDest, DATE_PATTERN.YYYY_MM_DD );
        Calendar c = Calendar.getInstance();
        c.setTime( date );
        int month = c.get( Calendar.MONTH );
        month = month + 1;
        if (month < 10) {
            return "0" + month;
        }
        return String.valueOf( month );
    }

    public static final String string2Day(String strDest) {
        if (strDest == null || strDest.equals( "" )) {
            throw new IllegalArgumentException( "str dest null" );
        }
        Date date = string2Date( strDest, DATE_PATTERN.YYYY_MM_DD );
        Calendar c = Calendar.getInstance();
        c.setTime( date );
        int day = c.get( Calendar.DAY_OF_MONTH );
        if (day < 10) {
            return "0" + day;
        }
        return "" + day;
    }

    public static final Date getFirstDayOfMonth(Calendar c) {
        int year = c.get( Calendar.YEAR );
        int month = c.get( Calendar.MONTH );
        int day = 1;
        c.set( year, month, day, 0, 0, 0 );
        return c.getTime();
    }

    public static final Date getLastDayOfMonth(Calendar c) {
        int year = c.get( Calendar.YEAR );
        int month = c.get( Calendar.MONTH ) + 1;
        int day = 1;
        if (month > 11) {
            month = 0;
            year = year + 1;
        }
        c.set( year, month, day - 1, 0, 0, 0 );
        return c.getTime();
    }

    public static final boolean compareDate(Date firstDate, Date secondDate) {
        if (firstDate == null || secondDate == null) {
            throw new RuntimeException();
        }
        String strFirstDate = date2String( firstDate, "yyyy-MM-dd" );
        String strSecondDate = date2String( secondDate, "yyyy-MM-dd" );
        return strFirstDate.equals( strSecondDate );
    }

    public static final Date getStartTimeOfDate(Date currentDate) {
        String strDateTime = date2String( currentDate, "yyyy-MM-dd" ) + " 00:00:00";
        return string2Date( strDateTime, "yyyy-MM-dd hh:mm:ss" );
    }

    public static final Date getEndTimeOfDate(Date currentDate) {
        String strDateTime = date2String( currentDate, "yyyy-MM-dd" ) + " 59:59:59";
        return string2Date( strDateTime, "yyyy-MM-dd hh:mm:ss" );
    }

    /**
     * 日期计算
     *
     * @param date
     * @param field
     * @param amount
     * @return
     */
    public static final Date addDate(Date date, int field, int amount) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        calendar.add( field, amount );
        return calendar.getTime();
    }

    /**
     * 间隔天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getDayBetween(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime( startDate );
        start.set( Calendar.HOUR_OF_DAY, 0 );
        start.set( Calendar.MINUTE, 0 );
        start.set( Calendar.SECOND, 0 );
        start.set( Calendar.MILLISECOND, 0 );
        Calendar end = Calendar.getInstance();
        end.setTime( endDate );
        end.set( Calendar.HOUR_OF_DAY, 0 );
        end.set( Calendar.MINUTE, 0 );
        end.set( Calendar.SECOND, 0 );
        end.set( Calendar.MILLISECOND, 0 );
        long n = end.getTimeInMillis() - start.getTimeInMillis();
        return (int) (n / (60 * 60 * 24 * 1000l));
    }

    /**
     * 间隔月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getMonthBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before( endDate )) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime( startDate );
        Calendar end = Calendar.getInstance();
        end.setTime( endDate );
        int year1 = start.get( Calendar.YEAR );
        int year2 = end.get( Calendar.YEAR );
        int month1 = start.get( Calendar.MONTH );
        int month2 = end.get( Calendar.MONTH );
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        return n;
    }

    /**
     * 间隔月，多一天就多算一个月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getMonthBetweenWithDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before( endDate )) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime( startDate );
        Calendar end = Calendar.getInstance();
        end.setTime( endDate );
        int year1 = start.get( Calendar.YEAR );
        int year2 = end.get( Calendar.YEAR );
        int month1 = start.get( Calendar.MONTH );
        int month2 = end.get( Calendar.MONTH );
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        int day1 = start.get( Calendar.DAY_OF_MONTH );
        int day2 = end.get( Calendar.DAY_OF_MONTH );
        if (day1 <= day2) {
            n++;
        }
        return n;
    }

    /**
     * 将长整型数字转换为日期格式的字符串
     *
     * @param time
     * @param format
     * @return
     */
    public static String long2String(long time, String format) {
        if (time > 0l) {
            if (StringUtils.isEmpty( format )) {
                format = DATE_PATTERN.YYYY_MM_DD_HH_MM_SS;
            }
            SimpleDateFormat sf = new SimpleDateFormat( format );
            Date date = new Date( time );
            return sf.format( date );
        }
        return "";
    }

    /**
     * 将日期格式的字符串转换为长整型
     *
     * @param date
     * @param format
     * @return
     */
    public static long string2long(String date, String format) {
        try {
            if (!StringUtils.isEmpty( date )) {
                if (StringUtils.isEmpty( format )) {
                    format = DATE_PATTERN.YYYY_MM_DD_HH_MM_SS;
                }
                SimpleDateFormat sf = new SimpleDateFormat( format );
                return sf.parse( date ).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static void main(String[] args) {
        System.out.println( date2String( new Date(), "yyyy" ) );
        Date bizMonthL = DateUtil.addDate( new Date(), 2, -12 );
        System.out.println( date2String( bizMonthL, "yyyy-MM-dd" ) );
    }
}
