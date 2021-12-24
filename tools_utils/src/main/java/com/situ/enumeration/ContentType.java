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
 * @date 2017-04-28 11:28
 */
public enum ContentType {

    application_json("application/json"),
    application_x_www_form_urlencoded("application/x-www-form-urlencoded"),
    text_xml("text/xml"),
    text_plain("text/plain"),
    other("other"),
    ;

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
