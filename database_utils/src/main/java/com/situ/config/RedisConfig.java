/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The type Redis config.
 *
 * @author 司徒彬
 * @date 2021 /9/22 21:30
 */
@Component
@Getter
@Setter
public class RedisConfig {
    @Value("${redis.redis_ip}")
    private String redis_ip;
    @Value("${redis.redis_port}")
    private String redis_port;
    @Value("${redis.auth}")
    private String redis_auth;
    @Value("${redis.pool.maxTotal}")
    private String redis_pool_maxTotal;
    @Value("${redis.pool.maxIdle}")
    private String redis_pool_maxIdle;
    @Value("${redis.pool.testOnBorrow}")
    private String redis_pool_testOnBorrow;
    @Value("${redis.pool.testOnReturn}")
    private String redis_pool_testOnReturn;
}
