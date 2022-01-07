/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.enumeration;

/**
 * The enum Http method.
 *
 * @author 司徒彬
 * @date 2020 /6/26 19:42
 */
public enum HttpMethod {
    /**
     * Post http method.
     */
    Post("POST"),
    /**
     * Get http method.
     */
    Get("Get"),
    /**
     * Put http method.
     */
    Put("PUT"),
    /**
     * Delete http method.
     */
    Delete("DELETE"),
    /**
     * Options http method.
     */
    Options("OPTIONS"),

    ;
    private String method;

    HttpMethod(String method) {
        this.method = method;
    }

    /**
     * Get method string.
     *
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:36:10
     */
    public String getMethod(){
        return method.toUpperCase();
    }
}
