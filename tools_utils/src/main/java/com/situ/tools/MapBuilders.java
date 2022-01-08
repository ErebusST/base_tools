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
 * @author 司徒彬
 * @date 2022/1/8 15:53
 */
public class MapBuilders<Key, Value> {

    private LinkedHashMap<Key, Value> temp;

    public static MapBuilders newInstance() {
        return new MapBuilders();
    }

    public static MapBuilders newInstance(Integer size) {
        return new MapBuilders(size);
    }

    private MapBuilders() {
        temp = new LinkedHashMap<>(100);
    }

    private MapBuilders(Integer size) {
        temp = new LinkedHashMap<>(size);
    }

    public MapBuilders add(Key key, Value value) {
        temp.put(key, value);
        return this;
    }

    public Map<Key, Value> get() {
        return temp;
    }
}
