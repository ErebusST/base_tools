/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.enumeration;

/**
 * ContentType枚举 枚举
 *
 * @author 司徒彬
 * @date 2017 -04-28 11:28
 */
public enum ContentType {

    /**
     * Application json content type.
     */
    application_json("application/json"),
    /**
     * Application x www form urlencoded content type.
     */
    application_x_www_form_urlencoded("application/x-www-form-urlencoded"),
    /**
     * Text xml content type.
     */
    text_xml("text/xml"),
    /**
     * Text plain content type.
     */
    text_plain("text/plain"),
    /**
     * Other content type.
     */
    other("other"),
    ;

    private String value;

    ContentType(String value) {
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

    @Override
    public String toString() {
        return value;
    }
}
