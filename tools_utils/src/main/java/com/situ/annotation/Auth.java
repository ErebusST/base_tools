/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解，用于auth授权时的权限控制
 *
 * @author 司徒彬
 * @date 2020 /7/24 10:46
 */
@Documented //文档
@Retention(RetentionPolicy.RUNTIME) //在运行时可以获取
@Target({ElementType.TYPE, ElementType.METHOD}) //作用到类，方法，接口上等
@Inherited //子类会继承
public @interface Auth {
    /**
     * Value string.
     *
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    String code() default "";
}
