/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.enumeration;

/**
 * 自定义http代码
 *
 * @author 司徒彬
 * @date 2019-11-25 15:54
 */
public enum CustomHttpStatus {
    BAD_SIGNATURE(4011, "错误的签名"),
    UNAUTHORIZED(4012, "未授权"),
    AUTHORIZED_TIMEOUT(4013, "授权过期"),
    FORBIDDEN(4003, "恶意请求，单一ip1分钟内庆祝超过30次"),
    BAD_REQUEST(4000, "请求参数错误"),
    OK(200, "OK"),
    UNIFIED_LOGIN_OUT(1000, "使用了统一登陆，并且统一登陆已退出"),
    UNIFIED_MERCHANT__CODE(4014, "未定义的商户code"),

    ;
    private Integer code;
    private String description;

    private CustomHttpStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage(){
        return this.description;
    }
}
