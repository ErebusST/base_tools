/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.enumeration;


/**
 * 信息摘要算法类别 枚举
 *
 * @author 司徒彬
 * @date 2017 -04-12 10:47
 */
public enum MessageDigestEnum {
    /**
     * Md 5 message digest enum.
     */
    MD5("MD5"),
    /**
     * Sha 1 message digest enum.
     */
    SHA1("SHA-1"),

    SHA512("SHA-512"),
    ;

    private String value;

    MessageDigestEnum(String value) {
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
