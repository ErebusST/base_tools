/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.google.gson.*;
import com.situ.entity.bo.DataItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author 司徒彬
 * @date 2022/2/27 12:32
 */
@Slf4j
public class JsonUtils {

    /**
     * Merge json array string.
     * <p>
     * 合并N个 形如[["a",1],["b",1],["c",1]] 的二维数组
     *
     * @param data the data
     * @return the string
     * @author ErebusST
     * @since 2022 -02-17 17:17:53
     */
    public static String mergeJsonArray(String... data) {
        return mergeJsonArray(false, data);
    }

    /**
     * Merge json array string.
     * <p>
     * 合并N个 形如[["a",1],["b",1],["c",1]] 的二维数组
     *
     * @param sorted the sorted
     * @param data   the data
     * @return the string
     * @author ErebusST
     * @since 2022 -02-19 22:28:41
     */
    public static String mergeJsonArray(boolean sorted, String... data) {
        List<DataItem> result = merge(sorted, data);
        return JsonUtils.toJsonArrayString(result, false);
    }

    /**
     * Merge list.
     * 合并N个 形如[["a",1],["b",1],["c",1]] 的二维数组
     *
     * @param sorted the sorted
     * @param data   the data
     * @return the list
     * @author ErebusST
     * @since 2022 -02-17 17:17:53
     */
    private static List<DataItem> merge(boolean sorted, String... data) {
        List<DataItem> result = Arrays.stream(data)
                .map(json -> {
                    List<DataItem> items = JsonUtils.toDataItem(json);
                    return items;
                })
                .reduce((item1, item2) -> {
                    List<String> keys = getKey(item1, item2);

                    Map<String, BigDecimal> collect1 = item1.stream().collect(Collectors.toMap(DataItem::getText, DataItem::getCount));
                    Map<String, BigDecimal> collect2 = item2.stream().collect(Collectors.toMap(DataItem::getText, DataItem::getCount));

                    List<DataItem> collect = keys.stream()
                            .map(key -> {
                                BigDecimal count1 = MapUtils.tryGet(collect1, key, BigDecimal.ZERO);
                                BigDecimal count2 = MapUtils.tryGet(collect2, key, BigDecimal.ZERO);
                                return DataItem.get(key, NumberUtils.add(count1, count2));
                            })
                            .collect(Collectors.toList());
                    return collect;
                }).orElse(new ArrayList<>(0));
        result = NumberUtils.fixPercent(result, sorted);
        return result;
    }


    public static JsonObject mergeJsonObject(JsonObject object, Object merge) {
        return mergeJsonObject(object, merge, null);
    }

    @Test
    public void test() {
        JsonObject object = DataSwitch.convertStringToJsonObject("{'a':1,'b':false}");
        JsonObject object1 = DataSwitch.convertStringToJsonObject("{'a':1,'b':false}");
        JsonObject object2 = mergeJsonObject(object, object1);
        log.info(object2.toString());
    }

