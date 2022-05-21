/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.situ.entity.bo.JdbcField;
import com.situ.entity.bo.TableSetting;
import org.apache.commons.lang3.tuple.Pair;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author 司徒彬
 * @date 2022/5/21 18:26
 */

public class FieldUtils {
    private static final Map<String, TableSetting> TABLE_SETTING = new ConcurrentHashMap<>();


    /**
     * Get table settings list.
     *
     * @param schema the schema
     * @return the list
     * @author ErebusST
     * @since 2022 -05-21 20:28:57
     */
    public static List<TableSetting> getTableSettings(String schema) {
        return TABLE_SETTING.entrySet()
                .stream()
                .filter(entry -> {
                    String key = entry.getKey();
                    return StringUtils.startsWith(key, schema);
                })
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Get fields list.
     *
     * @param clazz the clazz
     * @return the fields
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -05-21 18:38:40
     */
    public static TableSetting getEntityInfo(Class<?> clazz) throws Exception {
        Table table = clazz.getAnnotation(Table.class);
        if (ObjectUtils.isNull(table)) {
            throw new Exception(String.format("The class named %s is not a hibernate entity.", clazz.getName()));
        }
        String schema = table.schema();
        String name = table.name();
        String key = StringUtils.getCombineString("^", schema, name);
        if (!TABLE_SETTING.containsKey(key)) {
            String packageName = clazz.getPackage().getName();
            List<Class<?>> classes = ClassUtils.getClasses(packageName);
            classes.parallelStream()
                    .forEach(temp -> {
                        Table annotation = temp.getAnnotation(Table.class);
                        TableSetting setting = new TableSetting();
                        setting.setSchema(annotation.schema());
                        setting.setTable(annotation.name());
                        List<JdbcField> fields = Arrays.stream(ReflectionUtils.getFields(temp))
                                .map(field -> {
                                    JdbcField jdbcField = new JdbcField();
                                    Id id = field.getAnnotation(Id.class);
                                    jdbcField.setPrimaryKey(ObjectUtils.isNotEmpty(id));

                                    GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                                    boolean autoGenerate;
                                    if (ObjectUtils.isNotNull(generatedValue)) {
                                        autoGenerate = generatedValue.strategy().equals(GenerationType.AUTO);
                                    } else {
                                        autoGenerate = false;
                                    }
                                    jdbcField.setAutoGenerate(autoGenerate);
                                    jdbcField.setField(field);
                                    Column column = field.getAnnotation(Column.class);
                                    jdbcField.setFieldInDb(column.name());
                                    if (jdbcField.isPrimaryKey()) {
                                        setting.setAutoIncrement(autoGenerate);
                                        setting.setPrimaryKey(jdbcField);
                                    }
                                    return jdbcField;
                                })
                                .collect(Collectors.toList());
                        setting.setFields(fields);
                        TABLE_SETTING.put(setting.getKey(), setting);
                    });
        }
        return TABLE_SETTING.get(key);
    }

    /**
     * Get primary key setting jdbc field.
     *
     * @param clazz the clazz
     * @return the primary key setting
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -05-21 18:38:35
     */
    public static JdbcField getPrimaryKeySetting(Class<?> clazz) throws Exception {
        TableSetting setting = getEntityInfo(clazz);
        JdbcField primaryKey = setting.getPrimaryKey();
        if (ObjectUtils.isNull(primaryKey)) {
            throw new Exception("在实体中未找到主键设置");
        }
        return primaryKey;
    }


    /**
     * Get field join string for insert field string.
     *
     * @param <Entity> the type parameter
     * @param setting  the setting
     * @return the field join string for insert field
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -05-21 19:50:34
     */
    public static <Entity> String getFieldJoinStringForSelect(TableSetting setting) {
        return setting.getFields()
                .stream()
                .map(JdbcField::getFieldInDb)
                .collect(Collectors.joining(","));
    }

    /**
     * Get field join string for insert field string.
     *
     * @param <Entity> the type parameter
     * @param entity   the entity
     * @return the field join string for insert field
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -05-21 19:38:12
     */
    public static <Entity> String getFieldJoinStringForInsertField(Entity entity) throws Exception {
        Class<?> clazz = entity.getClass();
        TableSetting setting = getEntityInfo(clazz);
        return setting.getFields()
                .stream()
                .filter(field -> {
                    Object value = ReflectionUtils.getFieldValue(entity, field.getField().getName());
                    return ObjectUtils.isNotNull(value);
                })
                .map(JdbcField::getFieldInDb)
                .collect(Collectors.joining(","));
    }

    /**
     * Get field join string for insert parameter string.
     *
     * @param entity the entity
     * @return the field join string for insert parameter
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -05-21 19:07:49
     */
    public static <Entity> String getFieldJoinStringForInsertParameter(Entity entity) throws Exception {
        Class<?> clazz = entity.getClass();
        TableSetting setting = getEntityInfo(clazz);
        return setting.getFields()
                .stream()
                .filter(field -> {
                    Object value = ReflectionUtils.getFieldValue(entity, field.getField().getName());
                    return ObjectUtils.isNotNull(value);
                })
                .map(JdbcField::getFieldInDb)

                .map(str -> ":".concat(str))
                .collect(Collectors.joining(","));
    }

    /**
     * Get field join string for update string string.
     *
     * @param <Entity> the type parameter
     * @param entity   the entity
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -05-21 19:09:15
     */
    public static <Entity> String getFieldJoinStringForUpdateString(Entity entity) throws Exception {
        return getFieldJoinStringForUpdateString(entity, true);
    }

    /**
     * Get field join string for update string string.
     *
     * @param <Entity>   the type parameter
     * @param entity     the entity
     * @param filterNull the filter null
     * @return the field join string for update string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -05-21 20:05:46
     */
    public static <Entity> String getFieldJoinStringForUpdateString(Entity entity, Boolean filterNull) throws Exception {
        Class<?> clazz = entity.getClass();
        TableSetting setting = getEntityInfo(clazz);
        return setting.getFields()
                .stream()
                .filter(field -> !field.isPrimaryKey())
                .filter(field -> {
                    if (!filterNull) {
                        return true;
                    }
                    Object value = ReflectionUtils.getFieldValue(entity, field.getField().getName());
                    return ObjectUtils.isNotNull(value);
                })
                .map(JdbcField::getFieldInDb)
                .filter(str -> !StringUtils.equalsAnyIgnoreCase(str,
                        setting.getPrimaryKey().getField().getName(), "createTime"))
                .map(str -> StringUtils.concat(str, " = :", str))
                .collect(Collectors.joining(","));
    }

    /**
     * Get parameters map.
     *
     * @param <Entity> the type parameter
     * @param entity   the entity
     * @return the parameters
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -05-21 19:01:12
     */
    public static <Entity> Map<String, Object> getParameters(Entity entity) throws Exception {
        Class<?> clazz = entity.getClass();
        TableSetting setting = getEntityInfo(clazz);
        Map<String, Object> parameters = setting.getFields()
                .stream()
                .map(field -> {
                    String name = field.getField().getName();
                    Object value = ReflectionUtils.getFieldValue(entity, name);
                    if (ObjectUtils.isNull(value)) {
                        return null;
                    }
                    return Pair.of(name, value);
                })
                .filter(ObjectUtils::isNotNull)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        return parameters;
    }


}
