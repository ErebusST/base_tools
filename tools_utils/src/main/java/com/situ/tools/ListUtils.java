/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type List utils.
 *
 * @author 司徒彬
 * @date 2020 /4/16 23:51
 */
public class ListUtils extends org.apache.commons.collections.ListUtils {
    /**
     * New array list list.
     *
     * @param <T> the type parameter
     * @param t   the t
     * @return the list
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static <T> List<T> newArrayList(T... t) {
        return Arrays.stream(t).collect(Collectors.toList());
    }

    /**
     * Union all list.
     *
     * @param <T>   the type parameter
     * @param lists the lists
     * @return the list
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static <T> List<T> unionAll(List<T>... lists) {
        return Arrays.stream(lists).flatMap(list -> list.stream()).collect(Collectors.toList());
    }

    /**
     * Last t.
     *
     * @param <T>  the type parameter
     * @param list the list
     * @return the t
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static <T> T last(List<T> list) {
        int size = list.size();
        if (size == 0) {

            return null;
        } else {
            return list.get(size - 1);
        }

    }


    /**
     * First t.
     *
     * @param <T>  the type parameter
     * @param list the list
     * @return the t
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static <T> T first(List<T> list) {
        int size = list.size();
        if (size > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Try get t.
     *
     * @param <T>   the type parameter
     * @param list  the list
     * @param index the index
     * @return the t
     * @author ErebusST
     * @since 2022 -01-12 13:00:46
     */
    public static <T> T tryGet(List<T> list, Integer index) {
        return tryGet(list, index, null);
    }

    /**
     * Try get t.
     *
     * @param <T>          the type parameter
     * @param list         the list
     * @param index        the index
     * @param defaultValue the default value
     * @return the t
     * @author ErebusST
     * @since 2022 -01-12 13:00:08
     */
    public static <T> T tryGet(List<T> list, Integer index, T defaultValue) {
        int size = list.size();
        if (index >= size) {
            return defaultValue;
        } else {
            return list.get(index);
        }
    }
}
