/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.exception;

/**
 * 请求上限异常
 *
 * @author 司徒彬
 * @date 2020 /6/22 00:31
 */
public class RequestLimitException extends Exception {
    /**
     * Instantiates a new Request limit exception.
     */
    public RequestLimitException() {
    }

    /**
     * Instantiates a new Request limit exception.
     *
     * @param message the message
     */
    public RequestLimitException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Request limit exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public RequestLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Request limit exception.
     *
     * @param cause the cause
     */
    public RequestLimitException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Request limit exception.
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public RequestLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
