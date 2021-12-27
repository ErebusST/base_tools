/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 司徒彬
 * @date 2019-12-09 15:16
 */
@Getter
@Setter
public class Point {

    public Point() {

    }

    public static Point get(Object x, Object y) {
        return new Point(x, y);
    }

    public Point(@Nonnull Object x,@Nonnull Object y) {
        this.lng =new BigDecimal(x.toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
        this.lat =new BigDecimal(y.toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
    }


    public Point(Double x, Double y) {
        this.lng = BigDecimal.valueOf(x).setScale(6, BigDecimal.ROUND_HALF_UP);
        this.lat = BigDecimal.valueOf(y).setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    public Point(String x, String y) {
        this.lng = new BigDecimal(x).setScale(6, BigDecimal.ROUND_HALF_UP);
        this.lat = new BigDecimal(y).setScale(6, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 经度 X
     */
    private BigDecimal lng;
    /**
     * 纬度 Y
     */
    private BigDecimal lat;

    private Integer value = 0;
    private Double distance;

    List<Point> points;


    String id;

    String type;
    private boolean exist = true;


    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        object.addProperty("lng", lng);
        object.addProperty("lat", lat);
        return object.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Point that = (Point) obj;
        return (this.lng.floatValue() + " " + this.lat.floatValue())
                .equalsIgnoreCase(that.getLng().floatValue() + " " + that.getLat().floatValue());
    }

    public JsonArray toArray() {
        JsonArray array = new JsonArray(2);
        array.add(lng);
        array.add(lat);
        return array;
    }

}
