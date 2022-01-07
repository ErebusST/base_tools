/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;


/**
 * Bean操作类
 *
 * @author 司徒彬
 * @date 2017年1月12日10 :54:59
 */
@Slf4j
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

    /**
     * Deep clone t.
     * <p>
     * 深度拷贝注意，如果obj 实现 Serializable 接口 或者 非JavaBean 则不支持深度拷贝，会返回传入的obj
     *
     * @param <T> the type parameter
     * @param obj the obj
     * @return the t
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static <T> T deepClone(Object obj) {
        try {
            if (obj.getClass().equals(JsonObject.class) || obj.getClass().equals(JsonArray.class)) {
                return (T) obj;
            }
            //将对象写到流里
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            //从流里读出来
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) (objectInputStream.readObject());
        } catch (NotSerializableException ex) {
            try {
                return copyBean((T) obj);
            } catch (Exception e) {
                return (T) obj;
            }

        } catch (Exception ex) {
            return (T) obj;
        }
    }

    /**
     * Clone bean t.
     *
     * @param <T> the type parameter
     * @param obj the obj
     * @return the t
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static final <T> T copyBean(@Nonnull T obj) throws Exception {
        try {
            return copyBean(obj, (Class<T>) obj.getClass());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 通过反射将指定的Bean复制到泛型的实体中，源实体中没有的属性将为null，并且只有属性名称以及属性类型相同的字段才会复制
     *
     * @param <T>          the type parameter
     * @param sourceEntity the obj 复制源实体
     * @param targetEntity the t 目标实体
     * @param isCover      the is cover 是否覆盖，如果是false，则目标实体中有值的属性不会被赋值
     * @return the t
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static <T> T copyBean(@Nonnull Object sourceEntity, @Nonnull T targetEntity, @Nonnull boolean isCover) {
        return copyBean(sourceEntity, targetEntity, isCover, false);
    }

    /**
     * Copy bean t.
     *
     * @param <T>          the type parameter
     * @param sourceEntity the source entity
     * @param targetEntity the target entity
     * @param isCover      the is cover
     * @param ignoreType   the ignore type
     * @return the t
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static <T> T copyBean(@Nonnull Object sourceEntity, @Nonnull T targetEntity, @Nonnull boolean isCover, @Nonnull boolean ignoreType) {
        try {
            //把目标实体的继承实体中的属性，与目标实体中的属性合并成一个list
            Field[] targetFields = ReflectionUtils.getFields(targetEntity.getClass());// Arrays.asList(targetEntity.getClass().getDeclaredFields());

            //把源实体的继承实体中的属性，与源实体中的属性合并成一个list
            Field[] fieldsTemp = ReflectionUtils.getFields(sourceEntity.getClass());// Arrays.asList(sourceEntity.getClass().getDeclaredFields());

            Arrays.stream(targetFields).forEach(targetField ->
                    Arrays.stream(fieldsTemp)
                            .filter(field -> {
                                boolean sameName = field.getName().equals(targetField.getName());
                                boolean sameType = true;
                                if (!ignoreType) {
                                    sameType = field.getType().equals(targetField.getType());
                                }
                                return sameName && sameType;
                            })
                            .findFirst()
                            .ifPresent(
                                    field -> {
                                        boolean isFinal = ReflectionUtils.isFinal(field);
                                        if (!isFinal) {
                                            final String fieldName = field.getName();
                                            Object targetValue = ReflectionUtils.getFieldValue(targetEntity, fieldName);
                                            if (isCover || (isCover && targetValue == null)) {
                                                Object value = ReflectionUtils.getFieldValue(sourceEntity, fieldName);
                                                // field.get
                                                if(!targetField.getType().equals(field.getType())){
                                                    value = DataSwitch.getDefaultValue(value,targetField.getType());
                                                }
                                                ReflectionUtils.setFieldValue(targetEntity, fieldName, value);
                                            }
                                        }
                                    }
                            )
            );
            return targetEntity;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 通过反射将指定的Bean复制到泛型的实体中,全部覆盖
     *
     * @param <T>          the type parameter
     * @param sourceEntity the obj
     * @param clazz        the clazz
     * @return the t
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static <T> T copyBean(@Nonnull Object sourceEntity, Class<T> clazz) {
        try {
            return copyBean(sourceEntity, clazz, false);
        } catch (Exception ex) {
            //不可能出现的异常
            return null;
        }
    }

    /**
     * 通过反射将指定的Bean复制到泛型的实体中,全部覆盖
     *
     * @param <T>          the type parameter
     * @param sourceEntity the obj
     * @param clazz        the clazz
     * @param ignoreType   the ignore type
     * @return the t
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static <T> T copyBean(@Nonnull Object sourceEntity, Class<T> clazz, boolean ignoreType) {
        try {
            T targetEntity = clazz.newInstance();
            return copyBean(sourceEntity, targetEntity, true, ignoreType);
        } catch (Exception ex) {
            //不可能出现的异常
            ex.printStackTrace();
            JsonElement element = DataSwitch.convertObjectToJsonElement(sourceEntity);
            StringBuilder error = new StringBuilder("copyBean 出现异常");
            error.append(StaticValue.LINE_SEPARATOR);
            error.append("对象:").append(StaticValue.LINE_SEPARATOR);
            error.append(element).append(StaticValue.LINE_SEPARATOR);
            error.append("to_class:").append(clazz.getName());
            log.error(error.toString(), ex);
            return null;
        }
    }


}
