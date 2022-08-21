/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.situ.config.RedisConfig;
import com.situ.tools.DataSwitch;
import com.situ.tools.ListUtils;
import com.situ.tools.ObjectUtils;
import com.situ.tools.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.*;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Redis工具类
 *
 * @author 司徒彬
 * @date 2019 -04-02 13:23
 */
@Repository
@Slf4j
public class RedisHelper {

    //默认缓存时间
    private final int EXPIRE = 60000;


    private static RedisHelper instance;

    private static JedisPool jedisPool;

    private static ReentrantLock lock = new ReentrantLock();


    /**
     * Gets instance.
     *
     * @return the instance
     * @author ErebusST
     * @since 2022 -01-07 15:39:05
     */
    public synchronized static RedisHelper getInstance() {
        if (instance == null) {
            instance = new RedisHelper();
        }
        return instance;
    }


    private static RedisConfig redisConfig;

    @Autowired
    private RedisConfig redisConfigTemp;

    /**
     * Before init .
     *
     * @author ErebusST
     * @since 2022 -01-07 15:39:05
     */
    @PostConstruct
    public void beforeInit() {
        redisConfig = redisConfigTemp;
    }

//    @Autowired
//    private Environment environment;

    /**
     * redis.ip=
     * redis.port=
     * redis.auth=
     * redis.pool.maxIdle=1
     * redis.pool.maxTotal=300
     * redis.pool.testOnBorrow=true
     * redis.pool.testOnReturn=true
     */

