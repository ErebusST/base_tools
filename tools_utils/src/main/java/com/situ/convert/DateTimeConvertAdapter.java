/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.convert;

import com.google.gson.*;
import com.situ.tools.DataSwitch;
import com.situ.tools.DateUtils;
import com.situ.tools.ObjectUtils;
import com.situ.tools.StringUtils;

import java.lang.reflect.Type;
import java.sql.Timestamp;

/**
 * @author 司徒彬
 * @date 2022/12/5 15:17
 */
public class DateTimeConvertAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
    @Override
    public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String value = json.getAsString();
        try {
            if (value.equals("") || value.equals("null")) {
                return null;
            }
            if (StringUtils.endsWith(value, ".0")) {
                value = StringUtils.replace(value, ".0", "");
            }
        } catch (Exception ignore) {
            return null;
        }
        try {
            return DateUtils.getTimestamp(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public JsonElement serialize(Timestamp value, Type typeOfSrc, JsonSerializationContext context) {
        if (ObjectUtils.isNull(value)) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(value.toString());
    }
}
