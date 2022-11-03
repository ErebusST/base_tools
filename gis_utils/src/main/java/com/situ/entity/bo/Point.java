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
import com.situ.tools.DataSwitch;
import com.situ.tools.ObjectUtils;
import com.situ.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * The type Point.
 *
 * @author 司徒彬
 * @date 2019 -12-09 15:16
 */
@Getter
@Setter
public class Point implements Serializable {

    /**
     * Instantiates a new Point.
     */
    public Point() {

    }

    /**
     * Get point.
     *
     * @param location the location
     * @return the point
     * @author ErebusST
     * @since 2022 -10-26 15:05:15
     */
    public static Point get(String location){
        List<String> strings = StringUtils.splitToList(location, ",");
        if(strings.size() == 2){
            return Point.get(strings.get(0), strings.get(1));
        }else {
            return null;
        }
    }

    /**
     * Get point.
     *
     * @param x the x
     * @param y the y
     * @return the point
     * @author ErebusST
     * @since 2022 -01-07 15:36:28
     */
    public static Point get(Object x, Object y) {
        if (ObjectUtils.isNull(x) || ObjectUtils.isNull(y)) {
            return null;
        }
        return new Point(x, y);
    }

    /**
     * Instantiates a new Point.
     *
     * @param x the x
     * @param y the y
     */
    public Point(@Nonnull Object x, @Nonnull Object y) {
        this.lng = new BigDecimal(x.toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
        this.lat = new BigDecimal(y.toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * Instantiates a new Point.
     *
     * @param x the x
     * @param y the y
     */
    public Point(Double x, Double y) {
        this.lng = BigDecimal.valueOf(x).setScale(6, BigDecimal.ROUND_HALF_UP);
        this.lat = BigDecimal.valueOf(y).setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Instantiates a new Point.
     *
     * @param x the x
     * @param y the y
     */
    public Point(String x, String y) {
        this.lng = new BigDecimal(x).setScale(6, BigDecimal.ROUND_HALF_UP);
        this.lat = new BigDecimal(y).setScale(6, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 经度 X
     */
    private BigDecimal lng;

    public void setLng(BigDecimal lng) {
        this.lng = DataSwitch.convertObjectToBigDecimal(lng,6);
    }

    public void setLat(BigDecimal lat) {
        this.lat = DataSwitch.convertObjectToBigDecimal(lat,6);;
    }

    /**
     * 纬度 Y
     */
    private BigDecimal lat;

    private Integer value = 0;
    private Double distance;

    /**
     * The Points.
     */
    List<Point> points;


    /**
     * The Id.
     */
    String id;

    /**
     * The Type.
     */
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

    @Override
    public int hashCode() {
        return Objects.hash(lng, lat);
    }

    /**
     * To array json array.
     *
     * @return the json array
     * @author ErebusST
     * @since 2022 -01-07 15:36:29
     */
    public JsonArray toArray() {
        JsonArray array = new JsonArray(2);
        array.add(lng);
        array.add(lat);
        return array;
    }

}
