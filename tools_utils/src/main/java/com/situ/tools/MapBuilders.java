/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The type Map builders.
 *
 * @param <Key>   the type parameter
 * @param <Value> the type parameter
 * @author 司徒彬
 * @date 2022 /1/8 15:53
 */
public class MapBuilders<Key, Value> {

    private LinkedHashMap<Key, Value> temp;

    /**
     * New instance map builders.
     *
     * @return the map builders
     * @author ErebusST
     * @since 2022 -08-23 11:32:58
     */
    public static MapBuilders newInstance() {
        return new MapBuilders();
    }

    /**
     * New instance map builders.
     *
     * @param size the size
     * @return the map builders
     * @author ErebusST
     * @since 2022 -08-23 11:32:58
     */
    public static MapBuilders newInstance(Integer size) {
        return new MapBuilders(size);
    }

    private MapBuilders() {
        temp = new LinkedHashMap<>(100);
    }

    private MapBuilders(Integer size) {
        temp = new LinkedHashMap<>(size);
    }

    /**
     * Add map builders.
     *
     * @param key   the key
     * @param value the value
     * @return the map builders
     * @author ErebusST
     * @since 2022 -08-23 11:32:58
     */
    public MapBuilders add(Key key, Value value) {
        if(ObjectUtils.isNotNull(value)){
            temp.put(key, value);
        }
        return this;
    }

    /**
     * Get map.
     *
     * @return the map
     * @author ErebusST
     * @since 2022 -08-23 11:32:58
     */
    public Map<Key, Value> get() {
        return temp;
    }
}
