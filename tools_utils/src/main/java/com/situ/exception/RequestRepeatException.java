/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.exception;

/**
 * 重复请求
 *
 * @author 司徒彬
 * @date 2020 /6/24 00:21
 */
public class RequestRepeatException extends Exception {
    /**
     * Instantiates a new Request repeat exception.
     */
    public RequestRepeatException() {
    }

    /**
     * Instantiates a new Request repeat exception.
     *
     * @param message the message
     */
    public RequestRepeatException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Request repeat exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public RequestRepeatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Request repeat exception.
     *
     * @param cause the cause
     */
    public RequestRepeatException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Request repeat exception.
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public RequestRepeatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
