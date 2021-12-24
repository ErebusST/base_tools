/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.enumeration;

/**
 * DateFormatEnum
 *
 * @author ：司徒彬 @date：2016/10/18 13:05
 */
public enum DateFormatEnum {
    YYYY_MM_DD("yyyy-MM-dd"), YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"), YYYYMMDDHHMMSS("yyyy_MM_dd_HH_mm_ss"), YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"), YYYYMMDDHHMMSSsss("yyyyMMddHHmmssS"),
    YYYYMMDD("yyyyMMdd"), YYYYNMMYDDR("yyyy年MM月dd日");

    //    YYYY_MM_DD {public String getValue(){return "yyyy-MM-dd HH:mm:ss";}},
    //    YYYY_MM_DD_HH_MM_SS {public String getValue(){return "yyyy-MM-dd HH:mm:ss";}},
    //    YYYY_MM_DD_HH_MM {public String getValue(){return "yyyy-MM-dd HH:mm";}};

    private String value;

    DateFormatEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