    public static JsonObject mergeJsonObject(JsonObject object, Object merge, Integer scale) {
        if (ObjectUtils.isNull(object)) {
            object = new JsonObject();
        }
        if (ObjectUtils.isNotNull(merge)) {
            JsonObject object1 = DataSwitch.convertStringToJsonObject(merge);
            if (ObjectUtils.isNull(object1)) {
                return object;
            }
            Set<String> keys = object1.keySet();
            for (String key : keys) {
                JsonElement element = object1.get(key);
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = element.getAsJsonPrimitive();

                    if (primitive.isNumber()) {
                        BigDecimal value = primitive.getAsBigDecimal();
                        if (object.has(key)) {
                            value = NumberUtils.add(primitive.getAsBigDecimal(), object.get(key).getAsBigDecimal());
                        }
                        if (ObjectUtils.isNotNull(scale)) {
                            value = value.setScale(scale, BigDecimal.ROUND_HALF_UP);
                        }
                        object.addProperty(key, value);
                    }

                }

            }
        }
        return object;
    }

    /**
     * Get key list.
     *
     * @param items the items
     * @return the key
     * @author ErebusST
     * @since 2022 -02-17 16:10:07
     */
    private static List<String> getKey(List<DataItem>... items) {
        return Arrays.stream(items)
                .flatMap(List::stream)
                .map(DataItem::getText)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * To data item list.
     *
     * @param json the json
     * @return the list
     * @author ErebusST
     * @since 2022 -02-17 17:17:53
     */
    public static List<DataItem> toDataItem(String json) {
        AtomicInteger index = new AtomicInteger(0);
        JsonArray array = DataSwitch.convertStringToJsonArray(json);
        List<DataItem> collect = StreamSupport.stream(array.spliterator(), false)
                .map(element -> {
                    JsonArray jsonArray = element.getAsJsonArray();
                    DataItem item = new DataItem();
                    item.setIndex(index.getAndIncrement());
                    item.setText(jsonArray.get(0).getAsString());
                    item.setCount(jsonArray.get(1).getAsBigDecimal());
                    return item;
                })
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * To json array json array.
     *
     * @param objects the objects
     * @return the json array
     * @author ErebusST
     * @since 2022 -02-27 12:32:59
     */
    public static JsonArray toJsonArray(Object... objects) {
        return DataSwitch.convertObjectToJsonArray(Arrays.stream(objects)
                .filter(ObjectUtils::isNotNull).collect(Collectors.toList()));
    }


    /**
     * To json array string.
     *
     * @param list the list
     * @return the string
     * @author ErebusST
     * @since 2022 -02-17 17:15:36
     */
    public static String toJsonArrayString(List<DataItem> list) {
        return toJsonArrayString(list, false);
    }

    /**
     * To json array string.
     *
     * @param list the list
     * @return the string
     * @author ErebusST
     * @since 2022 -01-17 13:52:39
     */
    public static String toJsonArrayString(List<DataItem> list, boolean filterZero) {
        return toJsonArray(list, filterZero).toString();
    }


    /**
     * To json array json array.
     *
     * @param list the list
     * @return the json array
     * @author ErebusST
     * @since 2022 -02-27 13:00:25
     */
    public static JsonArray toJsonArray(List<DataItem> list) {
        return toJsonArray(list, false);
    }

    /**
     * To json array json array.
     *
     * @param list       the list
     * @param filterZero the filter zero
     * @return the json array
     * @author ErebusST
     * @since 2022 -02-17 19:35:21
     */
    public static JsonArray toJsonArray(List<DataItem> list, boolean filterZero) {
        return list.stream()
                .filter(item -> {
                    if (filterZero) {
                        return !item.getCount().equals(0L);
                    } else {
                        return true;
                    }
                })
                .map(DataItem::toArray)
                .collect(Collectors.collectingAndThen(Collectors.toList(), DataSwitch::convertObjectToJsonArray));
    }

    /**
     * Get big decimal.
     * 从 [['a',1],['b',1]] 找到key 为a 的值
     *
     * @param jsonArray the json array
     * @param key       the key
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:21:55
     */
    public static BigDecimal get(Object jsonArray, String key) {
        if (StringUtils.isEmpty(jsonArray)) {
            return BigDecimal.ZERO;
        }
        JsonArray array = DataSwitch.convertStringToJsonArray(jsonArray);
        BigDecimal value = StreamSupport.stream(array.spliterator(), true)
                .filter(element -> {
                    JsonArray child = element.getAsJsonArray();
                    String temp = child.get(0).getAsString();
                    return StringUtils.equalsIgnoreCase(temp, key);
                })
                .map(element -> {
                    JsonArray child = element.getAsJsonArray();
                    BigDecimal temp = child.get(1).getAsBigDecimal();
                    return temp;
                })
                .findFirst()
                .orElse(BigDecimal.ZERO);
        return value;
    }

    /**
     * Try get t.
     *
     * @param object the object
     * @param key    the key
     * @return the t
     * @author ErebusST
     * @since 2022 -03-23 14:59:07
     */
    public static Object tryGet(JsonObject object, String key) {
        return tryGet(object, key, null);
    }

    /**
     * Try get t.
     *
     * @param object       the object
     * @param key          the key
     * @param defaultValue the default value
     * @return the t
     * @author ErebusST
     * @since 2022 -03-23 14:59:09
     */
    public static Object tryGet(JsonObject object, String key, Object defaultValue) {
        if (ObjectUtils.isNull(object)) {
            return defaultValue;
        } else if (object.has(key)) {
            try {
                return object.get(key).getAsString();
            } catch (Exception ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Try get object.
     *
     * @param array the array
     * @param index the index
     * @return the object
     * @author ErebusST
     * @since 2023 -06-29 10:59:12
     */
    public static JsonElement tryGet(JsonArray array, Integer index) {
        int size = array.size();
        if (index >= size) {
            return null;
        } else {
            JsonElement element = array.get(index);
            if (element.isJsonNull()) {
                return null;
            } else {
                return element;
            }
        }
    }
}
