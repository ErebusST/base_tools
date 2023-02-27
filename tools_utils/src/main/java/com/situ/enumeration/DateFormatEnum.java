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
    /**
     * Yyyy mm dd date format enum.
     */
    YYYY_MM_DD("yyyy-MM-dd"),
    /**
     * The Yyyy mm dd hh mm ss.
     */
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    /**
     * Yyyymmddhhmmss date format enum.
     */
    YYYYMMDDHHMMSS("yyyy_MM_dd_HH_mm_ss"),
    /**
     * The Yyyy mm dd hh mm.
     */
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
    /**
     * Yyyymmddhhmms ssss date format enum.
     */
    YYYYMMDDHHMMSSsss("yyyyMMddHHmmssS"),
    YYYYMMDDHHMMSS1("yyyyMMddHHmmss"),
    /**
     * Yyyymmdd date format enum.
     */
    YYYYMMDD("yyyyMMdd"),
    /**
     * Yyyynmmyddr date format enum.
     */
    YYYYNMMYDDR("yyyy年MM月dd日");

    //    YYYY_MM_DD {public String getValue(){return "yyyy-MM-dd HH:mm:ss";}},
    //    YYYY_MM_DD_HH_MM_SS {public String getValue(){return "yyyy-MM-dd HH:mm:ss";}},
    //    YYYY_MM_DD_HH_MM {public String getValue(){return "yyyy-MM-dd HH:mm";}};

    private String value;

    DateFormatEnum(String value) {
        this.value = value;
    }

    /**
     * Get value string.
     *
     * @return the value
     * @author ErebusST
     * @since 2022 -01-07 15:36:10
     */
    public String getValue() {
        return value;
    }

}
