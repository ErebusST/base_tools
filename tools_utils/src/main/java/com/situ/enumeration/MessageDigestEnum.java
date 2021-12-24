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
 * @date 2017-04-12 10:47
 */
public enum MessageDigestEnum {
    MD5("MD5"), SHA1("SHA1"),
    ;

    private String value;

    MessageDigestEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
