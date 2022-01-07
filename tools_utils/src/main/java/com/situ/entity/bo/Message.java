/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * The type Message.
 *
 * @param <T> the type parameter
 * @author 司徒彬
 * @date 2020 /4/8 13:35
 */
@Setter
@Getter
public class Message<T> {
    private Long taskId;
    private T data;
    private boolean retry = false;
    private boolean resetSurround = true;
    private boolean sendMessage = true;
    private String remark;

}
