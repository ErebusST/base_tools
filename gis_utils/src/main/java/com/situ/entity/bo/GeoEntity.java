/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author 司徒彬
 * @date 2022/11/16 18:08
 */
@Getter
@Setter
public class GeoEntity {
    private String geoHash;
    private Integer rowIndex;
    private Integer columnIndex;
    private BigDecimal percent;
    private Boolean crossing;
}
