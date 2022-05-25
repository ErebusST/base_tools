/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * @author 司徒彬
 * @date 2022/5/21 18:26
 */
@Getter
@Setter
public class JdbcField {
    /**
     * The Field.
     */
    Field field;
    /**
     * The Field in db.
     */
    String fieldInDb;
    /**
     * The Auto generate.
     */
    boolean autoGenerate;//GeneratedValue

    boolean primaryKey;
}