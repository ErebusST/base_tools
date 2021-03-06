/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
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
 * @author 司徒彬
 * @date 2022/6/9 18:34
 */
@Component
@Getter
@Setter
public class OssConfig {
    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.access_key_id}")
    private String accessKeyId;
    @Value("${oss.access_key_secret}")
    private String accessKeySecret;
    @Value("${oss.url:}")
    private String url;

    @Value("${oss.protocol:http}")
    private String protocol;
}
