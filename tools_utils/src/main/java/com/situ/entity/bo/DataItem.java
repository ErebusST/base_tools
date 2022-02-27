/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import com.google.gson.JsonObject;
import com.situ.tools.DataSwitch;
import com.situ.tools.ListUtils;
import com.situ.tools.ObjectUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 司徒彬
 * @date 2022/2/27 12:42
 */
@Getter
@Setter
public class DataItem {
    public static DataItem get(String text, BigDecimal count) {
        return get(text, count.longValue());
    }

    public static DataItem get(String text, Integer count) {
        return get(text, count.longValue());
    }

    public static DataItem get(String text, Long count) {
        DataItem item = new DataItem();
        item.setText(text);
        item.setCount(count);
        return item;
    }

    private Integer index;
    private String text;
    private Long count;
    //相对于本批次数据的百分比
    private BigDecimal percent;
    //相对于整体区域数据的百分比
    private BigDecimal percent1;

    List<DataItem> child;

    /**
     * To array list.
     *
     * @return the list
     * @author ErebusST
     * @since 2022 -01-28 14:35:44
     */
    public List<Object> toArray() {
        List<Object> items = ListUtils.newArrayList(text, count, percent);

        if (ObjectUtils.isNotNull(percent1)) {
            items.add(percent1);
        }
        if (ObjectUtils.isNotEmpty(child)) {
            List<List<Object>> child = this.child.stream().map(DataItem::toArray).collect(Collectors.toList());
            items.add(child);
        }
        return items;
    }

    /**
     * To object json object.
     *
     * @return the json object
     * @author ErebusST
     * @since 2022 -01-28 14:35:42
     */
    public JsonObject toObject() {
        Map<String, Object> item = new HashMap<>(4);
        item.put("name", text);
        item.put("count", count);
        item.put("percent", percent);
        if (ObjectUtils.isNotEmpty(child)) {
            List<JsonObject> collect = child.stream().map(DataItem::toObject).collect(Collectors.toList());
            item.put("child", collect);
        }
        return DataSwitch.convertObjectToJsonObject(item);
    }
}
