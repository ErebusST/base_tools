/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.config;

import com.situ.entity.bo.StatViewServlet;
import com.situ.entity.bo.WebStatFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 数据库配置
 *
 * @author 司徒彬
 * @date 2020 /6/21 11:06
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.datasource.pool")
public class DruidDBConfig {
    @Value("${spring.datasource.url:null}")
    private String url;
    @Value("${spring.datasource.username:null}")
    private String username;
    @Value("${spring.datasource.password:null}")
    private String password;

    private String driverClassName;

    private int initialSize;

    private int minIdle;

    private int maxActive;

    private int maxWait;

    private int timeBetweenEvictionRunsMillis;

    private int minEvictableIdleTimeMillis;

    private String validationQuery;

    private boolean testWhileIdle;

    private boolean testOnBorrow;

    private boolean testOnReturn;

    private boolean poolPreparedStatements;

    private int maxPoolPreparedStatementPerConnectionSize;

    private String filters;

    private String connectionProperties;

    private Boolean updateVersion = false;

    private boolean useGlobalDataSourceStat;

    private String dbType;

    StatViewServlet statViewServlet;

    WebStatFilter webStatFilter;


    //@Getter
    //@Setter
    //public class StatViewServlet {
    //    String loginName;
    //    String loginPassword;
    //    boolean resetEnable;
    //    String urlPattern;
    //    boolean enabled = false;
    //}

    //@Getter
    //@Setter
    //public class WebStatFilter {
    //    String urlPattern;
    //    String exclusions;
    //}

    /**
     * The Source.
     */
    @Autowired
    JdbcSource source;

}
