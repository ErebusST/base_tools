/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.situ.tools.StringUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The type Jdbc source.
 *
 * @author 司徒彬
 * @date 2021 /12/20 15:43
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class JdbcSource {

    private List<Source> sources;

    /**
     * The type Source.
     */
    @Data
    public static class Source {
        /**
         * The Key.
         */
        String key;
        /**
         * The Url.
         */
        String url;

        String schema;

        public String getSchema() {
            if (StringUtils.isEmpty(schema)) {
                this.schema = this.key;
            }
            return this.schema;
        }

        /**
         * The Username.
         */
        String username;
        /**
         * The Password.
         */
        String password;

        DruidDataSource druidDataSource;
    }

}
