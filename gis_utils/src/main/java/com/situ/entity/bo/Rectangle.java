/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;


import com.situ.tools.GisUtils;
import com.situ.tools.ListUtils;
import com.situ.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 司徒彬
 * @date 2022/10/26 14:53
 */
@Getter
@Setter
public class Rectangle {
    public List<Point> border;
    public Point center;

    /**
     * Get rectangle.
     *
     * @param rectangle the rectangle
     * @return the rectangle
     * @author ErebusST
     * @since 2022 -10-26 15:06:46
     */
    public static Rectangle get(String rectangle){
        if(StringUtils.equals(rectangle,"[]")){
            return null;
        }
        List<String> strings = StringUtils.splitToList(rectangle, ";");
        if(strings.size()==2){
            Point leftUp = Point.get(strings.get(0));
            Point rightDown = Point.get(strings.get(1));
            return get(leftUp, rightDown);
        }else {
            return null;
        }
    }

    /**
     * Get rectangle.
     *
     * @param leftUp    the left up
     * @param rightDown the right down
     * @return the rectangle
     * @author ErebusST
     * @since 2022 -10-26 15:02:20
     */
    public static Rectangle get(Point leftUp, Point rightDown) {
        Point rightUp = Point.get(leftUp.getLng(),rightDown.getLat());
        Point leftDown = Point.get(rightDown.getLng(),leftUp.getLat());

        Rectangle rectangle = new Rectangle();
        rectangle.border = ListUtils.newArrayList(leftUp,rightUp,rightDown,leftDown);
        rectangle.center = GisUtils.getCoreOfPolygon(rectangle.getBorder());
        return rectangle;
    }
}
