/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.situ.config.DruidDBConfig;
import com.situ.config.JdbcSource;
import com.situ.tools.ObjectUtils;
import com.situ.tools.ReflectionUtils;
import com.situ.tools.StringUtils;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 数据库连接池配置
 *
 * @author 司徒彬
 * @date 2016-12-28 10:50
 */
@Component
@Configuration
public class DruidPool {

    public static final String DEFAULT = "489a0aac-52d2-4f8b-8e8c-a6e1deb97d4f";
    public static final Integer DEFAULT_SIZE = 10;
    public static final Map<String, DruidDataSource> DATA_SOURCE_SETTING = new HashMap<>(DEFAULT_SIZE);
    @Getter
    private List<String> keys = new ArrayList<>(DEFAULT_SIZE);

    @PostConstruct
    public void initDataSource() throws Exception {
        List<JdbcSource.Source> sources = config.getSource().getSources();

        Field[] fields = ReflectionUtils.getFields(DruidDBConfig.class);
        Map<String, Object> setting = Arrays.stream(fields)
                .filter(field -> !StringUtils.equalsIgnoreCase(field.getName(), "sources"))
                .filter(field -> !StringUtils.equalsIgnoreCase(field.getName(), "dbUrl"))
                .filter(field -> !StringUtils.equalsIgnoreCase(field.getName(), "username"))
                .filter(field -> !StringUtils.equalsIgnoreCase(field.getName(), "password"))
                .map(field -> {
                    String name = field.getName();
                    Object value = ReflectionUtils.getFieldValue(config, name);
                    if (ObjectUtils.isNotNull(value) && !String.class.equals(value.getClass())) {
                        value = value.toString();
                    }
                    return Pair.of(name, value);
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        if (ObjectUtils.isNotNull(sources) && sources.size() > 0) {
            for (JdbcSource.Source source : sources) {
                String key = source.getKey();
                String url = source.getUrl();
                String username = source.getUsername();
                String password = source.getPassword();
                setting.put("url", url);
                setting.put("username", username);
                setting.put("password", password);
                DruidDataSource dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(setting);
                keys.add(key);
                DATA_SOURCE_SETTING.put(key, dataSource);
            }
        } else {
            String url = config.getDbUrl();
            String username = config.getUsername();
            String password = config.getPassword();
            if (StringUtils.equalsIgnoreCase(url, "null") || StringUtils.equalsIgnoreCase(username, "null") || StringUtils.equalsIgnoreCase(password, "null")) {
                throw new Exception("必须至少配置一类数据源");
            }
            setting.put("url", url);
            setting.put("username", username);
            setting.put("password", password);
            DruidDataSource dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(setting);
            keys.add(DEFAULT);
            DATA_SOURCE_SETTING.put(DEFAULT, dataSource);
        }
    }

    @Autowired
    DruidDBConfig config;


    /**
     * dialect=org.hibernate.dialect.MySQL5Dialect
     * driverClassName=com.mysql.jdbc.Driver
     * url=jdbc:mysql://180.76.120.27:3316/hms?characterEncoding=utf8&autoReconnect=true&useSSL=false
     * username=root
     * password=yysp@2017
     * auto=none
     * showSql=true
     * formatSql=true
     * initialSize=1
     * maxActive=2
     * maxWait=60000
     * timeBetweenEvictionRunsMillis=60000
     * minEvictableIdleTimeMillis=300000
     * validationQuery=SELECT 1
     * testWhileIdle=true
     * testOnBorrow=false
     * testOnReturn=false
     * poolPreparedStatements=true
     * maxPoolPreparedStatementPerConnectionSize=200
     * filters=stat
     */


    public DruidPooledConnection getConnection() throws Exception {
        DruidDataSource dds = DATA_SOURCE_SETTING.values().stream().findFirst().get();
        return dds.getConnection();
    }


    @Bean(name = "sessionFactory")
    public SessionFactory sessionFactory() {
        DruidDataSource dataSource = DATA_SOURCE_SETTING.values().stream().findFirst().get();
        LocalSessionFactoryBuilder localSessionFactoryBuilder = new LocalSessionFactoryBuilder(dataSource);
        SessionFactory sessionFactory = localSessionFactoryBuilder.buildSessionFactory();
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(){
        HibernateTransactionManager manager = new HibernateTransactionManager(sessionFactory());
        return manager;
    }
}
