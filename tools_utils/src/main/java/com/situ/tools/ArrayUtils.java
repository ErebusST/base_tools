/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

/**
 * @author 司徒彬
 * @date 2022/9/26 14:23
 */
public class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {

    /**
     * New array entity [ ].
     *
     * @param <Entity> the type parameter
     * @param entities the entities
     * @return the entity [ ]
     * @author ErebusST
     * @since 2022 -09-26 14:25:07
     */
    public static <Entity> Entity[] newArray(Entity... entities) {
        return entities;
    }
}
