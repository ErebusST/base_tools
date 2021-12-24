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
    public static <T> List<T> newArrayList(T... t) {
        return Arrays.stream(t).collect(Collectors.toList());
    }

    public static <T> List<T> unionAll(List<T>... lists) {
        return Arrays.stream(lists).flatMap(list -> list.stream()).collect(Collectors.toList());
    }

    public static <T> T last(List<T> list) {
        int size = list.size();
        if (size == 0) {

            return null;
        } else {
            return list.get(size - 1);
        }

    }


    public static <T> T first(List<T> day) {
        int size = day.size();
        if (size > 0) {
            return day.get(0);
        } else {
            return null;
        }
    }
}
