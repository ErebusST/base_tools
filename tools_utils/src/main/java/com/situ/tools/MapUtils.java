/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import java.util.Map;

/**
 * @author 司徒彬
 * @date 2022/2/27 13:08
 */
public class MapUtils {
    /**
     * Try get value.
     *
     * @param <Key>   the type parameter
     * @param <Value> the type parameter
     * @param map     the map
     * @param key     the key
     * @return the value
     * @author ErebusST
     * @since 2022 -02-17 17:17:53
     */
//public static <Key, Value> Value tryGet(Map<Key, Value> map, Key key, Value defaultValue) {
    //    if (map.containsKey(key)) {
    //        return map.get(key);
    //    } else {
    //        return defaultValue;
    //    }
    //}
    public static <Key, Value> Value tryGet(Map<Key, Value> map, Key key) {
        return tryGet(map, key, null);
    }

    /**
     * Try get t.
     *
     * @param <Key>        the type parameter
     * @param <Value>      the type parameter
     * @param map          the map
     * @param key          the key
     * @param defaultValue the default value
     * @return the t
     * @author ErebusST
     * @since 2022 -03-17 19:18:39
     */
    public static <Key, Value> Value tryGet(Map<Key, Value> map, Key key, Value defaultValue) {
        if (map.containsKey(key)) {
            try {
                return map.get(key);
            } catch (Exception ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Try get value.
     *
     * @param <Key>   the type parameter
     * @param <Value> the type parameter
     * @param map     the map
     * @param index   the index
     * @return the value
     * @author ErebusST
     * @since 2022 -04-05 22:54:51
     */
    public static <Key, Value> Value tryGet(Map<Key, Value> map, Integer index) {
        return tryGet(map,index,null);
    }

    /**
     * Try get value.
     *
     * @param <Key>        the type parameter
     * @param <Value>      the type parameter
     * @param map          the map
     * @param index        the index
     * @param defaultValue the default value
     * @return the value
     * @author ErebusST
     * @since 2022 -04-05 22:53:48
     */
    public static <Key, Value> Value tryGet(Map<Key, Value> map, Integer index, Value defaultValue) {
        Object[] objects = map.keySet().toArray();
        int length = objects.length;
        if (index >= length) {
            return defaultValue;
        } else {
            Key key = (Key) objects[index];
            return map.get(key);
        }
    }
}
