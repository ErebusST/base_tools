/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.convert;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * @author 司徒彬
 * @date 2022/11/11 11:47
 */
public class BigDecimalConvertAdapter implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            if (json.getAsString().equals("") || json.getAsString().equals("null")) {
                return null;
            }
        } catch (Exception ignore) {
            return null;
        }
        try {
            return json.getAsBigDecimal();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public JsonElement serialize(BigDecimal value, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(value);
    }
}
