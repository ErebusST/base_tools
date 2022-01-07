/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.enumeration;


/**
 * 排序 枚举
 *
 * @author 司徒彬
 * @date 2017 -03-31 19:58
 */
public enum OrderTypeEnum {
    /**
     * Asc order type enum.
     */
    ASC("ASC"),
    /**
     * Desc order type enum.
     */
    DESC("DESC"),
    ;

    private String value;

    OrderTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Get value string.
     *
     * @return the value
     * @author ErebusST
     * @since 2022 -01-07 15:39:07
     */
    public String getValue() {
        return value;
    }
}
