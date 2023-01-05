/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.google.gson.*;
import com.situ.convert.*;
import com.situ.enumeration.DateFormatEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * DataSwitch 操作类
 *
 * @author 司徒彬
 * @date 2017年1月12日12 :06:33
 */
@Slf4j
public class DataSwitch {

    //region Util

    /**
     * Get default value object.
     *
     * @param value the value
     * @param type  the type
     * @return the default value
     * @author ErebusST
     * @since 2022 -10-26 10:34:11
     */
    public static Object getDefaultValue(Object value, Type type) {
        return getDefaultValue(value, type.getClass());
    }

    /**
     * Gets default value.
     *
     * @param value the value
     * @param type  the type
     * @return the default value
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Object getDefaultValue(Object value, Class<?> type) {
        try {
            Object resultObject;
            if (value == null) {
                resultObject = null;
            } else {
                if (type == String.class) {
                    resultObject = convertObjectToString(value);
                } else if (type == Integer.class || type == int.class) {
                    resultObject = convertObjectToInteger(value);
                } else if (type == Long.class || type == long.class) {
                    resultObject = convertObjectToLong(value);
                } else if (type == Date.class) {
                    resultObject = convertObjectToDate(value);
                } else if (type == Double.class || type == double.class) {
                    resultObject = convertObjectToDouble(value);
                } else if (type == Float.class || type == float.class) {
                    resultObject = convertObjectToFloat(value);
                } else if (type == Boolean.class || type == boolean.class) {
                    resultObject = convertObjectToBoolean(value);
                } else if (type == BigDecimal.class) {
                    resultObject = convertObjectToBigDecimal(value);
                } else if (type == Short.class) {
                    resultObject = convertObjectToShort(value);
                } else {
                    resultObject = value;
                }
            }
            return resultObject;
        } catch (Exception ex) {
            ex.printStackTrace();
            return value;
        }
    }

    //endregion

    //region 数据格式转换

    /**
     * 将整型对象格式字符串转换成整型对象，如果传入对象为 null 或 空，返回 0
     *
     * @param value the value
     * @return the integer
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Integer convertObjectToInteger(Object value) {
        return convertObjectToInteger(value, null);
    }

    /**
     * Convert object to integer integer.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the integer
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Integer convertObjectToInteger(Object value, Integer defaultValue) {
        try {
            if (ObjectUtils.isNotEmpty(value)) {
                return DataSwitch.convertObjectToBigDecimal(value).intValue();
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            log.info("{}/{}", value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 将类型转换成Double对象，如果传入对象为 null 或 空，返回 0.0
     *
     * @param value the value
     * @return the double
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Double convertObjectToDouble(Object value) {
        return convertObjectToDouble(value, null);
    }


    /**
     * Convert object to double double.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the double
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Double convertObjectToDouble(Object value, Double defaultValue) {
        try {
            if (ObjectUtils.isNotEmpty(value)) {
                return DataSwitch.convertObjectToBigDecimal(value).doubleValue();
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * 将对象转换成Long对象，如果传入对象为 null 或 空，返回 0l
     *
     * @param value ： 传入参数值
     * @return the long
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Long convertObjectToLong(Object value) {
        return convertObjectToLong(value, null);
    }

    /**
     * Convert object to long long.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the long
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Long convertObjectToLong(Object value, Long defaultValue) {
        try {
            if (ObjectUtils.isNotEmpty(value)) {
                return DataSwitch.convertObjectToBigDecimal(value).longValue();
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * 将对象转换成Float对象，如果传入对象为 null 或 空，返回 0f
     *
     * @param value the value
     * @return the float
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Float convertObjectToFloat(Object value) {
        return convertObjectToFloat(value, null);
    }

    /**
     * Convert object to float float.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the float
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Float convertObjectToFloat(Object value, Float defaultValue) {
        try {
            if (ObjectUtils.isNotEmpty(value)) {
                return DataSwitch.convertObjectToBigDecimal(value).floatValue();
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将对象转换成BigDecimal对象，如果传入对象为 null 或 空，返回 null
     *
     * @param value the value
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static BigDecimal convertObjectToBigDecimal(Object value) {
        return convertObjectToBigDecimal(value, null);
    }

    /**
     * Convert object to big decimal big decimal.
     *
     * @param value the value
     * @param scale the scale
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static BigDecimal convertObjectToBigDecimal(Object value, Integer scale) {
        return convertObjectToBigDecimal(value, scale, null);
    }

    /**
     * Convert object to big decimal big decimal.
     *
     * @param value        the value
     * @param scale        the scale
     * @param defaultValue the default value
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static BigDecimal convertObjectToBigDecimal(Object value, Integer scale, BigDecimal defaultValue) {
        try {
            if (StringUtils.isNotEmpty(value)) {
                BigDecimal decimal = new BigDecimal(value.toString().trim());
                if (ObjectUtils.isNotNull(scale)) {
                    decimal = decimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
                }
                return decimal;
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将对象转换成Short对象，如果传入对象为 null 或 空，返回 null
     *
     * @param value the value
     * @return the short
     * @author ErebusST
     * @since 2022 -01-07 15:34:45
     */
    public static Short convertObjectToShort(Object value) {
        return convertObjectToShort(value, null);
    }

