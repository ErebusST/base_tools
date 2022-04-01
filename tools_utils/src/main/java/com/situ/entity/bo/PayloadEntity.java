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

import java.io.Serializable;

/**
 * The type Payload entity.
 */
@Getter
@Setter
public class PayloadEntity  implements Serializable {
    //The issuer of the token，token 是给谁的
    private String iss;
    //The subject of the token，token 主题
    private String sub;
    //Expiration Time。 token 过期时间，Unix 时间戳格式
    private String exp;
    //Issued At。 token 创建时间， Unix 时间戳格式
    private String iat;
    //JWT ID。针对当前 token 的唯一标识
    private String jti;


}
