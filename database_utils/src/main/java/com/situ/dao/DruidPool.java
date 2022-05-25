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
import com.situ.entity.bo.StatViewServlet;
import com.situ.entity.bo.WebStatFilter;
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
 * @date 2016 -12-28 10:50
 */
@Component
@Configuration
public class DruidPool {

    /**
     * The constant DEFAULT.
     */
    public static final String DEFAULT = "489a0aac-52d2-4f8b-8e8c-a6e1deb97d4f";
    /**
     * The constant DEFAULT_SIZE.
     */
    public static final Integer DEFAULT_SIZE = 10;
    /**
     * The constant DATA_SOURCE_SETTING.
     */
    public static final Map<String, JdbcSource.Source> DATA_SOURCE_SETTING = new HashMap<>(DEFAULT_SIZE);
    @Getter
    private List<String> keys = new ArrayList<>(DEFAULT_SIZE);

    /**
     * Init data source .
     *
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:39:01
     */
    @PostConstruct
    public void initDataSource() throws Exception {
        List<JdbcSource.Source> sources = config.getSource().getSources();

        Field[] fields = ReflectionUtils.getFields(DruidDBConfig.class);
        Map<String, Object> setting = Arrays.stream(fields)
                .filter(field -> !StringUtils.equalsIgnoreCase(field.getName(), "sources"))
                .filter(field -> !StringUtils.equalsIgnoreCase(field.getName(), "url"))
                .filter(field -> !StringUtils.equalsIgnoreCase(field.getName(), "username"))
                .filter(field -> !StringUtils.equalsIgnoreCase(field.getName(), "password"))
                .map(field -> {
                    String name = field.getName();
                    Object value = ReflectionUtils.getFieldValue(config, name);
                    if (ObjectUtils.isEmpty(value)) {
                        return null;
                    }
                    Class<?> clazz = value.getClass();

                    if (StatViewServlet.class.equals(clazz)) {
                        StatViewServlet set = (StatViewServlet) value;
                        return Pair.of(name, set.toMap());
                    }
                    if (WebStatFilter.class.equals(clazz)) {
                        WebStatFilter set = (WebStatFilter) value;
                        return Pair.of(name, set.toMap());
                    }
                    if (ObjectUtils.isNotNull(value) && !String.class.equals(clazz)) {
                        value = value.toString();
                    }
                    return Pair.of(name, value);
                })
                .filter(ObjectUtils::isNotNull)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        if (ObjectUtils.isNotNull(sources) && sources.size() > 0) {
            for (JdbcSource.Source source : sources) {
                String key = source.getKey();
                String url = source.getUrl();
                String schema = source.getSchema();
                String username = source.getUsername();
                String password = source.getPassword();
                setting.put("url", url);
                setting.put("username", username);
                setting.put("password", password);
                DruidDataSource dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(setting);
                keys.add(key);
                source.setDruidDataSource(dataSource);
                DATA_SOURCE_SETTING.put(key, source);
            }

        } else {
            String url = config.getUrl();
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

            JdbcSource.Source source = new JdbcSource.Source();
            source.setKey(DEFAULT);
            source.setUrl(url);
            source.setSchema("");
            source.setUsername(username);
            source.setPassword(password);
            source.setDruidDataSource(dataSource);
            DATA_SOURCE_SETTING.put(DEFAULT, source);
        }
    }

    /**
     * The Config.
     */
    @Autowired
    DruidDBConfig config;


    /**
     * @return the connection
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:39:01
     */
    public DruidPooledConnection getConnection() throws Exception {
        DruidDataSource dds = DATA_SOURCE_SETTING.values().stream().findFirst().get().getDruidDataSource();
        return dds.getConnection();
    }


    /**
     * Session factory session factory.
     *
     * @return the session factory
     * @author ErebusST
     * @since 2022 -01-07 15:39:01
     */
    @Bean(name = "sessionFactory")
    public SessionFactory sessionFactory() {
        DruidDataSource dataSource = DATA_SOURCE_SETTING.values().stream().findFirst().get().getDruidDataSource();
        LocalSessionFactoryBuilder localSessionFactoryBuilder = new LocalSessionFactoryBuilder(dataSource);
        SessionFactory sessionFactory = localSessionFactoryBuilder.buildSessionFactory();
        return sessionFactory;
    }

    /**
     * Transaction manager hibernate transaction manager.
     *
     * @return the hibernate transaction manager
     * @author ErebusST
     * @since 2022 -01-07 15:39:01
     */
    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager manager = new HibernateTransactionManager(sessionFactory());
        return manager;
    }
}