    /**
     * Convert object to short short.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the short
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static Short convertObjectToShort(Object value, Short defaultValue) {
        try {
            if (ObjectUtils.isNotEmpty(value)) {
                return Short.parseShort(value.toString().trim());
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * 将对象转换成Boolean对象，如果传入对象为 null 或 空，返回false
     *
     * @param value the value
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static Boolean convertObjectToBoolean(Object value) {
        return convertObjectToBoolean(value, false);
    }

    /**
     * Convert object to boolean boolean.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static Boolean convertObjectToBoolean(Object value, Boolean defaultValue) {
        try {
            if (value == null) {
                return defaultValue;
            }
            Class clazz = value.getClass();
            if (clazz.equals(String.class)) {
                return value.toString().equalsIgnoreCase("true") || value.toString().equals("1");
            } else if (clazz.equals(Long.class)) {
                return DataSwitch.convertObjectToLong(value) == 1L;
            } else if (clazz.equals(Integer.class)) {
                return DataSwitch.convertObjectToInteger(value) == 1;
            } else if (clazz.equals(BigInteger.class)) {
                return DataSwitch.convertObjectToInteger(value) == 1;
            } else {
                return (Boolean) value;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 对象转换成String对象，如果传入对象为 null 或 空，返回 ""
     *
     * @param value the value
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static String convertObjectToString(Object value) {
        try {
            if (null != value) {
                Class<?> clazz = value.getClass();
                if (BigDecimal.class.equals(clazz)) {
                    return convertObjectToBigDecimal(value).toPlainString();
                }
                return value.toString().trim();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将日期字符串转换成日期对象
     *
     * @param time the time
     * @return the date
     * @throws ParseException the parse exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static Date convertObjectToDate(Object time) throws ParseException {
        try {
            String format = null;
            if (time == null || "".equals(DataSwitch.convertObjectToString(time))) {
                return null;
            }
            String timeStr = time.toString();
            if (timeStr.contains(":") && !timeStr.contains(".")) {
                if (timeStr.indexOf(":") == timeStr.lastIndexOf(":")) {
                    format = "yyyy-MM-dd HH:mm";
                } else {
                    format = "yyyy-MM-dd HH:mm:ss";
                }
            } else if (timeStr.contains(".")) {
                timeStr = timeStr.substring(0, timeStr.indexOf("."));
                format = "yyyy-MM-dd HH:mm:ss";
            } else {
                format = "yyyy-MM-dd";
            }
            return DateUtils.parseDate(timeStr, new String[]{format});
        } catch (ParseException e) {
            throw e;
        }
    }

    //endregion

    //region Map 与 实体转换

    /**
     * Convert map obj to toolsentity t. 不区分大小写
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param map   the map
     * @return the t
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static <T> T convertMapObjToEntity(Class<T> clazz, Map<String, Object> map) {
        if (ObjectUtils.isNull(map)) {
            return null;
        }
        T t;
        try {
            t = clazz.newInstance();
        } catch (Exception ex) {
            log.error("convertMapObjToEntity 出现异常", ex);
            return null;
        }
        Field[] fields = ReflectionUtils.getFields(clazz);
        Arrays.stream(fields).forEach(field ->
        {
            String name = field.getName();
            Optional<String> keyOptional =
                    map.keySet().stream().filter(key -> key.equalsIgnoreCase(name)).findFirst();
            if (!keyOptional.equals(Optional.empty())) {
                Type type = field.getType();
                String keyName = keyOptional.get();
                try {
                    ReflectionUtils.setFieldValue(t, name, getDefaultValue(map.get(keyName), type));
                } catch (IllegalArgumentException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw ex;
                }
                //map.remove(keyName);
            }
        });
        return t;

    }



    /**
     * 将单个实体转换为Map
     *
     * @param entityObject :目标实体对象
     * @return the map
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static Map<String, Object> convertEntityToMap(Object entityObject) throws Exception {
        try {
            // 转换后的Map
            Map<String, Object> map = new HashMap<>();
            Class clazz = entityObject.getClass();
            Field[] fields = clazz.getDeclaredFields();

            List<String> errorList = new ArrayList<>();

            Arrays.stream(fields).forEach(field ->
            {
                String key = field.getName();
                Object value = ReflectionUtils.getFieldValue(entityObject, key);
                if (ObjectUtils.isNull(value)) {
                    return;
                }
                Class valueClass = value.getClass();
                if (valueClass.equals(ArrayList.class) || valueClass.equals(Collection.class) ||
                        valueClass.equals(List.class)) {
                    try {
                        value = convertListEntityToListMap((List<Object>) value);
                    } catch (Exception e) {
                        errorList.add(e.getMessage());
                    }
                }
                map.put(key, value);
            });
            if (errorList.size() > 0) {
                String errorMessage = StringUtils.concat(errorList.toArray());
                throw new Exception(errorMessage);
            }
            return map;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 将listEntity转换成listMap
     *
     * @param listEntityObject the list toolsentity object
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @功能简介：将listEntity转换成listMap
     * @since 2022 -01-07 15:34:46
     */
    public static List<Map<String, Object>> convertListEntityToListMap(List<Object> listEntityObject) throws Exception {
        try {
            List<Map<String, Object>> listMap = new ArrayList<>();
            List<String> errorList = new ArrayList<>();
            listEntityObject.forEach(entity ->
            {
                try {
                    Map<String, Object> map = convertEntityToMap(entity);
                    listMap.add(map);
                } catch (Exception e) {
                    errorList.add(e.getMessage());
                }
            });
            if (errorList.size() > 0) {
                String errorMessage = StringUtils.concat(errorList.toArray());
                throw new Exception(errorMessage);
            }
            return listMap;
        } catch (Exception ex) {
            throw ex;
        }

    }

