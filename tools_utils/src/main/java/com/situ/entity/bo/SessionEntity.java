/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;


import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

/**
 * ${description}
 *
 * @author 司徒彬
 * @date 2017-11-24 11:56
 */
@Setter
@Getter
public class SessionEntity {
    private Long timeStamp;
    private Object value;
    private Boolean deleteWhenExpire = true;
    private Long currentTime = System.currentTimeMillis();
    private Long expire;//秒


    public static SessionEntity getInstance(Object value, @Nonnull long expire) {
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setTimeStamp(System.currentTimeMillis());
        sessionEntity.setValue(value);
        if (expire <= 0L) {
            sessionEntity.setDeleteWhenExpire(false);
        }else {
            sessionEntity.setDeleteWhenExpire(true);
            sessionEntity.setExpire(expire);
        }
        return sessionEntity;
    }

}
