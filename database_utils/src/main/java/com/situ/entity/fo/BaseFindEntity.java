/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.fo;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 查询基础类
 *
 * @author 司徒彬
 * <p>
 * 2017年1月11日18:08:13
 */
@Getter
@Setter
public class BaseFindEntity implements Serializable {
    private Long userId;
    private String sortField; // 排序字段
    private String sortType; // 排序方式

    private Integer pageSize;//自定义封装分页控件  每页记录数
    private Integer pageNumber;//自定义封装分页控件  当前页码

    private String searchContent;
    private String beginTime;
    private String endTime;

    private String operationUser;

    //private String state;


    public String getSearchContent() {
        return searchContent;
    }

    public String getLikeSearchContent() {
        searchContent = searchContent == null ? searchContent = "" : searchContent;
        return "%" + searchContent + "%";
    }

    private String state;


}
