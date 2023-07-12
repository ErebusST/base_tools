/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import ch.hsr.geohash.GeoHash;
import com.situ.tools.GisUtils;
import com.situ.tools.NumberUtils;
import com.situ.tools.ObjectUtils;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author 司徒彬
 * @date 2022/11/16 18:08
 */
@Getter
@Setter
public class GeoEntity {


    public static GeoEntity getInstance(GeoHash geohash, Integer row, Integer col, Polygon current, Polygon parent) {
        GeoEntity entity = new GeoEntity();
        entity.setGeohash(geohash);
        entity.setRowIndex(row);
        entity.setColumnIndex(col);

        try {
            boolean contains = parent.contains(current);
            Geometry intersection = parent.intersection(current);

            BigDecimal area1 = GisUtils.calcArea(intersection);
            BigDecimal area2 = GisUtils.calcArea(current);

            BigDecimal percent = NumberUtils.divide(area1, area2);
            entity.setContains(contains);
            entity.setPercent(percent);
        } catch (Exception ex) {
            throw ex;
        }
        return entity;
    }

    public static GeoEntity getInstance(GeoHash geohash, Integer row, Integer col, Boolean contains, BigDecimal percent) {
        GeoEntity entity = new GeoEntity();
        entity.setGeohash(geohash);
        entity.setRowIndex(row);
        entity.setColumnIndex(col);
        entity.setContains(contains);
        entity.setPercent(percent);
        return entity;
    }

    private GeoHash geohash;
    private Integer rowIndex;
    private Integer columnIndex;
    private BigDecimal percent;
    private Boolean contains;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoEntity entity = (GeoEntity) o;
        return ObjectUtils.equals(this.geohash.toBase32(), entity.getGeohash().toBase32());
    }

    @Override
    public int hashCode() {
        return Objects.hash(geohash.toBase32());
    }
}
