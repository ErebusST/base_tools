/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.google.gson.JsonArray;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author 司徒彬
 * @date 2022/2/27 12:32
 */
public class JsonUtils {

    /**
     * To json array json array.
     *
     * @param objects the objects
     * @return the json array
     * @author ErebusST
     * @since 2022 -02-27 12:32:59
     */
    public static JsonArray toJsonArray(Object... objects){
        return DataSwitch.convertObjectToJsonArray(Arrays.stream(objects)
                .filter(ObjectUtils::isNotNull).collect(Collectors.toList()));
    }
}
