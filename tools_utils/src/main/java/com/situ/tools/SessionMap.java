/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.situ.entity.bo.SessionEntity;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存数据存储Map
 *
 * @author 司徒彬
 * @date 2017 -04-11 22:00
 */
@Slf4j
public class SessionMap {

    private static final Map<String, SessionEntity> PARA_MAP = new ConcurrentHashMap<>();


    /**
     * Gets keys.
     *
     * @return the keys
     */
    public static Set<String> getKeys() {
        return PARA_MAP.keySet();
    }

    /**
     * Get object.
     *
     * @param key the key
     * @return the object
     * @throws Exception the exception
     */
    public static Object get(@Nonnull String key) {
        return get(key, true);
    }

    /**
     * Get object.
     *
     * @param key   the key
     * @param flush the flush
     * @return the object
     */
    public static Object get(@Nonnull String key, boolean flush) {
        SessionEntity sessionEntity = getSessionEntity(key);
        if (sessionEntity != null) {
            Object object = sessionEntity.getValue();
            if (flush && sessionEntity.getDeleteWhenExpire()) {
                long expire = sessionEntity.getExpire();
                //刷新初始化时间
                put(key, object, expire);
            }

            Object result = BeanUtils.deepClone(object);
            return result;
        } else {
            return null;
        }
    }

    /**
     * Gets session entity.
     *
     * @param key the key
     * @return the session entity
     */
    public static SessionEntity getSessionEntity(@Nonnull String key) {

        if (containsKey(key)) {
            SessionEntity sessionEntity = PARA_MAP.get(key);
            return sessionEntity;
        } else {
            return null;
        }
    }

    /**
     * Gets string.
     *
     * @param key the key
     * @return the string
     * @throws Exception the exception
     */
    public static String getString(@Nonnull String key) {
        Object value = get(key);
        return DataSwitch.convertObjectToString(value);
    }


    /**
     * Gets value.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the value
     * @throws Exception the exception
     */
    public static <T> T getValue(@Nonnull String key) {
        Object value = get(key);
        return value == null ? null : (T) value;
    }


    /**
     * Remove user info boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
    public static boolean removeUserInfo(@Nonnull Long userId) {
        String key = userId + "_login";
        remove(key);
        return true;
    }


    /**
     * Put.
     *
     * @param key   the key
     * @param value the value
     */
    public static void put(@Nonnull String key, Object value) {
        put(key, value, 0L);

    }

    /**
     * Put.
     *
     * @param key    the key
     * @param value  the value
     * @param expire the  expire
     */
    public static void put(@Nonnull String key, Object value, long expire) {
        Object result = BeanUtils.deepClone(value);
        if (key != null && value != null) {
            SessionEntity sessionEntity = SessionEntity.getInstance(result, expire);
            PARA_MAP.put(key, sessionEntity);
        }
    }

    /**
     * Contains key boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public static boolean containsKey(@Nonnull String key) {
        return PARA_MAP.keySet().stream().filter(str -> str.equalsIgnoreCase(key)).count() > 0;
    }

    /**
     * Remove.
     *
     * @param key the key
     */
    public static void remove(@Nonnull String key) {
        SessionEntity sessionEntity = PARA_MAP.get(key);
        if (ObjectUtils.isNull(sessionEntity)) {
            return;
        }
        if (key.endsWith("_login")) {
            if (ObjectUtils.isNull(sessionEntity.getCurrentTime())) {
                sessionEntity.setCurrentTime(System.currentTimeMillis());
            }
            boolean isOverTime = sessionEntity.getCurrentTime() - sessionEntity.getTimeStamp()
                    > sessionEntity.getExpire() * 1000;
            String userInfo = DataSwitch.convertObjectToJsonString(sessionEntity);
            log.error(StringUtils.concat("删除了用户信息:", userInfo, "isOverTime:", isOverTime));
        }
        PARA_MAP.remove(key);
    }

    /**
     * Check is expire boolean.
     *
     * @param key the key
     * @return the boolean
     */
    private static boolean checkIsExpire(@Nonnull String key) {
        long now = System.currentTimeMillis();
        if (PARA_MAP.containsKey(key)) {
            SessionEntity sessionEntity = PARA_MAP.get(key);
            if (ObjectUtils.isNotNull(sessionEntity) && sessionEntity.getDeleteWhenExpire()) {
                boolean isOverTime = now - sessionEntity.getTimeStamp() > sessionEntity.getExpire() * 1000;
                return isOverTime;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Gets OVERTIME keys.
     *
     * @return the OVERTIME keys
     */
    public static void clearOverTimeSession() {
        PARA_MAP.keySet().stream().filter(SessionMap::checkIsExpire).forEach(SessionMap::remove);
    }
}




