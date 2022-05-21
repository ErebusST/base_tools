/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.situ.entity.bo.TableSetting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author 司徒彬
 * @date 2022/5/21 18:19
 */
public class ForeignKeyUtils {

    /**
     * key 为实体对应的key 格式为 schema_name:table_name
     * value 中的map
     * a、key 为 主键
     * b、value 为引用该主键的表名
     */
    private static final Map<String, Map<String, List<String>>> FOREIGN_KEY_SETTING = new ConcurrentHashMap<>();


    /**
     * Get map.
     *
     * @param <T>           the type parameter
     * @param clazz         the clazz
     * @param ignoreClasses the ignore classes
     * @return the map
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -05-21 20:44:58
     */
    public static <T> Map<String, List<String>> get(Class<T> clazz, Class<?>... ignoreClasses) throws Exception {
        List<TableSetting> ignoreList = Arrays.stream(ignoreClasses)
                .map(temp -> {
                    try {
                        TableSetting setting = FieldUtils.getEntityInfo(temp);
                        return setting;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(ObjectUtils::isNotNull)
                .collect(Collectors.toList());
        TableSetting setting = FieldUtils.getEntityInfo(clazz);
        String schema = setting.getSchema();
        String key = setting.getKey();
        String primaryKey = setting.getPrimaryKey().getFieldInDb();
        if (!FOREIGN_KEY_SETTING.containsKey(key)) {
            List<TableSetting> settings = FieldUtils.getTableSettings(schema);
            List<String> foreignTables = settings.stream()
                    .filter(foreign -> {
                        /**
                         * 1、不包含自己
                         * 2、去除排除项
                         */
                        String temp = foreign.getKey();
                        if (StringUtils.equals(temp, key)) {
                            return false;
                        } else {
                            return ignoreList.stream()
                                    .noneMatch(item -> StringUtils.equals(item.getKey(), temp));
                        }
                    })
                    .filter(foreign -> {
                        //找出所有实体中包含对应主键id的实体
                        boolean exist = foreign.getFields().stream()
                                .anyMatch(item -> StringUtils.equalsIgnoreCase(item.getFieldInDb(), primaryKey));
                        return exist;
                    })
                    .map(TableSetting::getTable)
                    .collect(Collectors.toList());
            Map<String, List<String>> foreignMap = new HashMap<>();
            foreignMap.put(primaryKey, foreignTables);
            FOREIGN_KEY_SETTING.put(key, foreignMap);
        }

        return FOREIGN_KEY_SETTING.get(key);

    }


}
