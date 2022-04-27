/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ;

import com.situ.tools.StringUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 司徒彬
 * @date 2022/4/27 23:57
 */
public class PinyinUtils {

    @Test
    public void test() {
        String city =
                spell("北京市");
        System.out.println(city);
    }

    /**
     * First spell string.
     *
     * @param chinese the chinese
     * @return the string
     * @author ErebusST
     * @since 2022 -04-28 00:15:02
     */
    public static String firstSpell(String chinese) {
        return firstSpell(chinese, "");
    }

    /**
     * First spell string.
     *
     * @param chinese   the chinese
     * @param delimiter the delimiter
     * @return the string
     * @author ErebusST
     * @since 2022 -04-28 00:14:57
     */
    public static String firstSpell(String chinese, String delimiter) {
        char[] arr = chinese.toCharArray();
        List<String> list = new ArrayList<>(arr.length);
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] t = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (t != null) {
                        list.add(String.valueOf(t[0].charAt(0)));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                list.add(String.valueOf(arr[i]));
            }
        }
        return StringUtils.getCombineString(delimiter, list).replaceAll("\\W", "").trim().toLowerCase();
    }

    /**
     * Spell string.
     *
     * @param chinese the chinese
     * @return the string
     * @author ErebusST
     * @since 2022 -04-28 00:17:21
     */
    public static String spell(String chinese){
        return spell(chinese,"");
    }

    /**
     * Spell string.
     *
     * @param chinese   the chinese
     * @param delimiter the delimiter
     * @return the string
     * @author ErebusST
     * @since 2022 -04-28 00:17:23
     */
    public static String spell(String chinese, String delimiter) {
        char[] arr = chinese.toCharArray();
        List<String> list = new ArrayList<>(arr.length);
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    list.add(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                list.add(String.valueOf(arr[i]));
            }
        }
        return StringUtils.getCombineString(delimiter, list).replaceAll("\\W", "").trim().toLowerCase();
    }

}
