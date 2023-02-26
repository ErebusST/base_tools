/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ObjectUtils
 *
 * @author 司徒彬
 * @date 2017 -03-30 14:03
 */
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {
    /**
     * Is empty boolean.
     *
     * @param object the object
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:07
     */
    public static boolean isEmpty(Object object) {
        return !isNotEmpty(object);
    }

    /**
     * Is not empty boolean.
     *
     * @param object the object
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:07
     */
    public static boolean isNotEmpty(Object object) {
        boolean isNotEmpty = StringUtils.isNotEmpty(object);
        if (object != null) {
            if (object.getClass().equals(List.class) || object.getClass().equals(ArrayList.class)) {
                isNotEmpty = isNotEmpty && ((List) object).size() != 0;
            } else if (object.getClass().equals(JsonArray.class)) {
                isNotEmpty = isNotEmpty && ((JsonArray) object).size() != 0;
            }
        }

        return isNotEmpty;
    }

    /**
     * Is null boolean.
     *
     * @param object the object
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:07
     */
    public static boolean isNull(Object object) {
        return object == null;
    }


    /**
     * Is not null boolean.
     *
     * @param object the object
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:07
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }


    /**
     * Any empty boolean.
     *
     * @param objects the objects
     * @return the boolean
     * @author ErebusST
     * @since 2023 -02-22 18:13:12
     */
    public static boolean anyEmpty(Object... objects) {
        return Arrays.stream(objects).anyMatch(ObjectUtils::isEmpty);
    }

    /**
     * Any null boolean.
     *
     * @param objects the objects
     * @return the boolean
     * @author ErebusST
     * @since 2023 -02-22 18:13:14
     */
    public static boolean anyNull(Object... objects) {
        return Arrays.stream(objects).anyMatch(ObjectUtils::isNull);
    }

    /**
     * Equals boolean.
     *
     * @param obj1 the obj 1
     * @param obj2 the obj 2
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:07
     */
    public static boolean equals(Object obj1, Object obj2) {
        if (isNull(obj1) && isNull(obj2)) {
            return true;
        } else if (isNotNull(obj1)) {
            return obj1.equals(obj2);
        } else if (isNotNull(obj2)) {
            return obj2.equals(obj1);
        }
        return false;
    }
}
