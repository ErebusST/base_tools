/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.situ.entity.bo.DataItem;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * Get limit data string.
     *
     * @param <T>    the type parameter
     * @param <R>    the type parameter
     * @param stream the stream
     * @param sortBy the sort by
     * @param limit  the limit
     * @param fields the fields
     * @return the limit data
     * @author ErebusST
     * @since 2022 -01-13 15:35:28
     */
    public static <T, R extends Comparable<? super R>> JsonArray limit(List<T> stream,
                                                                    Function<T, R> sortBy,
                                                                    Integer limit,
                                                                    Pair<String, Function<T, Object>>... fields) {
        return limit(stream.stream(), sortBy, limit, fields);
    }


    /**
     * Get limit data string.
     *
     * @param <T>    the type parameter
     * @param <R>    the type parameter
     * @param stream the stream
     * @param sortBy the sort by
     * @param limit  the limit
     * @param fields the fields
     * @return the limit data
     * @author ErebusST
     * @since 2022 -01-13 15:23:01
     */
    public static <T, R extends Comparable<? super R>> JsonArray limit(Stream<T> stream,
                                                                    Function<T, R> sortBy,
                                                                    Integer limit,
                                                                    Pair<String, Function<T, Object>>... fields) {
        return stream
                .filter(item -> {
                    R value = sortBy.apply(item);
                    return ObjectUtils.isNotNull(value);
                })
                .sorted(Comparator.comparing(sortBy).reversed())
                .limit(limit)
                .map(item -> {
                    JsonObject object = new JsonObject();
                    Arrays.stream(fields)
                            .forEach(setting -> {
                                String column = setting.getLeft();
                                Function<T, Object> field = setting.getRight();
                                Object value = field.apply(item);
                                object.add(column, DataSwitch.convertObjectToJsonElement(value));
                            });
                    return object;
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), DataSwitch::convertObjectToJsonArray));

    }


    /**
     * Format percent string.
     *
     * @param <T>     the type parameter
     * @param list    the list
     * @param groupBy the group by
     * @return the string
     * @author ErebusST
     * @since 2022 -01-17 10:58:57
     */
    public static <T> JsonArray formatPercent(List<T> list, Function<T, Object> groupBy) {
        return formatPercent(list, groupBy, null);
    }


    /**
     * Format percent string.
     *
     * @param <T>     the type parameter
     * @param <R>     the type parameter
     * @param list    the list
     * @param groupBy the group by
     * @param sort    the sort
     * @return the string
     * @author ErebusST
     * @since 2022 -01-17 11:03:27
     */
    public static <T, R extends Comparable<? super R>> JsonArray formatPercent(List<T> list, Function<T, Object> groupBy, Function<T, R> sort) {
        return formatPercent(list.stream(), list.size(), groupBy, sort);
    }

    /**
     * Format percent string.
     *
     * @param <T>     the type parameter
     * @param list    the list
     * @param total   the total
     * @param groupBy the group by
     * @return the string
     * @author ErebusST
     * @since 2022 -01-17 11:03:18
     */
    public static <T> JsonArray formatPercent(Stream<T> list, Integer total, Function<T, Object> groupBy) {
        return formatPercent(list, total, groupBy, null);
    }

    /**
     * Format percent string.
     *
     * @param <T>     the type parameter
     * @param <R>     the type parameter
     * @param list    the list
     * @param total   the total
     * @param groupBy the group by
     * @param sort    the sort
     * @return the string
     * @author ErebusST
     * @since 2022 -01-17 10:58:58
     */
    public static <T, R extends Comparable<? super R>> JsonArray formatPercent(Stream<T> list, Integer total, Function<T, Object> groupBy, Function<T, R> sort) {
        Stream<Pair<String, List<T>>> stream = list
                .map(item -> {
                    Object apply = groupBy.apply(item);
                    if (ObjectUtils.isNull(apply)) {
                        return Pair.of("无数据", item);
                    } else if (Boolean.class.equals(apply.getClass())) {
                        apply = DataSwitch.convertObjectToBoolean(apply) ? "是" : "否";
                    }
                    return Pair.of(apply.toString(), item);
                })
                .collect(Collectors.groupingBy(Pair::getKey))
                .entrySet()
                .stream()
                .map(entry -> {
                    String type = entry.getKey();
                    List<T> value = entry.getValue().stream().map(Pair::getValue).collect(Collectors.toList());
                    return Pair.of(type, value);
                });

        if (ObjectUtils.isNotNull(sort)) {
            stream = stream
                    .map(entry -> {
                        T t = entry.getRight().get(0);
                        R index = sort.apply(t);
                        return Pair.of(index, entry);
                    })
                    .sorted(Comparator.comparing(Pair::getKey))
                    .map(Pair::getRight);
        }
        AtomicInteger index = new AtomicInteger(0);
        List<DataItem> collect = stream
                .map(entry -> {
                    String type = entry.getKey();
                    Integer count = entry.getRight().size();
                    BigDecimal percent = NumberUtils.percent(count, total);
                    DataItem dataItem = new DataItem();
                    dataItem.setIndex(index.getAndIncrement());
                    dataItem.setText(type);
                    dataItem.setCount(DataSwitch.convertObjectToBigDecimal(count));
                    dataItem.setPercent(percent);
                    return dataItem;
                })
                .collect(Collectors.toList());
        collect = NumberUtils.fixPercent(collect);

        return JsonUtils.toJsonArray(collect);
    }

    /**
     * Format percent string.
     *
     * @param jsonString the json string
     * @return the string
     * @author ErebusST
     * @since 2022 -02-10 14:24:35
     */
    public static JsonArray formatPercent(String jsonString) {
        if (ObjectUtils.isNull(jsonString)) {
            return new JsonArray();
        }
        List<DataItem> list = DataSwitch.convertStringToJsonObject(jsonString)
                .entrySet()
                .stream()
                .map(entry -> {
                    String key = entry.getKey();
                    BigDecimal value = entry.getValue().getAsBigDecimal();
                    return DataItem.get(key, value);
                })
                .sorted(Comparator.comparing(DataItem::getCount).reversed())
                .collect(Collectors.toList());
        return formatPercent(list);
    }

    /**
     * Format percent string.
     *
     * @param list the list
     * @return the string
     * @author ErebusST
     * @since 2022 -01-27 17:03:05
     */
    public static JsonArray formatPercent(List<DataItem> list) {
        list = NumberUtils.fixPercent(list);
        return JsonUtils.toJsonArray(list);
    }

}
