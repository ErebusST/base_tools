/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.dao;


import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;


/**
 * 自定义的mysql方言
 *
 * @author 司徒彬
 * @date 2020 /3/12 18:32
 */
public class MySQLDialect extends MySQL57Dialect {

    /**
     * Instantiates a new My sql dialect.
     */
    public MySQLDialect() {
        super(); //调用父类的构造方法（super()一定要放在方法的首个语句）
        registerHibernateType(Types.NULL, Object.class.getName());
        registerHibernateType(Types.BIGINT, StandardBasicTypes.LONG.getName());
    }
}