    //endregion

    //region Json与实体转换

    private static Gson gson;

    /**
     * Get gson instance gson.
     *
     * @return the gson instance
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static Gson getGsonInstance() {
        return getGsonInstance(true);
    }

    private static Gson getGsonInstance(boolean isSerializeNulls) {
        return getGsonInstance(isSerializeNulls, null);
    }

    private static Gson getGsonInstance(boolean isSerializeNulls, DateFormatEnum dateFormatEnum) {
        if (ObjectUtils.isNull(gson)) {
            dateFormatEnum = ObjectUtils.isNull(dateFormatEnum) ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setPrettyPrinting()
                    .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                    .registerTypeAdapter(Long.class, new LongConvertAdapter())
                    .registerTypeAdapter(Integer.class, new IntegerConvertAdapter())
                    .registerTypeAdapter(Double.class, new DoubleConvertAdapter())
                    .registerTypeAdapter(BigDecimal.class, new BigDecimalConvertAdapter())
                    .registerTypeAdapter(Timestamp.class, new DateTimeConvertAdapter())
                    .setDateFormat(dateFormatEnum.getValue());
            gsonBuilder.registerTypeAdapter(Map.class, new CustomObjectTypeAdapter());
            if (isSerializeNulls) {

                gsonBuilder.serializeNulls();
            }
            gson = gsonBuilder.create();
        }

        return gson;
    }


    /**
     * Convert json string to entity t.
     *
     * @param <T>        the type parameter
     * @param jsonString the json string
     * @param clazz      the clazz
     * @return the t
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static <T> T convertJsonStringToEntity(String jsonString, Class<T> clazz) {
        JsonObject jsonObject = convertStringToJsonObject(jsonString);
        return convertJsonObjectToEntity(jsonObject, clazz);
    }

    /**
     * 将json格式的字符串转换成目标实体
     *
     * @param <T>   the type parameter
     * @param json  ：json格式的字符串
     * @param clazz ：实体
     * @return the t
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:46
     */
    public static <T> T convertJsonObjectToEntity(JsonObject json, Class<T> clazz) {
        try {
            if (ObjectUtils.isNull(json)) {
                return null;
            }
            Gson gson = getGsonInstance();
            T entity = gson.fromJson(json, clazz);
            return entity;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 将实体对象转换成jsonObj对象
     *
     * @param obj the obj
     * @return the json object
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:47
     */
    public static JsonObject convertObjectToJsonObject(Object obj) {
        try {
            JsonElement jsonElement = convertObjectToJsonElement(obj);
            return jsonElement == null ? null : jsonElement.getAsJsonObject();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Convert string to json json object.
     *
     * @param value the value
     * @return the json object
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:47
     */
    public static JsonObject convertStringToJsonObject(Object value) {
        try {
            if (ObjectUtils.isNull(value)) {
                return null;
            }
            return convertStringToJsonElement(value).getAsJsonObject();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Convert string to json array json array.
     *
     * @param value the value
     * @return the json array
     * @author ErebusST
     * @since 2022 -01-07 15:34:47
     */
    public static JsonArray convertStringToJsonArray(Object value) {
        if (ObjectUtils.isEmpty(value)) {
            return new JsonArray();
        } else {
            return convertStringToJsonElement(value.toString()).getAsJsonArray();
        }
    }

    /**
     * Convert string to json json object.
     *
     * @param value the value
     * @return the json object
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:47
     */
    public static JsonElement convertStringToJsonElement(String value) {
        try {
            if (null == value || "".equals(value)) {
                return null;
            }
            JsonParser parser = new JsonParser();
            JsonElement jObject = parser.parse(value);
            return jObject;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Convert string to json json object.
     *
     * @param value the value
     * @return the json object
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:47
     */
    public static JsonElement convertStringToJsonElement(Object value) {
        try {
            String valueStr = DataSwitch.convertObjectToString(value);
            if (null == valueStr || "".equals(valueStr)) {
                return null;
            }
            JsonParser parser = new JsonParser();
            JsonElement jObject = parser.parse(valueStr);
            return jObject;
        } catch (Exception e) {
            log.info("value:");
            throw e;
        }
    }

    /**
     * Convert object to json json object.
     *
     * @param obj              the obj
     * @param isSerializeNulls the is serialize nulls
     * @return the json object
     * @author ErebusST
     * @since 2022 -01-07 15:34:47
     */
    public static JsonObject convertObjectToJsonObject(Object obj, boolean isSerializeNulls) {
        try {
            JsonElement jsonElement = convertObjectToJsonElement(obj, isSerializeNulls);
            return jsonElement == null ? null : jsonElement.getAsJsonObject();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert object to json element json element.
     *
     * @param value the value
     * @return the json element
     * @author ErebusST
     * @since 2022 -01-07 15:34:47
     */
    public static JsonElement convertObjectToJsonElement(Object value) {
        try {
            JsonElement jsonElement = convertObjectToJsonElement(value, true);
            return jsonElement;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert object to json element json element.
     *
     * @param value            the value
     * @param isSerializeNulls the is serialize nulls
     * @return the json element
     * @author ErebusST
     * @since 2022 -01-07 15:34:47
     */
    public static JsonElement convertObjectToJsonElement(Object value, boolean isSerializeNulls) {
        try {
            if (value == null) {
                return null;
            }
            Gson gson = getGsonInstance(isSerializeNulls);
            JsonElement jsonElement = gson.toJsonTree(value);
            return jsonElement.getClass().equals(JsonNull.class) ? null : jsonElement;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert object to json string string.
     *
     * @param value the value
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:34:47
     */
    public static String convertObjectToJsonString(Object value) {
        Gson gson = getGsonInstance();
        return gson.toJson(value);
    }


    /**
     * Convert json array to list list.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param array the array
     * @return the list
     * @author ErebusST
     * @since 2022 -01-12 12:45:55
     */
    public static <T> List<T> convertJsonArrayToListEntity(Class<T> clazz, @Nonnull JsonArray array) {
        if (array.size() == 0) {
            return new ArrayList<>(0);
        }
        List<T> list = StreamSupport.stream(array.spliterator(), true)
                .map(element -> {
                    if (element.isJsonObject()) {
                        T t = convertJsonObjectToEntity(element.getAsJsonObject(), clazz);
                        return t;
                    } else {
                        return (T) getDefaultValue(element.getAsJsonPrimitive().getAsString(), clazz);
                    }

                })
                .collect(Collectors.toList());
        //List<T> list = getGsonInstance().fromJson(array, new TypeToken<List<T>>() {
        //}.getType());

        return list;

    }


    /**
     * Convert json array to list entity list.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param array the array
     * @return the list
     * @author ErebusST
     * @since 2022 -01-12 12:48:34
     */
    public static <T> List<T> convertJsonArrayToListEntity(Class<T> clazz, @Nonnull String array) {
        if (ObjectUtils.isEmpty(array)) {
            return new ArrayList<>(0);
        }
        JsonArray jsonArray = DataSwitch.convertStringToJsonArray(array);
        return convertJsonArrayToListEntity(clazz, jsonArray);
    }


    /**
     * Convert list t to json object json array.
     *
     * @param <T>  the type parameter
     * @param list the list
     * @return the json array
     * @author ErebusST
     * @since 2022 -01-07 15:34:37
     */
    public static <T> JsonArray convertObjectToJsonArray(List<T> list) {
        try {
            JsonElement element = convertObjectToJsonElement(list);
            return element.getAsJsonArray();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert entity to json object json object.
     * <p>
     * 该方法中 字符串 如果是json 则转换成 json对象 如果是 字符串 则正常按照字符串处理
     *
     * @param <Entity> the type parameter
     * @param clazz    the clazz
     * @param entity   the entity
     * @return the json object
     * @author ErebusST
     * @since 2022 -01-17 16:52:43
     */
    @Deprecated
    public static <Entity> JsonObject convertEntityToJsonObject(Class<Entity> clazz, Entity entity) {
        if (ObjectUtils.isNull(entity)) {
            return null;
        }
        JsonObject result = new JsonObject();
        Field[] fields = ReflectionUtils.getFields(clazz);
        Arrays.stream(fields)
                .forEach(field -> {
                    String name = field.getName();
                    Object fieldValue = ReflectionUtils.getFieldValue(entity, name);
                    if (ObjectUtils.isNotEmpty(fieldValue)) {
                        Class<?> type = field.getType();
                        JsonElement element;
                        if (String.class.equals(type)) {
                            element = convertStringToJsonElement(fieldValue);
                        } else {
                            element = convertObjectToJsonElement(fieldValue);
                        }
                        result.add(name, element);
                    }
                });
        return result;
    }

    /**
     * Merge json object json object.
     *
     * @param objects the objects
     * @return the json object
     * @author ErebusST
     * @since 2022 -01-17 17:05:02
     */
    public static JsonObject mergeJsonObject(JsonObject... objects) {
        return mergeJsonObject(false, objects);
    }

    /**
     * Merge json object json object.
     *
     * @param cover   the cover
     * @param objects the objects
     * @return the json object
     * @author ErebusST
     * @since 2022 -01-17 17:05:00
     */
    public static JsonObject mergeJsonObject(Boolean cover, JsonObject... objects) {
        JsonObject result = new JsonObject();
        Arrays.stream(objects)
                .filter(ObjectUtils::isNotNull)
                .forEach(object -> {
                    object.entrySet()
                            .stream()
                            .forEach(entry -> {
                                String key = entry.getKey();
                                JsonElement value = entry.getValue();
                                if (cover && !result.has(key)) {
                                    result.add(key, value);
                                } else {
                                    result.add(key, value);
                                }


                            });
                });
        return result;
    }

    /**
     * Convert json object to map map.
     *
     * @param object the object
     * @return the map
     * @author ErebusST
     * @since 2023 -01-05 10:58:28
     */
    public static Map<String, Object> convertJsonObjectToMap(JsonObject object) {
        return object.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    //endregion


    /**
     * 生成UUID
     *
     * @return the uuid
     * @author ErebusST
     * @since 2022 -01-07 15:34:48
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Convert list map to list entity list.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param data  the data
     * @return the list
     * @author ErebusST
     * @since 2022 -01-07 15:34:48
     */
    public static <T> List<T> convertListMapToListEntity(Class<T> clazz, List<Map<String, Object>> data) {
        return data.stream()
                .map(row -> {
                    T t = DataSwitch.convertMapObjToEntity(clazz, row);
                    return t;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    /**
     * 将ajax url中文解码
     *
     * @param strValue the str value
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:48
     */
    public String httpUrlDecodeUTF8(String strValue) throws UnsupportedEncodingException {
        try {
            return URLDecoder.decode(strValue, "utf-8");
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets random num.
     *
     * @param min the min
     * @param max the max
     * @return the random num
     * @author ErebusST
     * @since 2022 -01-07 15:34:48
     */
    public static int getRandomNum(int min, int max) {
        int random = new Random(System.nanoTime()).nextInt(max) % (max - min + 1) + min;
        return random;
    }

    /**
     * 将json格式的字符串转换成目标实体
     *
     * @param <T>   the type parameter
     * @param json  ：json格式的字符串
     * @param clazz ：实体
     * @return the t
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     * @author ErebusST
     * @since 2022 -01-07 15:34:48
     */
    public static <T> T convertJsonToEntity(JsonObject json, Class<T> clazz) {
        try {
            Gson gson = getGsonInstance();
            T entity = gson.fromJson(json, clazz);
            return entity;
        } catch (Exception e) {
            throw e;
        }
    }

    private static String[] chars = new String[]{
            "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"
    };

    /**
     * 获取八位随机数
     *
     * @return the string
     * @author ErebusST
     * @author：赵亮
     * @date：2020-07-27 14 :13
     * @since 2022 -01-07 15:34:48
     */
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }


}
