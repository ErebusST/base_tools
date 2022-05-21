/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import com.situ.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 司徒彬
 * @date 2022/5/21 18:42
 */
@Setter
@Getter
public class TableSetting {
    private String schema;
    private String table;
    private JdbcField primaryKey;
    boolean autoIncrement;
    List<JdbcField> fields;

    public String getKey() {
        return StringUtils.concat(schema, "^", table);
    }
}
