/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import com.situ.tools.ObjectUtils;
import lombok.Getter;

import java.io.Serializable;

/**
 * The type Http header.
 *
 * @author 司徒彬
 * @date 2020 /6/26 15:06
 */
@Getter
public class HttpHeader  implements Serializable {
    private String key;
    private String value;

    private HttpHeader(String key, Object value) {
        this.key = key;
        this.value = ObjectUtils.isEmpty(value) ? "" : value.toString();
    }

    /**
     * Get http header.
     *
     * @param key   the key
     * @param value the value
     * @return the http header
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public static HttpHeader get(String key, Object value) {
        return new HttpHeader(key, value);
    }
}
