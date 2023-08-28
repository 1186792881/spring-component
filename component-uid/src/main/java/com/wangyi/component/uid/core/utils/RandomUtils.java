package com.wangyi.component.uid.core.utils;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomUtils {
    public final static char[] CHARS = new char[52];
    public final static int[] NUMS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    public static long time = 1558779301082L;

    static {
        for (char i = 'a'; i <= 'z'; i++) {
            CHARS[i - 97] = i;
        }

        for (char i = 'A'; i <= 'Z'; i++) {
            CHARS[i - 39] = i;
        }
    }

    /**
     * 生成某个区间的 随机double
     * digits 保留小数位数
     */
    public static Double randomDouble(int min, int max, int digits) {
        if (digits < 1 || min > max) {
            return null;
        }
        Random ran = new Random();
        double decimal = ran.nextDouble(); //小数部分
        int integer = randomInt( min, max ); //整数部分
        double num = integer + decimal;
        String numFormat = "0."; //格式化小数，保留固定位数
        for (int i = 0; i < digits; i++) {
            numFormat += "0";
        }
        return Double.parseDouble( new DecimalFormat( numFormat ).format( num ) );
    }

    /**
     * 生成某个区间的 随机Float
     * digits 保留小数位数
     */
    public static Float randomFloat(int min, int max, int digits) {
        if (digits < 1 || min > max) {
            return null;
        }
        Random ran = new Random();
        float decimal = ran.nextFloat(); //小数部分
        int integer = randomInt( min, max ); //整数部分
        float num = integer + decimal;
        String numFormat = "0."; //格式化小数，保留固定位数
        for (int i = 0; i < digits; i++) {
            numFormat += "0";
        }
        return Float.parseFloat( new DecimalFormat( numFormat ).format( num ) );
    }

    /**
     * 生成某个区间的随机整数
     */
    public static Integer randomInt(int min, int max) {
        if (min > max) {
            return min;
        }
        return new Random().nextInt( max - min ) + min;
    }

    /**
     * 在给定的数组中，随机取一个元素
     */
    public static <T> T randomElement(T[] objs) {
        if (objs == null || objs.length == 0) {
            return null;
        }
        Random ran = new Random();
        int ranIndex = ran.nextInt( objs.length );
        return objs[ranIndex];
    }

    /**
     * 生成随机字符串
     * <p>
     * useChar:  true  使用字母abcd等生成字符串   false 使用数字123等生成字符串
     */
    public static String randomStr(int length, boolean useChar) {
        if (length < 1) {
            return null;
        }
        Random ran = new Random();
        StringBuilder bulider = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (useChar) {
                int ranIndex = ran.nextInt( CHARS.length );
                bulider.append( CHARS[ranIndex] );
            } else {
                int ranIndex = ran.nextInt( NUMS.length );
                if (i == 0 && ranIndex == 0) { //首位不允许0
                    ranIndex = 1;
                }
                bulider.append( NUMS[ranIndex] );
            }
        }
        return bulider.toString();
    }

    /**
     * 生成随机汉字
     */
    public static String randomChinese(int length) {
        if (length < 1) {
            return null;
        }
        StringBuilder bulider = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int hightPos = (176 + Math.abs( random.nextInt( 39 ) ));//获取高位值
            int lowPos = (161 + Math.abs( random.nextInt( 93 ) ));//获取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer( hightPos ).byteValue());
            b[1] = (new Integer( lowPos ).byteValue());
            String s = new String( b, Charset.forName( "GBK" ) );
            bulider.append( s );
        }

        return bulider.toString();
    }

    /**
     * 生成随机IP
     */
    public static String randomIp() {
        String ip = "";
        for (int i = 0; i < 4; i++) {
            int num = randomInt( 1, 255 );
            num = num < 10 ? num * 10 : num;
            ip += num;
            if (i != 3) {
                ip += ".";
            }
        }
        return ip;
    }

    //物理地址  separator  分隔符   一般使用 "-" 或 ":"
    public static String randomMac() {
        String mac = "";
        for (int i = 0; i < 6; i++) {
            mac += randomStr( 1, true ) + randomStr( 1, false );
            if (i != 5) {
                mac += "-";
            }
        }
        return mac.toLowerCase();
    }

    public static String getTimeNow() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        return simpleDateFormat.format( now );
    }

    public static String getTimeRolling() {
        long step = randomInt( 1000, 119000 );
        time = time + step;
        Date date = new Date( time );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        return simpleDateFormat.format( date );
    }

    public static String randomStr(int count, String str) {
        if (str != null) {
            if (!StringUtils.isBlank( str )) {
                if (count <= 0) {
                    return "";
                }
                char[] charArray = str.toCharArray();
                int index;
                char[] newArray = new char[count];
                for (int i = 0; i < count; i++) {
                    index = randomInt( 0, charArray.length );
                    newArray[i] = str.charAt( index );
                }
                return new String( newArray );
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println( RandomUtils.randomStr( 40, "casffsgdfgjguktujrsvsvsbgnhgcvbxvsdfsgerdgdhd" ) );
    }
}
