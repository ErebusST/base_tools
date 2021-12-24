/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * StringUtils重载方法
 *
 * @author 司徒彬
 * @date 2017-03-15 10:56
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * Is empty boolean.
     *
     * @param cs the cs
     * @return the boolean
     */
    public static boolean isEmpty(CharSequence cs) {
        return !isNotEmpty(cs);
    }

    /**
     * Is not empty boolean.
     *
     * @param cs the cs
     * @return the boolean
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return isNotEmpty((Object) cs);
    }

    /**
     * Is empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isEmpty(Object str) {
        return !isNotEmpty(str);
    }

    /**
     * Is not empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isNotEmpty(Object str) {
        return str != null && str.toString().trim().length() != 0;
    }

    /**
     * To string string.
     *
     * @param str the str
     * @return the string
     */
    public static String toString(Object str) {
        return isEmpty(str) ? "" : str.toString();
    }

    /**
     * Create random str string.
     *
     * @param length the length
     * @return the string
     */
    public static String createRandomStr(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    /**
     * Ends with boolean.
     *
     * @param source the source
     * @param target the target
     * @return the boolean
     */
    public static boolean endsWith(String source, String target) {
        return endsWith(source, target, true);
    }

    /**
     * Ends with boolean.
     *
     * @param source     the source
     * @param target     the target
     * @param ignoreCase the ignore case
     * @return the boolean
     */
    public static boolean endsWith(String source, String target, boolean ignoreCase) {
        if (ObjectUtils.isNull(source) || ObjectUtils.isNull(target)) {
            return false;
        }

        ignoreCase = ObjectUtils.isNull(ignoreCase) || ignoreCase;
        if (ignoreCase) {
            source = source.toLowerCase();
            target = target.toLowerCase();
        }

        return source.endsWith(target);
    }

    /**
     * Split to list list.
     * <p>
     * 会去除空字符串
     *
     * @param sourceStr the source str
     * @param splitChar the split char
     * @return the list
     */
    public static List<String> splitToList(Object sourceStr, String splitChar) {
        return splitToList(sourceStr, splitChar, true);
    }

    /**
     * Split to list list.
     *
     * @param sourceStr   the source str
     * @param splitChar   the split char
     * @param filterEmpty the filter empty
     * @return the list
     */
    public static List<String> splitToList(Object sourceStr, String splitChar, boolean filterEmpty) {
        if (sourceStr != null) {
            String[] arr =org.apache.commons.lang3.StringUtils
                    .splitByWholeSeparatorPreserveAllTokens(DataSwitch.convertObjectToString(sourceStr),splitChar);
            return Arrays.stream(arr)
                    .map(String::trim)
                    .filter(str -> {
                        if (filterEmpty && str.trim().length() == 0) {
                            return false;
                        } else {
                            return true;
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 验证是否浮点数
     *
     * @param value the value
     * @return the boolean
     */
    public static boolean isFloat(Object value) {
        return isFloat(value, false);
    }

    /**
     * 验证是否浮点数
     *
     * @param value
     * @param allowBlank
     * @return
     */
    public static boolean isFloat(Object value, boolean allowBlank) {
        if (ObjectUtils.isEmpty(value)) {
            if (allowBlank) {
                return true;
            } else {
                return false;
            }
        } else {
            String str = toString(value);
            return match(str, isFloat);
        }
    }

    /**
     * 验证是否证书
     *
     * @param value the value
     * @return the boolean
     */
    public static boolean isInteger(Object value) {
        String str = toString(value);
        return match(str, isInteger);
    }

    public static boolean isCellPhone(Object value) {
        String str = toString(value);
        return match(str, isCellPhone);
    }

    public static boolean isIdentityCard(Object value) {
        String str = toString(value);
        return match(str, isIdentityCard);
    }

    /**
     * Is date boolean.
     *
     * @param value the value
     * @return the boolean
     */
    public static boolean isDate(Object value) {
        String str = toString(value);
        if (match(str, isDate1)) {
            return true;
        } else if (match(str, isDate2)) {
            return true;
        } else if (match(str, isDate3)) {
            return true;
        } else if (match(str, isDate4)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets first not empty string.
     *
     * @param strings the strings
     * @return the first not empty string
     */
    public static String getFirstNotEmptyString(String... strings) {
        try {
            for (String string : strings) {
                if (isNotEmpty(string)) {
                    return string;
                }
            }
            return "";
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static Pattern isInteger;
    private static Pattern isFloat;
    private static Pattern isCellPhone;
    private static Pattern isIdentityCard;
    private static Pattern isDate1;
    private static Pattern isDate2;
    private static Pattern isDate3;
    private static Pattern isDate4;

    static {
        isInteger = getIntegerPattern();
        isFloat = getFloatPattern();
        isCellPhone = getCellPhonePattern();
        isIdentityCard = getIdentityCardPatter();
        isDate1 = getDatePattern1();
        isDate2 = getDatePattern2();
        isDate3 = getDatePattern3();
        isDate4 = getDatePattern4();
    }

    private static Pattern getIdentityCardPatter() {
        String regex = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$";
        Pattern pattern = Pattern.compile(regex);
        return pattern;
    }

    private static Pattern getIntegerPattern() {
        String regex = "^-?[1-9]\\d*$";
        Pattern pattern = Pattern.compile(regex);
        return pattern;
    }

    private static Pattern getFloatPattern() {
        String regex = "^[1-9]\\d*\\.{1}\\d+$|^0\\.{1}\\d+$|^[1-9]\\d*$|^0$";
        return Pattern.compile(regex);
    }

    private static Pattern getCellPhonePattern() {
        String regex = "^1\\d{10}$";
        return Pattern.compile(regex);
    }

    private final static String YYYY_DD_MM = "^\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}$";//2017-02-02
    private final static String YYYY_DD_MM_HH = "^\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}\\s+\\d{1,2}$";//2017-02-02 12
    private final static String YYYY_DD_MM_HH_MM = "^\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}\\s+\\d{1,2}:\\d{1,2}$";//2017-02-02 12:13
    private final static String YYYY_DD_MM_HH_MM_SS = "^\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}$";//2017-02-02 12:13:14

    private static Pattern getDatePattern1() {
        return Pattern.compile(YYYY_DD_MM);
    }

    private static Pattern getDatePattern2() {
        return Pattern.compile(YYYY_DD_MM_HH_MM_SS);
    }

    private static Pattern getDatePattern3() {
        return Pattern.compile(YYYY_DD_MM_HH);
    }

    private static Pattern getDatePattern4() {
        return Pattern.compile(YYYY_DD_MM_HH_MM);
    }


    public static boolean match(String str, String regex) {
        Pattern compile = Pattern.compile(regex);
        return match(str, compile);
    }

    /***
     * @param str 源字符串
     * @param pattern 正则表达式
     * @return 是否匹配 boolean
     */
    public static boolean match(String str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }


    //region 合并字符串

    /**
     * 传入多个字符串，取得合并并且用半角逗号分隔的字符串 <p> 如：getCombineString(str1,str2,str3) 返回值 str1,str2,str3 <p> 如果 str1、str2、str3中有空字符串则去掉 如：str2为空，则 返回str1,str2
     *
     * @param strings the strings
     * @return the string
     */
    public static String getCombineString(List<String> strings) {
        try {
            return getCombineString(strings.stream());
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets combine string.
     *
     * @param delimiter the delimiter
     * @param strings   the strings
     * @return the combine string
     */
    public static String getCombineString(String delimiter, List<String> strings) {
        try {
            return getCombineString(delimiter, strings.stream());
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets combine string.
     *
     * @param strings the strings
     * @return the combine string
     */
    public static String getCombineString(Stream<String> strings) {
        try {
            return getCombineString(",", strings);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets combine string.
     *
     * @param delimiter the delimiter
     * @param strings   the strings
     * @return the combine string
     */
    public static String getCombineString(String delimiter, Stream<String> strings) {
        try {
            return strings.filter(StringUtils::isNotEmpty).collect(Collectors.joining(delimiter));
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 传入多个字符串，取得合并并且用半角逗号分隔的字符串 <p> 如：getCombineString(str1,str2,str3) 返回值 str1,str2,str3 <p> 如果 str1、str2、str3中有空字符串则去掉 如：str2为空，则 返回str1,str2
     *
     * @param strings the strings
     * @return the string
     */
//    public static String getCombineString(String... strings)
//    {
//        try
//        {
//            return getCombineString(Arrays.asList(strings));
//        }
//        catch (Exception ex)
//        {
//            throw ex;
//        }
//    }

    /**
     * Gets combine string.
     *
     * @param delimiter the delimiter
     * @param strings   the strings
     * @return the combine string
     */
    public static String getCombineString(String delimiter, String... strings) {
        try {
            if (ObjectUtils.isNull(strings)) {
                return "";
            }
            return getCombineString(delimiter,
                    Arrays.asList(strings));
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static boolean equalsIgnoreCase(Object obj1, Object obj2) {
        return equals(obj1, obj2, true);
    }

    public static boolean equals(Object obj1, Object obj2, boolean ignoreCase) {
        if (ignoreCase) {
            return DataSwitch.convertObjectToString(obj1).trim()
                    .equalsIgnoreCase(DataSwitch.convertObjectToString(obj2).trim());
        } else {
            return DataSwitch.convertObjectToString(obj1).trim()
                    .equals(DataSwitch.convertObjectToString(obj2).trim());
        }

    }

    public static String concat(Object... objects) {
        return Arrays.stream(objects).map(DataSwitch::convertObjectToString).collect(Collectors.joining());
    }


    /**
     * Format string.
     *
     * @param string  the string
     * @param objects the objects
     * @return the string
     */
    public static String format(String string, Object... objects) {
        for (Object object : objects) {
            string = StringUtils.replace(string, "{}", DataSwitch.convertObjectToString(object), 1);
        }
        return string;
    }


    //endregion
}