    /**
     * 初始化JedisPool
     */
    private void initPool() {
        try {
            if (ObjectUtils.isNull(redisConfig)) {
                redisConfig = redisConfigTemp;
            }
            String redis_ip = redisConfig.getRedis_ip();

            String redis_port = redisConfig.getRedis_port();

            String redis_auth = redisConfig.getRedis_auth();

            Integer redis_pool_maxTotal = redisConfig.getRedis_pool_maxTotal();

            Integer redis_pool_maxIdle = redisConfig.getRedis_pool_maxIdle();

            Boolean redis_pool_testOnBorrow = redisConfig.getRedis_pool_testOnBorrow();

            Boolean redis_pool_testOnReturn = redisConfig.getRedis_pool_testOnReturn();

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(redis_pool_maxIdle);
            config.setMaxTotal(redis_pool_maxTotal);
            config.setMinIdle(redis_pool_maxIdle / 2);
            config.setTestOnCreate(true);
            config.setTestOnBorrow(redis_pool_testOnBorrow);
            config.setTestOnReturn(redis_pool_testOnReturn);

            //是否在空闲资源监测时通过ping命令监测连接有效性，无效连接将被销毁。
            config.setTestWhileIdle(true);
            //空闲资源的检测周期
            config.setTimeBetweenEvictionRunsMillis(30 * 1000L);
            //当资源池连接用尽后，调用者的最大等待时间（单位为毫秒
            config.setMaxWaitMillis(5000L);
            config.setJmxEnabled(true);
            log.info("redis.port 2:" + redis_port);
            jedisPool = new JedisPool(
                    config,
                    redis_ip,
                    Integer.valueOf(redis_port), 5000, redis_auth);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 通用方法：从JedisPool中获取Jedis
     *
     * @return client
     * @author ErebusST
     * @since 2022 -07-27 18:25:51
     */
    public synchronized Jedis getClient() {
        if (jedisPool == null) {
            initPool();
            log.info("JedisPool init success！");
        }
        Jedis resource = jedisPool.getResource();
        return resource;
    }

    private void execute(Callable callable) {
        Jedis client = null;
        try {
            client = getClient();
        } finally {
            close(client);
        }
    }

    /**
     * Exec.
     *
     * @param callables the callables
     * @author ErebusST
     * @since 2022 -01-07 15:39:05
     */
    public void exec(Callable... callables) {

        Arrays.stream(callables).forEach(callable -> {
            //callable.call();
        });
    }


    /**
     * 通用方法：释放Jedis
     *
     * @param client
     */
    private void close(Jedis client) {
        if (ObjectUtils.isNotNull(client)) {
            client.close();
        }
    }

    /**
     * Put string string.
     *
     * @param key   the key
     * @param value the value
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:39:05
     */
    public String putString(@Nonnull String key, @Nonnull String value) {
        try {
            return putString(key, value, null, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Put string string.
     *
     * @param key     the key
     * @param value   the value
     * @param seconds the seconds
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:39:05
     */
    public String putString(@Nonnull String key, @Nonnull String value, Integer seconds) {
        return putString(key, value, seconds, null);
    }


    /**
     * Put string string.
     *
     * @param key     the key
     * @param value   the value
     * @param seconds the seconds
     * @param dbIndex the db index
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:39:05
     */
    public String putString(@Nonnull String key, @Nonnull String value, Integer seconds, Integer dbIndex) {
        Jedis client = null;
        try {
            if (ObjectUtils.isNull(value)) {
                return "";
            }
            client = getClient();
            if (ObjectUtils.isNotNull(dbIndex)) {
                client.select(dbIndex);
            }


            if (ObjectUtils.isNull(seconds)) {
                return client.set(key, value);
            } else {
                return client.setex(key, seconds, value);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }
    }


    /**
     * Put object string.
     *
     * @param key   the key
     * @param value the value
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:39:05
     */
    public String putObject(@Nonnull String key, @Nonnull Object value) {
        try {
            return putObject(key, value, null);
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Put object string.
     *
     * @param key     the key
     * @param value   the value
     * @param seconds the seconds
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:39:05
     */
    public String putObject(@Nonnull String key, @Nonnull Object value, Integer seconds) {
        try {
            return putObject(key, value, seconds, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Put object string.
     *
     * @param key     the key
     * @param value   the value
     * @param seconds the seconds
     * @param dbIndex the db index
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:39:05
     */
    public String putObject(@Nonnull String key, @Nonnull Object value, Integer seconds, Integer dbIndex) {
        try {
            Class<?> clazz = value.getClass();
            if (clazz.equals(String.class)
                    || clazz.equals(Integer.class)
                    || clazz.equals(BigDecimal.class)
                    || clazz.equals(Timestamp.class)
                    || clazz.equals(Long.class)) {
                value = value.toString();
            } else {
                value = DataSwitch.convertObjectToJsonElement(value);
            }
            return putString(key, value.toString(), seconds, dbIndex);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public <T> List<T> getEntities(@Nonnull Class<T> clazz, @Nonnull String key) {
        return getEntities(clazz, key, null);
    }

    /**
     * Get entities list.
     *
     * @param <T>     the type parameter
     * @param clazz   the clazz
     * @param key     the key
     * @param dbIndex the db index
     * @return the list
     * @author ErebusST
     * @since 2022 -08-12 13:55:48
     */
    public <T> List<T> getEntities(@Nonnull Class<T> clazz, @Nonnull String key, Integer dbIndex) {
        JsonArray jsonArray = getJsonArray(key, dbIndex);
        return DataSwitch.convertJsonArrayToListEntity(clazz, jsonArray);
    }

    /**
     * Gets entity.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param key   the key
     * @return the entity
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public <T> T getEntity(@Nonnull Class<T> clazz, @Nonnull String key) {
        return getEntity(clazz, key, null);
    }

    /**
     * Gets entity.
     *
     * @param <T>     the type parameter
     * @param clazz   the clazz
     * @param key     the key
     * @param dbIndex the db index
     * @return the entity
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public <T> T getEntity(@Nonnull Class<T> clazz, @Nonnull String key, Integer dbIndex) {
        String string = getString(key, dbIndex);
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        JsonObject jsonObject = DataSwitch.convertStringToJsonObject(string);
        return DataSwitch.convertJsonObjectToEntity(jsonObject, clazz);
    }


    /**
     * Gets json object.
     *
     * @param key the key
     * @return the json object
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public JsonObject getJsonObject(@Nonnull String key) {
        return getJsonObject(key, null);
    }

    /**
     * Get json object json object.
     *
     * @param key     the key
     * @param dbIndex the db index
     * @return the json object
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public JsonObject getJsonObject(@Nonnull String key, Integer dbIndex) {
        return getJsonElement(key, dbIndex).getAsJsonObject();
    }

    /**
     * Gets json array.
     *
     * @param key the key
     * @return the json array
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public JsonArray getJsonArray(@Nonnull String key) {
        return getJsonArray(key, null);
    }

    /**
     * Gets json array.
     *
     * @param key     the key
     * @param dbIndex the db index
     * @return the json array
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public JsonArray getJsonArray(@Nonnull String key, Integer dbIndex) {
        JsonElement jsonElement = getJsonElement(key, dbIndex);
        if (ObjectUtils.isNull(jsonElement)) {
            return null;
        } else {
            return jsonElement.getAsJsonArray();
        }
    }

    /**
     * Gets json element.
     *
     * @param key the key
     * @return the json element
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public JsonElement getJsonElement(@Nonnull String key) {
        return getJsonElement(key, null);
    }

    /**
     * Gets json element.
     *
     * @param key     the key
     * @param dbIndex the db index
     * @return the json element
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public JsonElement getJsonElement(@Nonnull String key, Integer dbIndex) {
        String string = getString(key, dbIndex);
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        return DataSwitch.convertStringToJsonElement(string);
    }

    /**
     * Gets string.
     *
     * @param key the key
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public String getString(@Nonnull String key) {
        try {
            return getString(key, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets string.
     *
     * @param key     the key
     * @param dbIndex the db index
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public String getString(@Nonnull String key, Integer dbIndex) {
        Jedis client = null;
        try {
            if (ObjectUtils.isEmpty(key)) {
                return "";
            }
            client = getClient();
            if (ObjectUtils.isNotNull(dbIndex)) {
                client.select(dbIndex);
            }
            if (!client.exists(key)) {
                return "";
            }
            String value = client.get(key);
            return value;
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }
    }


    /**
     * Remove long.
     *
     * @param keys the keys
     * @return the long
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public Long remove(String... keys) {
        try {
            return remove(null, keys);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Remove long.
     *
     * @param dbIndex the db index
     * @param keys    the keys
     * @return the long
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public Long remove(Integer dbIndex, String... keys) {
        Jedis client = null;
        try {
            client = getClient();
            if (ObjectUtils.isNotNull(dbIndex)) {
                client.select(dbIndex);
            }
            Long del = client.del(Arrays.stream(keys).collect(Collectors.toList()).toArray(new String[keys.length]));
            return del;
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }
    }

    /**
     * Gets result by pattern.
     *
     * @param pattern the pattern
     * @param dbIndex the db index
     * @return the result by pattern
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    @Deprecated
    public List<String> getResultByPattern(String pattern, Integer dbIndex) {
        Jedis client = null;
        try {

            client = getClient();
            client.select(dbIndex);

            ScanParams scanParams = new ScanParams();
            scanParams.match(pattern);
            ScanResult<String> scan = client.scan("0", scanParams);
            List<String> keys = scan.getResult();
            if (keys.size() == 0) {
                return ListUtils.newArrayList();
            } else {
                List<String> result = client.mget(keys.stream().collect(Collectors.toList())
                        .toArray(new String[keys.size()]));
                return result;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key the key
     * @return boolean boolean
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public boolean exists(String key) {
        return exists(key, 0);
    }

    /**
     * 判断key是否存在
     *
     * @param key       the key
     * @param dataIndex the data index
     * @return boolean boolean
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public boolean exists(String key, Integer dataIndex) {
        Jedis client = null;
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }
            client = getClient();
            client.select(dataIndex);
            boolean exists = client.exists(key);
            close(client);
            return exists;
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }

    }


    /**
     * Expire.
     *
     * @param dbIndex the db indexDaySalaryController
     * @param key     the key
     * @param seconds the seconds
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public void expire(int dbIndex, String key, int seconds) {
        Jedis client = null;
        try {
            client = getClient();
            client.select(dbIndex);
            client.expire(key, seconds);
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }
    }

    /**
     * Update object string.
     *
     * @param key   the key
     * @param value the value
     * @return the string
     * @author ErebusST
     * @since 2022 -04-26 19:42:08
     */
    public String updateObject(@Nonnull String key, @Nonnull Object value) {
        return updateObject(key, value, 0);
    }

    /**
     * Update object string.
     *
     * @param key     the key
     * @param value   the value
     * @param dbIndex the db index
     * @return the string
     * @author ErebusST
     * @since 2022 -04-26 19:41:22
     */
    public String updateObject(@Nonnull String key, @Nonnull Object value, Integer dbIndex) {
        String string = DataSwitch.convertObjectToJsonElement(value).toString();
        return updateString(key, string, dbIndex);
    }


    /**
     * Update string string.
     *
     * @param key   the key
     * @param value the value
     * @return the string
     * @author ErebusST
     * @since 2022 -04-26 19:39:46
     */
    public String updateString(@Nonnull String key, @Nonnull String value) {
        return updateString(key, value, 0);
    }

    /**
     * Update string string.
     *
     * @param key     the key
     * @param value   the value
     * @param dbIndex the db index
     * @return the string
     * @author ErebusST
     * @since 2022 -04-26 19:39:43
     */
    public String updateString(@Nonnull String key, @Nonnull String value, Integer dbIndex) {
        Jedis client = null;
        try {
            client = getClient();
            client.select(dbIndex);
            Long expire = client.ttl(key);
            if (ObjectUtils.isNotNull(expire) && expire > 0L) {
                return client.setex(key, expire.intValue(), value);
            } else {
                return client.set(key, value);
            }

        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }
    }

    /**
     * Incr long.
     *
     * @param key the key
     * @return the long
     * @author ErebusST
     * @since 2022 -05-20 15:35:06
     */
    public Long incr(@Nonnull String key) {
        return incr(key, 0);
    }

    /**
     * Incr long.
     *
     * @param key     the key
     * @param dbIndex the db index
     * @return the long
     * @author ErebusST
     * @since 2022 -05-20 15:34:09
     */
    public Long incr(@Nonnull String key, Integer dbIndex) {
        return incr(key, dbIndex, null);
    }

    /**
     * Incr long.
     *
     * @param key     the key
     * @param dbIndex the db index
     * @param expire  the expire
     * @return the long
     * @author ErebusST
     * @since 2022 -07-29 09:44:08
     */
    public Long incr(@Nonnull String key, Integer dbIndex, Integer expire) {
        return incr(key, dbIndex, null, expire);
    }

    /**
     * Incr long.
     *
     * @param key       the key
     * @param dbIndex   the db index
     * @param increment the increment
     * @param expire    the expire
     * @return the long
     * @author ErebusST
     * @since 2022 -08-19 11:14:14
     */
    public Long incr(@Nonnull String key, Integer dbIndex, Long increment, Integer expire) {
        Jedis client = null;
        try {
            client = getClient();
            client.select(dbIndex);
            Long incr;
            if (ObjectUtils.isNotNull(increment)) {
                incr = client.incrBy(key, increment);
            } else {
                incr = client.incr(key);
            }
            if (ObjectUtils.isNotNull(expire)) {
                client.expire(key, expire);
            }
            return incr;
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }
    }


    /**
     * Decr long.
     *
     * @param key the key
     * @return the long
     * @author ErebusST
     * @since 2022 -05-20 16:53:55
     */
    public Long decr(@Nonnull String key) {
        return decr(key, 0);
    }

    /**
     * Decr long.
     *
     * @param key     the key
     * @param dbIndex the db index
     * @return the long
     * @author ErebusST
     * @since 2022 -05-20 16:53:56
     */
    public Long decr(@Nonnull String key, Integer dbIndex) {
        return decr(key, dbIndex, null);
    }

    /**
     * Incr long.
     *
     * @param key     the key
     * @param dbIndex the db index
     * @param expire  the expire
     * @return the long
     * @author ErebusST
     * @since 2022 -07-29 09:44:08
     */
    public Long decr(@Nonnull String key, Integer dbIndex, Integer expire) {
        return decr(key, dbIndex, null, expire);
    }

    /**
     * Decr long.
     *
     * @param key       the key
     * @param dbIndex   the db index
     * @param increment the increment
     * @param expire    the expire
     * @return the long
     * @author ErebusST
     * @since 2022 -08-19 11:17:21
     */
    public Long decr(@Nonnull String key, Integer dbIndex, Long increment, Integer expire) {
        Jedis client = null;
        try {
            client = getClient();
            client.select(dbIndex);
            Long incr;
            if (ObjectUtils.isNotNull(increment)) {
                incr = client.decrBy(key, increment);
            } else {
                incr = client.decr(key);
            }
            if (ObjectUtils.isNotNull(expire)) {
                client.expire(key, expire);
            }
            return incr;
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }
    }


    /**
     * Ttl long.
     *
     * @param key the key
     * @return the long
     * @author ErebusST
     * @since 2022 -05-20 15:39:28
     */
    public Long ttl(@Nonnull String key) {
        return ttl(key, 0);
    }

    /**
     * Ttl long.
     *
     * @param key     the key
     * @param dbIndex the db index
     * @return the long
     * @author ErebusST
     * @since 2022 -05-20 15:39:29
     */
    public Long ttl(@Nonnull String key, Integer dbIndex) {
        Jedis client = null;
        try {
            client = getClient();
            client.select(dbIndex);
            return client.ttl(key);
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(client);
        }
    }

    /**
     * Flush.
     *
     * @param dbIndex the db index
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public void flush(@Nonnull Integer dbIndex) {
        Jedis client = null;
        try {
            client = getClient();
            client.select(dbIndex);
            client.flushDB();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Flush all.
     *
     * @param dbIndex the db index
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public void flushAll(Integer... dbIndex) {
        Arrays.stream(dbIndex).filter(ObjectUtils::isNotEmpty).forEach(index -> {
            flush(index);
        });
    }

    /**
     * Get db size long.
     *
     * @param dbIndex the db index
     * @return the long
     * @author ErebusST
     * @since 2022 -01-07 15:39:06
     */
    public Long getDBSize(@Nonnull Integer dbIndex) {
        Jedis client = null;
        try {
            client = getClient();
            client.select(dbIndex);
            return client.dbSize();
        } catch (Exception ex) {
            throw ex;
        }
    }


}
