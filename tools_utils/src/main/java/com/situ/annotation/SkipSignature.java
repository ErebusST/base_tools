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
 * 跳过签名
 *
 * @author 司徒彬
 * @date 2020/10/28 16:18
 */
@Documented //文档
@Retention(RetentionPolicy.RUNTIME) //在运行时可以获取
@Target({ElementType.TYPE, ElementType.METHOD}) //作用到类，方法，接口上等
@Inherited //子类会继承
public @interface SkipSignature {
}
