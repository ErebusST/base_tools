/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 分页功能类
 */
public class Pager implements Serializable {

    /**
     * @Fields pageNo : 页码
     */
    private Integer pageNumber;
    /**
     * @Fields pageSize : 每页记录条数
     */
    private Integer pageSize;
    /**
     * @Fields total : 总记录数
     */
    private Integer total;

    private List<Map<String, Object>> rows;

    /**
     * Gets page number.
     *
     * @return the page number
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets page number.
     *
     * @param pageNumber the page number
     */
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Gets page size.
     *
     * @return the page size
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Sets page size.
     *
     * @param pageSize the page size
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets total.
     *
     * @return the total
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * Sets total.
     *
     * @param total the total
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * Gets rows.
     *
     * @return the rows
     */
    public List<Map<String, Object>> getRows() {
        return rows;
    }

    /**
     * Sets rows.
     *
     * @param rows the rows
     */
    public void setRows(List<Map<String, Object>> rows) {

        if (rows.size() > 0) {
            rows.stream().forEach(row -> {
                row.entrySet().stream()
                        .filter(entry -> entry.getValue() != null)
                        .forEach(entry -> {
                            Object value = entry.getValue();
                            if(BigDecimal.class.equals(value.getClass())){
                                try {
                                    value = new BigDecimal(value.toString());
                                } catch (Exception ex) {
                                   value = null;
                                }
                            }
                            entry.setValue(value);
                        });
            });

        }
        this.rows = rows;
    }


}
