/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.google.gson.JsonArray;
import com.situ.entity.bo.Point;
import com.situ.enumeration.HexagonType;
import com.situ.enumeration.ShapeType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;


/**
 * 获取某个点的六边形各个点的坐标
 *
 * @author 司徒彬
 * @date 2020 -01-08 12:12
 */
@Slf4j
public class GisUtils {
    //常量 二分之一
    private static final BigDecimal ERFENZHIYI = BigDecimal.valueOf(0.5);

    private static final Integer SCALE = 10;

    private static final BigDecimal ERFENZHISAN = BigDecimal.valueOf(3).multiply(ERFENZHIYI);

    private static final BigDecimal GENHAOSAN = BigDecimal.valueOf(Math.sqrt(3));
    private static final BigDecimal GENHAOER = BigDecimal.valueOf(Math.sqrt(2));
    //常量 二分之根号三
    private static final BigDecimal ERFENZHIGENHAOSAN = GENHAOSAN.multiply(ERFENZHIYI);

    /**
     * Generate data list.
     *
     * @param center       the center
     * @param centerRadius the center radius
     * @param hiveRadius   the hive radius
     * @param type         the type
     * @return the list
     * @author ErebusST
     * @since 2022 -01-07 15:36:25
     */
    public static List<Point> generateData(Point center, BigDecimal centerRadius, BigDecimal hiveRadius, ShapeType type) {
        try {

            List<Point> result = new ArrayList<>();

            int count = centerRadius.divide(hiveRadius, 0, BigDecimal.ROUND_UP).intValue() + 1;

            Point start = center;// BD092Mercator(center);

            List<Point> borderPoints;
            if (type.equals(ShapeType.hexagon)) {
                borderPoints = getSixPoint(start, hiveRadius);
            } else {
                borderPoints = getSquarePoint(start, hiveRadius);
            }
            start.setPoints(borderPoints);
            result.add(start);
            result.addAll(getCurrentRowPointsInfo(start, hiveRadius, count, type));
            //向上/下求点
            List<Point> upPoints = new ArrayList<>();
            List<Point> downPoints = new ArrayList<>();
            List<Point> details = new ArrayList<>();
            IntStream.range(0, count).forEach(index -> {
                Point lastUpPoint;
                if (upPoints.size() == 0) {
                    lastUpPoint = start;
                } else {
                    lastUpPoint = upPoints.get(upPoints.size() - 1);
                }
                Point upPoint = GisUtils.getNextCenterPoint(lastUpPoint, hiveRadius, HexagonType.UP, type);
                List<Point> upSixPoints;
                if (type.equals(ShapeType.hexagon)) {
                    upSixPoints = GisUtils.getSixPoint(upPoint, hiveRadius);
                } else {
                    upSixPoints = GisUtils.getSquarePoint(upPoint, hiveRadius);
                }
                upPoint.setPoints(upSixPoints);
                upPoints.add(upPoint);
                //再获取当前行所有的点信息
                details.addAll(GisUtils.getCurrentRowPointsInfo(upPoint, hiveRadius, count, type));


                //向下
                Point lastDownPoint;
                if (downPoints.size() == 0) {
                    lastDownPoint = start;
                } else {
                    lastDownPoint = downPoints.get(downPoints.size() - 1);
                }
                Point downPoint = GisUtils.getNextCenterPoint(lastDownPoint, hiveRadius, HexagonType.DOWN, type);
                List<Point> downSixPoints;
                if (type.equals(ShapeType.hexagon)) {
                    downSixPoints = GisUtils.getSixPoint(downPoint, hiveRadius);
                } else {
                    downSixPoints = GisUtils.getSquarePoint(downPoint, hiveRadius);
                }
                downPoint.setPoints(downSixPoints);
                downPoints.add(downPoint);
                //再获取当前行所有的点信息
                details.addAll(GisUtils.getCurrentRowPointsInfo(downPoint, hiveRadius, count, type));
            });

            result = ListUtils.unionAll(result, upPoints, downPoints, details);

            result = result.stream()
                    .filter(object -> {
                        List<Point> range = object.getPoints();
                        boolean inCircle = range.stream()
                                .noneMatch(child -> GisUtils.checkPointInCircle(center, child, centerRadius));
                        if (inCircle) {
                            return true;
                        } else {
                            boolean inPolygon = checkCycleInSquare(center, centerRadius, range);
                            return inPolygon;
                        }
                    })
                    .collect(Collectors.toList());

            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets current row points info.
     *
     * @param point  the point
     * @param radius the radius
     * @param count  the count
     * @param type   the type
     * @return the current row points info
     * @author ErebusST
     * @since 2022 -01-07 15:36:25
     */
    public static List<Point> getCurrentRowPointsInfo(Point point, BigDecimal radius, Integer count, ShapeType type) {
        List<Point> leftPoints = new ArrayList<>(count);
        List<Point> rightPoints = new ArrayList<>(count);
        IntStream.range(0, count).forEach(index -> {
            Point lastLeftPoint;
            if (leftPoints.size() == 0) {
                lastLeftPoint = point;
            } else {
                lastLeftPoint = leftPoints.get(leftPoints.size() - 1);
            }

            Point nextLeftPoint = GisUtils.getNextCenterPoint(lastLeftPoint, radius, HexagonType.LEFT, type);
            List<Point> leftChildPoints;
            if (type.equals(ShapeType.hexagon)) {
                leftChildPoints = GisUtils.getSixPoint(nextLeftPoint, radius);
            } else {
                leftChildPoints = GisUtils.getSquarePoint(nextLeftPoint, radius);
            }
            nextLeftPoint.setPoints(leftChildPoints);
            leftPoints.add(nextLeftPoint);

            Point lastRightPoint;
            if (rightPoints.size() == 0) {
                lastRightPoint = point;
            } else {
                lastRightPoint = rightPoints.get(rightPoints.size() - 1);
            }

            Point nextRightPoint = GisUtils.getNextCenterPoint(lastRightPoint, radius, HexagonType.RIGHT, type);
            List<Point> rightChildPoints;
            if (type.equals(ShapeType.hexagon)) {
                rightChildPoints = GisUtils.getSixPoint(nextRightPoint, radius);
            } else {
                rightChildPoints = GisUtils.getSquarePoint(nextRightPoint, radius);
            }
            nextRightPoint.setPoints(rightChildPoints);
            rightPoints.add(nextRightPoint);
        });
        return ListUtils.unionAll(leftPoints, rightPoints);
    }

    /**
     * Gets square point.
     *
     * @param center the center
     * @param radius the radius
     * @return the square point
     * @author ErebusST
     * @since 2022 -01-07 15:36:25
     */
    public static List<Point> getSquarePoint(Point center, BigDecimal radius) {
        List<Point> points = new ArrayList<>(4);
        // Point point1 = new Point(center.getLng(), center.getLat().add(radius));
        Point point1 = getPointByRadius(center, 315, radius.doubleValue());
        points.add(point1);
        Point point2 = getPointByRadius(center, 45, radius.doubleValue());
        points.add(point2);
        Point point3 = getPointByRadius(center, 135, radius.doubleValue());
        points.add(point3);
        Point point4 = getPointByRadius(center, 225, radius.doubleValue());
        points.add(point4);
        return points;
    }

    /**
     * 获取六边形六个点的坐标，顺序为从最高点（顶点）开始顺时针共计六个点
     *
     * @param center the center
     * @param radius the radius
     * @return six point
     * @author ErebusST
     * @since 2022 -01-07 15:36:25
     */
    public static List<Point> getSixPoint(Point center, BigDecimal radius) {
        List<Point> points = new ArrayList<>(6);
        // Point point1 = new Point(center.getLng(), center.getLat().add(radius));
        Point point1 = getPointByRadius(center, 0, radius.doubleValue());
        points.add(point1);
        Point point2 = getPointByRadius(center, 60, radius.doubleValue());
        points.add(point2);
        Point point3 = getPointByRadius(center, 120, radius.doubleValue());
        points.add(point3);
        Point point4 = getPointByRadius(center, 180, radius.doubleValue());
        points.add(point4);
        Point point5 = getPointByRadius(center, 240, radius.doubleValue());
        points.add(point5);
        Point point6 = getPointByRadius(center, 300, radius.doubleValue());
        points.add(point6);
        return points;
    }


    /**
     * Gets next center point.
     *
     * @param current   the current
     * @param radius    the radius
     * @param type      the type
     * @param shapeType the shape type
     * @return the next center point
     * @author ErebusST
     * @since 2022 -01-07 15:36:25
     */
    public static Point getNextCenterPoint(Point current, BigDecimal radius, HexagonType type, ShapeType shapeType) {
        switch (type) {
            case LEFT:
                return getNextRowCenterPoint(current, radius, HexagonType.LEFT, shapeType);
            case RIGHT:
                return getNextRowCenterPoint(current, radius, HexagonType.RIGHT, shapeType);
            case UP:
                return getNextColCenterPoint(current, radius, HexagonType.UP, shapeType);
            case DOWN:
                return getNextColCenterPoint(current, radius, HexagonType.DOWN, shapeType);
            default:
                return current;
        }

    }


    /**
     * 获取下一个六边形中心点
     *
     * @param center    上一个中心店
     * @param radius
     * @param type      left or right
     * @param shapeType
     * @return
     */
    private static Point getNextRowCenterPoint(Point center, BigDecimal radius, HexagonType type, ShapeType shapeType) {
        //两点距离为 根号3R
        //间距
        double distance;
        double angle1;
        double angle2;
        if (shapeType.equals(ShapeType.hexagon)) {
            distance = GENHAOSAN.multiply(radius).doubleValue();
            angle1 = 90;
            angle2 = 270;
        } else {
            distance = GENHAOER.multiply(radius).doubleValue();

            angle1 = 90;
            angle2 = 270;
        }

        if (type.equals(HexagonType.RIGHT)) {
            return getPointByRadius(center, angle1, distance);
        } else {
            return getPointByRadius(center, angle2, distance);
        }
    }

    /**
     * Gets next col center point.
     *
     * @param center    the center
     * @param radius    the radius
     * @param type      the type
     * @param shapeType the shape type
     * @return the next col center point
     * @author ErebusST
     * @since 2022 -01-07 15:36:25
     */
    public static Point getNextColCenterPoint(Point center, BigDecimal radius, HexagonType type, ShapeType shapeType) {
        //横坐标为 x + 根号三/2*R 纵坐标为 y + 二分之三*R
        double distance;
        double angle1;
        double angle2;
        if (shapeType.equals(ShapeType.hexagon)) {
            distance = GENHAOSAN.multiply(radius).doubleValue();
            angle1 = 330;
            angle2 = 210;
        } else {
            distance = GENHAOER.multiply(radius).doubleValue();
            angle1 = 0;
            angle2 = 180;
        }
        if (type.equals(HexagonType.UP)) {
            return getPointByRadius(center, angle1, distance);
        } else {
            return getPointByRadius(center, angle2, distance);
        }
    }

    /**
     * Check point in circle boolean.
     * <p>
     * 需要经纬度坐标系
     *
     * @param center the center
     * @param point  the point
     * @param radius the radius
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:25
     */
    public static boolean checkPointInCircle(Point center, Point point, BigDecimal radius) {
        if (ObjectUtils.isNull(point)) {
            return false;
        }
        double distance = distance(center, point);
        return radius.doubleValue() >= distance;
    }


    /**
     * 判断是否在多边形区域内
     *
     * @param point   the point 要判断的点
     * @param polygon the polygon 要按顺序依次排列 顺时针或者逆时针
     * @return boolean boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:25
     */
    public static boolean checkPointInPolygon(Point point, List<Point> polygon) {
        if (ObjectUtils.isNull(point) || polygon.size() == 0 || polygon.size() == 1) {
            return false;
        }
        double pointLon = point.getLng().doubleValue();
        double pointLat = point.getLat().doubleValue();
        // 将要判断的横纵坐标组成一个点
        Point2D.Double point2D = new Point2D.Double(pointLon, pointLat);
        GeneralPath border = pointsToPolygon(polygon);
        return border.contains(point2D);

        /**
         *  Coordinate temp = new Coordinate(point.getLng().doubleValue(), point.getLat().doubleValue());
         *         org.locationtech.jts.geom.Point pointTemp = new GeometryFactory().createPoint(temp);
         *         Polygon polygonTemp = toPolygon(polygon);
         *         return polygonTemp.contains(pointTemp);
         */
    }


    private static GeneralPath pointsToPolygon(List<Point> polygon) {
        GeneralPath border = new GeneralPath();
        Point first = polygon.get(0);
        //通过移动到指定坐标（以双精度指定），将一个点添加到路径中
        border.moveTo(first.getLng().doubleValue(), first.getLat().doubleValue());
        polygon.stream().forEach(p -> {
            //通过绘制一条从当前坐标到新指定坐标（以双精度指定）的直线，将一个点添加到路径中。
            border.lineTo(p.getLng().doubleValue(), p.getLat().doubleValue());
        });

        // 将几何多边形封闭
        border.lineTo(first.getLng().doubleValue(), first.getLat().doubleValue());
        border.closePath();
        return border;
    }

    /**
     * Check polygons intersect boolean.
     *
     * @param polygon1 the polygon 1
     * @param polygon2 the polygon 2
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:25
     */
    public static boolean checkPolygonsIntersect(List<Point> polygon1, List<Point> polygon2) {
        try {
            List<Line> polygonLines1 = getPolygonLines(polygon1);
            List<Line> polygonLines2 = getPolygonLines(polygon2);
            for (Line line1 : polygonLines1) {
                for (Line line2 : polygonLines2) {
                    boolean intersect = Line2D
                            .linesIntersect(line1.x1, line1.y1, line1.x2, line1.y2, line2.x1, line2.y1, line2.x2, line2.y2);
                    if (intersect) {
                        return true;
                    } else {
                        continue;
                    }
                }
            }
            for (Point point : polygon2) {
                boolean intersect = checkPointInPolygon(point, polygon1);
                if (intersect) {
                    return true;
                }
            }
            for (Point point : polygon1) {
                boolean intersect = checkPointInPolygon(point, polygon2);
                if (intersect) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 获取多边形的各个边
     *
     * @param points
     * @return
     */
    private static List<Line> getPolygonLines(List<Point> points) {
        List<Line> lines = new ArrayList<>(points.size());
        int size = points.size() - 1;
        for (int i = 0; i < size; i++) {
            Point point = points.get(i);
            Point end = points.get(i + 1);
            Line line = new Line(point.getLng(), point.getLat(), end.getLng(), end.getLat());
            lines.add(line);
        }
        return lines;
    }

    /**
     * To json array json array.
     *
     * @param area the area
     * @return the json array
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static JsonArray toJsonArray(List<Point> area) {
        return area.stream()
                .map(point -> ListUtils.newArrayList(point.getLng(), point.getLat()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), DataSwitch::convertObjectToJsonArray));
    }

    /**
     * To list point list.
     *
     * @param polygon the polygon
     * @return the list
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static List<Point> toListPoint(Object polygon) {
        if (ObjectUtils.isEmpty(polygon)) {
            return new ArrayList<>(0);
        }
        JsonArray array = DataSwitch.convertStringToJsonArray(polygon);
        return StreamSupport.stream(array.spliterator(), false)
                .map(element -> {
                    JsonArray temp = element.getAsJsonArray();
                    return Point.get(temp.get(0).getAsBigDecimal(), temp.get(1).getAsBigDecimal());
                }).collect(Collectors.toList());
    }

    @Getter
    @Setter
    private static class Line {
        /**
         * The X 1.
         */
        double x1;
        /**
         * The Y 1.
         */
        double y1;
        /**
         * The X 2.
         */
        double x2;
        /**
         * The Y 2.
         */
        double y2;

        /**
         * Instantiates a new Line.
         *
         * @param x1 the x 1
         * @param y1 the y 1
         * @param x2 the x 2
         * @param y2 the y 2
         */
        Line(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
            this.x1 = x1.doubleValue();
            this.y1 = y1.doubleValue();
            this.x2 = x2.doubleValue();
            this.y2 = y2.doubleValue();
        }
    }


    /**
     * Distance double. 单位:米
     * <p>
     * 需要经纬度坐标系
     *
     * @param point1 the point 1
     * @param point2 the point 2
     * @return the double
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static double distance(Point point1, Point point2) {
        double lat1 = point1.getLat().doubleValue();
        double lng1 = point1.getLng().doubleValue();
        double lat2 = point2.getLat().doubleValue();
        double lng2 = point2.getLng().doubleValue();

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c * 1000; // 单位转换成米
        return distance;
    }


    /**
     * 根据一点的坐标与距离，以及方向，计算另外一点的位置
     * <p>
     * 需要经纬度坐标系
     * <p>
     * Point up = GisUtils.getPointByRadius(center, 0, distance);
     * Point right = GisUtils.getPointByRadius(center, 90, distance);
     * Point down = GisUtils.getPointByRadius(center, 180, distance);
     * Point left = GisUtils.getPointByRadius(center, 270, distance);
     *
     * @param point    the point
     * @param angle    角度，从正北顺时针方向开始计算
     * @param distance 距离，单位m
     * @return point point by radius
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point getPointByRadius(Point point, double angle, double distance) {
        double startLat = Math.toRadians(point.getLat().doubleValue());
        double startLng = Math.toRadians(point.getLng().doubleValue());
        //将距离转换成经度的计算公式
        double δ = distance / (EARTH_RADIUS * 1000);
        // 转换为radian，否则结果会不正确
        angle = Math.toRadians(angle);
        double lat = Math.asin(Math.sin(startLat) * Math.cos(δ) + Math.cos(startLat) * Math.sin(δ) * Math.cos(angle));
        double lng = startLng + Math.atan2(Math.sin(angle) * Math.sin(δ) * Math.cos(startLat), Math.cos(δ) - Math.sin(startLat) * Math.sin(lat));
        // 转为正常的10进制经纬度
        lng = Math.toDegrees(lng);
        lat = Math.toDegrees(lat);
        return new Point(lng, lat);
    }


    private static final double RADIUS = 6370996.81;
    private static final Double EARTH_RADIUS = RADIUS / 1000;
    private static final List<Double> MERCATOR_BAND;
    private static final List<Double> LL_BAND;
    private static final Double[][] MC2LL = {
            {1.410526172116255e-8, 0.00000898305509648872, -1.9939833816331, 200.9824383106796, -187.2403703815547,
                    91.6087516669843, -23.38765649603339, 2.57121317296198, -0.03801003308653, 17337981.2},
            {-7.435856389565537e-9, 0.000008983055097726239, -0.78625201886289, 96.32687599759846, -1.85204757529826,
                    -59.36935905485877, 47.40033549296737, -16.50741931063887, 2.28786674699375, 10260144.86},
            {-3.030883460898826e-8, 0.00000898305509983578, 0.30071316287616, 59.74293618442277, 7.357984074871,
                    -25.38371002664745, 13.45380521110908, -3.29883767235584, 0.32710905363475, 6856817.37},
            {-1.981981304930552e-8, 0.000008983055099779535, 0.03278182852591, 40.31678527705744, 0.65659298677277,
                    -4.44255534477492, 0.85341911805263, 0.12923347998204, -0.04625736007561, 4482777.06},
            {3.09191371068437e-9, 0.000008983055096812155, 0.00006995724062, 23.10934304144901, -0.00023663490511,
                    -0.6321817810242, -0.00663494467273, 0.03430082397953, -0.00466043876332, 2555164.4},
            {2.890871144776878e-9, 0.000008983055095805407, -3.068298e-8, 7.47137025468032, -0.00000353937994,
                    -0.02145144861037, -0.00001234426596, 0.00010322952773, -0.00000323890364, 826088.5}
    };
    private static final Double[][] LL2MC = {
            {-0.0015702102444, 111320.7020616939, 1704480524535203d, -10338987376042340d, 26112667856603880d,
                    -35149669176653700d, 26595700718403920d, -10725012454188240d, 1800819912950474d, 82.5},
            {0.0008277824516172526, 111320.7020463578, 647795574.6671607, -4082003173.641316, 10774905663.51142,
                    -15171875531.51559, 12053065338.62167, -5124939663.577472, 913311935.9512032, 67.5},
            {0.00337398766765, 111320.7020202162, 4481351.045890365, -23393751.19931662, 79682215.47186455,
                    -115964993.2797253, 97236711.15602145, -43661946.33752821, 8477230.501135234, 52.5},
            {0.00220636496208, 111320.7020209128, 51751.86112841131, 3796837.749470245, 992013.7397791013,
                    -1221952.21711287, 1340652.697009075, -620943.6990984312, 144416.9293806241, 37.5},
            {-0.0003441963504368392, 111320.7020576856, 278.2353980772752, 2485758.690035394, 6070.750963243378,
                    54821.18345352118, 9540.606633304236, -2710.55326746645, 1405.483844121726, 22.5},
            {-0.0003218135878613132, 111320.7020701615, 0.00369383431289, 823725.6402795718, 0.46104986909093,
                    2351.343141331292, 1.58060784298199, 8.77738589078284, 0.37238884252424, 7.45}
    };
    private static final Integer MERCATOR_BAND_LENGTH;
    private static final Integer LL_BAND_LENGTH;

    static {
        MERCATOR_BAND = ListUtils.newArrayList(12890594.86, 8362377.87, 5591021d, 3481989.83, 1678043.12, 0d);
        MERCATOR_BAND_LENGTH = MERCATOR_BAND.size();
        LL_BAND = ListUtils.newArrayList(75d, 60d, 45d, 30d, 15d, 0d);
        LL_BAND_LENGTH = LL_BAND.size();
    }

    //region 坐标转换

    /**
     * 墨卡托坐标转经纬度坐标
     *
     * @param point the point
     * @return map point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point Mercator2BD09(Point point) {
        double x = point.getLng().doubleValue();
        double y = point.getLat().doubleValue();

        Double[] cF = null;
        x = Math.abs(x);
        y = Math.abs(y);

        for (int cE = 0; cE < MERCATOR_BAND_LENGTH; cE++) {
            if (y >= MERCATOR_BAND.get(cE)) {
                cF = MC2LL[cE];
                break;
            }
        }
        Pair<Double, Double> location = converter(x, y, cF);
        Point result = new Point(location.getLeft(), location.getRight());
        return result;
    }

    /**
     * 经纬度坐标转墨卡托坐标
     *
     * @param x
     * @param y
     * @param cE
     * @return
     */
    private static Pair<Double, Double> converter(Double x, Double y, Double[] cE) {
        Double xTemp = cE[0] + cE[1] * Math.abs(x);
        Double cC = Math.abs(y) / cE[9];
        Double yTemp = cE[2] + cE[3] * cC + cE[4] * cC * cC + cE[5] * cC * cC * cC + cE[6] * cC * cC * cC * cC + cE[7] * cC * cC * cC * cC * cC + cE[8] * cC * cC * cC * cC * cC * cC;
        xTemp *= (x < 0 ? -1 : 1);
        yTemp *= (y < 0 ? -1 : 1);
        return Pair.of(xTemp, yTemp);
    }

    /**
     * Bd 092 mercator point.
     *
     * @param point the point
     * @return the point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point BD092Mercator(Point point) {
        double lng = point.getLng().doubleValue();
        double lat = point.getLat().doubleValue();

        Double[] cE = null;
        lng = getLoop(lng, -180, 180);
        lat = getRange(lat, -74, 74);
        for (int i = 0; i < LL_BAND_LENGTH; i++) {
            if (lat >= LL_BAND.get(i)) {
                cE = LL2MC[i];
                break;
            }
        }
        if (cE != null) {
            for (int i = LL_BAND_LENGTH - 1; i >= 0; i--) {
                if (lat <= -LL_BAND.get(i)) {
                    cE = LL2MC[i];
                    break;
                }
            }
        }

        Pair<Double, Double> location = converter(lng, lat, cE);
        Point result = new Point(location.getLeft(), location.getRight());
        return result;
    }


    /**
     * @param lng
     * @param min
     * @param max
     * @return
     */
    private static Double getLoop(Double lng, Integer min, Integer max) {
        while (lng > max) {
            lng -= max - min;
        }
        while (lng < min) {
            lng += max - min;
        }
        return lng;
    }

    /**
     * @param lat
     * @param min
     * @param max
     * @return
     */
    private static Double getRange(Double lat, Integer min, Integer max) {
        if (min != null) {
            lat = Math.max(lat, min);
        }
        if (max != null) {
            lat = Math.min(lat, max);
        }
        return lat;
    }


    /**
     * Wgs 84 to bd 09 point.
     *
     * @param point the point
     * @return the point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point WGS84ToBD09(Point point) {
        Pair<Double, Double> pair = wgs84tobd09(point.getLng().doubleValue(), point.getLat().doubleValue());
        return new Point(pair.getLeft(), pair.getRight());
    }

    /**
     * WGS84 转换为 BD-09
     *
     * @param lng the lng
     * @param lat the lat
     * @return the pair
     * @author ErebusST
     * @returns {*[]}
     * @since 2022 -01-07 15:36:26
     */
    public static Pair<Double, Double> wgs84tobd09(double lng, double lat) {
        //第一次转换
        Double dlat = transformlat(lng - 105.0, lat - 35.0);
        double dlng = transformlng(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * Math.PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * Math.PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;

        //第二次转换
        double z = Math.sqrt(mglng * mglng + mglat * mglat) + 0.00002 * Math.sin(mglat * x_PI);
        double theta = Math.atan2(mglat, mglng) + 0.000003 * Math.cos(mglng * x_PI);
        Double bd_lng = z * Math.cos(theta) + 0.0065;
        Double bd_lat = z * Math.sin(theta) + 0.006;
        return Pair.of(bd_lng, bd_lat);
    }


    private static double transformlat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * Math.PI) + 20.0 * Math.sin(2.0 * lng * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * Math.PI) + 40.0 * Math.sin(lat / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * Math.PI) + 320 * Math.sin(lat * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformlng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * Math.PI) + 20.0 * Math.sin(2.0 * lng * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * Math.PI) + 40.0 * Math.sin(lng / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * Math.PI) + 300.0 * Math.sin(lng / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }


    /**
     * Gaode to baidu point.
     *
     * @param point the point
     * @return the point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point GaodeToBaidu(Point point) {
        Pair<BigDecimal, BigDecimal> pair = GaodeToBaidu(point.getLng(), point.getLat());
        return new Point(pair.getLeft(), pair.getRight());
    }

    /**
     * Gaode to baidu pair.
     * <p>
     * 高德坐标转百度坐标
     * <p>
     * 高德 GCJ02 国测局02
     * <p>
     * 百度BD09
     *
     * @param lng the lng
     * @param lat the lat
     * @return the pair
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Pair<BigDecimal, BigDecimal> GaodeToBaidu(BigDecimal lng, BigDecimal lat) {
        var X_PI = Math.PI * 3000.0 / 180.0;
        var x = lng.doubleValue();
        var y = lat.doubleValue();
        var z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
        var theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
        var bd_lng = z * Math.cos(theta) + 0.0065;
        var bd_lat = z * Math.sin(theta) + 0.006;
        return Pair.of(BigDecimal.valueOf(bd_lng), BigDecimal.valueOf(bd_lat));
    }

    /**
     * Baidu to gaode point.
     *
     * @param point the point
     * @return the point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point BaiduToGaode(Point point) {
        return BaiduToGaode(point.getLng(), point.getLat());
    }

    /**
     * 百度转高德
     *
     * @param lng the lng
     * @param lat the lat
     * @return point point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point BaiduToGaode(Object lng, Object lat) {
        BigDecimal lngTemp = DataSwitch.convertObjectToBigDecimal(lng, 6);
        BigDecimal latTemp = DataSwitch.convertObjectToBigDecimal(lat, 6);
        var X_PI = Math.PI * 3000.0 / 180.0;
        var x = lngTemp.doubleValue() - 0.0065;
        var y = latTemp.doubleValue() - 0.006;
        var z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        var theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        var gg_lng = z * Math.cos(theta);
        var gg_lat = z * Math.sin(theta);
        return new Point(gg_lng, gg_lat);
//        return Pair.of(BigDecimal.valueOf(gg_lng), BigDecimal.valueOf(gg_lat));

    }

    /**
     * Web mercator bd 90 point.
     *
     * @param point the point
     * @return the point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point WebMercatorBD90(Point point) {
        Double x = point.getLng().doubleValue();
        Double y = point.getLat().doubleValue();
        x = x / 20037508.34 * 180;
        Double mmy = y / 20037508.34 * 180;
        y = 180 / Math.PI * (2 * Math.atan(Math.exp(mmy * Math.PI / 180)) - Math.PI / 2);

        Double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

        Double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);

        Double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);

        x = z * Math.cos(theta) + 0.0065;
        y = z * Math.sin(theta) + 0.006;

        return new Point(x, y);
    }

    /**
     * Bd 092 gcj 02 point.
     *
     * @param point the point
     * @return the point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point BD092GCJ02(Point point) {
        double bd_lon = point.getLng().doubleValue();
        double bd_lat = point.getLat().doubleValue();
        bd_lon = +bd_lon;
        bd_lat = +bd_lat;
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new Point(DataSwitch.convertObjectToBigDecimal(gg_lng, 6), DataSwitch.convertObjectToBigDecimal(gg_lat, 6));
    }

    /**
     * 高德转WGS84
     *
     * @param point the point
     * @return point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point GCJ02ToWGS84(Point point) {
        double lng = point.getLng().doubleValue();
        double lat = point.getLat().doubleValue();
        var dlat = transformlat(lng - 105.0, lat - 35.0);
        var dlng = transformlng(lng - 105.0, lat - 35.0);
        var radlat = lat / 180.0 * Math.PI;
        var magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        var sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * Math.PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * Math.PI);
        var mglat = lat + dlat;
        var mglng = lng + dlng;
        return Point.get(lng * 2 - mglng, lat * 2 - mglat);
    }

    /**
     * Bd 09 to wgs 84 point.
     *
     * @param point the point
     * @return the point
     * @author ErebusST
     * @since 2022 -01-07 15:36:26
     */
    public static Point BD09ToWGS84(Point point) {
        Point temp = BaiduToGaode(point.getLng(), point.getLat());
        return GCJ02ToWGS84(temp);
    }

    //endregion

    /**
     * The X pi.
     */
    static double x_PI = Math.PI * 3000.0 / 180.0;
    /**
     * The A.
     */
    static double a = 6378245.0;
    /**
     * The Ee.
     */
    static double ee = 0.00669342162296594323;

    /**
     * Fix geo info list.
     *
     * @param baiGeo the bai geo
     * @return the list
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static List<Point> fixGeoInfo(String baiGeo) {
        try {
            return StringUtils.splitToList(baiGeo, ";").stream().map(pointStr -> {
                List<String> strings = StringUtils.splitToList(pointStr, ",");
                if (strings.size() != 2
                        || (strings.size() == 2 && (!StringUtils.isFloat(strings.get(0))) || !StringUtils.isFloat(strings.get(1)))) {
                    return null;
                } else {
                    return new Point(strings.get(0), strings.get(1));
                }

            }).filter(ObjectUtils::isNotNull).collect(Collectors.toList());
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Gets core of polygon.
     *
     * @param polygon the polygon
     * @return the core of polygon
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static Point getCoreOfPolygon(List<Point> polygon) {
        double area = 0.0;//多边形面积
        double Gx = 0.0, Gy = 0.0;// 重心的x、y

        int size = polygon.size();
        for (int i = 1; i <= size; i++) {
            double iLat = polygon.get(i % size).getLng().doubleValue();
            double iLng = polygon.get(i % size).getLat().doubleValue();
            double nextLat = polygon.get(i - 1).getLng().doubleValue();
            double nextLng = polygon.get(i - 1).getLat().doubleValue();
            double temp = (iLat * nextLng - iLng * nextLat) / 2.0;
            area += temp;
            Gx += temp * (iLat + nextLat) / 3.0;
            Gy += temp * (iLng + nextLng) / 3.0;
        }
        Gx = Gx / area;
        Gy = Gy / area;
        return Point.get(Gx, Gy);

    }

    /**
     * Gets center of polygon.
     *
     * @param polygon the polygon
     * @return the center of polygon
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static Point getCenterOfPolygon(List<Point> polygon) {
        int size = polygon.size();
        BigDecimal lng = polygon.stream().map(Point::getLng).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(size), 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal lat = polygon.stream().map(Point::getLat).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(size), 6, BigDecimal.ROUND_HALF_UP);

        return Point.get(lng, lat);
    }


    /**
     * The Earth radius.
     */
    static double earth_radius = 6371000.0;
    /**
     * The Meters per degree.
     */
    static double metersPerDegree = 2.0 * Math.PI * earth_radius / 360.0;
    /**
     * The Radians per degree.
     */
    static double radiansPerDegree = Math.PI / 180.0;
    /**
     * The Degrees radian.
     */
    static double degreesPerRadian = 180.0 / Math.PI;

    /**
     * 计算球面多边形的面积
     * 结果单位：平方米
     *
     * @param polygon the polygon
     * @return double double
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    @Deprecated
    public static double calcSphericalPolygonArea(List<Point> polygon) {
        double totalAngle = 0.0;
        int size = polygon.size();
        for (int i = 0; i < size; ++i) {
            int j = (i + 1) % size;
            int k = (i + 2) % size;
            totalAngle += getAngle(polygon.get(i), polygon.get(j), polygon.get(k));
        }
        double totalAngleOfPlanar = (size - 2) * 180.0;
        double excess = totalAngle - totalAngleOfPlanar;
        if (excess > 420) {
            totalAngle = size * 360.0 - totalAngle;
            excess = totalAngle - totalAngleOfPlanar;
        } else if (excess > 300 && excess < 420) {
            excess = Math.abs(360.0 - excess);
        }
        return excess * radiansPerDegree * earth_radius * earth_radius;
    }

    /**
     * Calc area big decimal.
     * <p>
     * 计算圆的面积
     *
     * @param radius the radius
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-18 10:43:00
     */
    public static BigDecimal calcArea(BigDecimal radius) {
        BigDecimal area = BigDecimal.valueOf(Math.pow(radius.doubleValue(), 2) * Math.PI);
        return area;
    }

    /**
     * Calc area big decimal.
     * <p>
     * 计算多边形面积
     *
     * @param polygon the polygon
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-18 10:43:04
     */
    public static BigDecimal calcArea(List<Point> polygon) {
        double area = calcPolygonArea(polygon);
        return BigDecimal.valueOf(area);
    }


    /**
     * Calc polygon area double.
     * 相对结果比较准确的方法
     * <p>
     * 此方法后续需要淘汰
     *
     * @param polygon the polygon
     * @return the double 平方米
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    @Deprecated
    public static double calcPolygonArea(List<Point> polygon) {
        if (polygon != null && polygon.size() >= 3) {
            double area = 0.0D;
            int size = polygon.size();

            for (int index = 0; index < size; ++index) {
                Point point1 = polygon.get(index);
                Point point2 = polygon.get((index + 1) % size);
                double temp1 = point1.getLng().doubleValue() * 111319.49079327357D * Math.cos(point1.getLat().doubleValue() * 0.017453292519943295D);
                double temp2 = point1.getLat().doubleValue() * 111319.49079327357D;
                double temp3 = point2.getLng().doubleValue() * 111319.49079327357D * Math.cos(point2.getLat().doubleValue() * 0.017453292519943295D);
                double temp4 = point2.getLat().doubleValue() * 111319.49079327357D;
                area += temp1 * temp4 - temp3 * temp2;
            }

            return Math.abs(area / 2.0D);
        } else {
            return 0.0F;
        }
    }


    /**
     * 计算角度
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    private static double getAngle(Point a, Point b, Point c) {
        double bearingBA = getBearing(b, a);
        double bearingBC = getBearing(b, c);
        double angle = bearingBA - bearingBC;
        if (angle < 0.0) {
            angle += 360.0;
        }
        return angle;
    }

    /**
     * 计算方向
     * <p>
     * 计算两个坐标的方位角
     *
     * @param from the from
     * @param to   the to
     * @return bearing
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static double getBearing(Point from, Point to) {
        double lat1 = from.getLat().doubleValue() * radiansPerDegree;
        double lng1 = from.getLng().doubleValue() * radiansPerDegree;
        double lat2 = to.getLat().doubleValue() * radiansPerDegree;
        double lng2 = to.getLng().doubleValue() * radiansPerDegree;

        double angle = -Math.atan2(Math.sin(lng1 - lng2) * Math.cos(lat2),
                Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lng1 - lng2));
        if (angle < 0.0) {
            angle += Math.PI * 2.0;
        }
        angle = angle * degreesPerRadian;
        return angle;
    }

    /**
     * 计算平面多边形面积
     * <p>
     * 结果单位：平方米
     *
     * @param polygon the polygon
     * @return the double
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static double calcPlanarPolygonArea(List<Point> polygon) {
        double area = 0.0;
        int size = polygon.size();
        for (int i = 0; i < size; ++i) {
            int j = (i + 1) % size;
            Point point = polygon.get(i);
            double xi = point.getLng().doubleValue() * metersPerDegree *
                    Math.cos(point.getLat().doubleValue() * radiansPerDegree);
            double yi = point.getLat().doubleValue() * metersPerDegree;
            double xj = polygon.get(j).getLng().doubleValue() * metersPerDegree
                    * Math.cos(polygon.get(j).getLat().doubleValue() * radiansPerDegree);
            double yj = polygon.get(j).getLat().doubleValue() * metersPerDegree;
            area += xi * yj - xj * yi;
        }
        return Math.abs(area / 2.0);
    }


    /**
     * 计算三角形面积
     *
     * @param point1 the point 1
     * @param point2 the point 2
     * @param point3 the point 3
     * @return 平方米 double
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static double calcTrigonArea(Point point1, Point point2, Point point3) {
        /**
         * 有 p1(x0,y0) p2(x1,y1) p3(x2,y2)
         * 直线方程 y=kx+b
         * k = (y1-y2)/(x1-x2)
         * b = (x1y2-x2y1)/(x1-x2)
         * 高 = |kx0-y0+b|/二次根(k*k+1)
         * 底 = distance(point1,piont2)
         * 面积 = (1/2)*高*底
         *
         *
         * 使用海伦公式
         * p = (a + b + c)/2
         * S = 二次根(p*(p-a)(p-b)(p-c))
         */
        double line12 = GisUtils.distance(point1, point2);
        double line23 = GisUtils.distance(point2, point3);
        double line31 = GisUtils.distance(point3, point1);

        double p = (line12 + line23 + line31) / 2;
        double area = Math.sqrt(p * (p - line12) * (p - line23) * (p - line31));
        return area;


    }

    /**
     * Gets core on line.
     *
     * @param point1 the point 1
     * @param point2 the point 2
     * @return the core on line
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static Point getCoreOnLine(Point point1, Point point2) {
        double a1 = point1.getLng().doubleValue();
        double b1 = point1.getLat().doubleValue();
        double a2 = point2.getLng().doubleValue();
        double b2 = point2.getLat().doubleValue();
        double x = (a1 + a2) / 2;
        double y = (b1 + b2) / 2;
        return new Point(x, y);
    }

    /**
     * Gets core in triangle.
     *
     * @param point1 the point 1
     * @param point2 the point 2
     * @param point3 the point 3
     * @return the core in triangle
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static Point getCoreInTriangle(Point point1, Point point2, Point point3) {
        double a1 = point1.getLng().doubleValue();
        double b1 = point1.getLat().doubleValue();
        double a2 = point2.getLng().doubleValue();
        double b2 = point2.getLat().doubleValue();
        double a3 = point3.getLng().doubleValue();
        double b3 = point3.getLat().doubleValue();
        double x = (a1 + a2 + a3) / 3;
        double y = (b1 + b2 + b3) / 3;
        return new Point(x, y);
    }

    /**
     * Gets center on line.
     *
     * @param point1 the point 1
     * @param point2 the point 2
     * @return the center on line
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static Point getCenterOnLine(Point point1, Point point2) {
        /**
         *设2个坐标分别为A(X1,Y1) B(X2,Y2)
         * 则两点的中心坐标为C((X1+X2)/2,(Y1+Y2)/2)
         */

        double lng = (point1.getLng().doubleValue() + point2.getLng().doubleValue()) / 2;
        double lat = (point1.getLat().doubleValue() + point2.getLat().doubleValue()) / 2;
        return new Point(lng, lat);
    }

    //region JTS

    /**
     * To coordinate array coordinate [ ].
     *
     * @param points the points
     * @return the coordinate [ ]
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static Coordinate[] toCoordinateArray(List<Point> points) {
        if (points.size() == 0) {
            return (Coordinate[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        Coordinate[] coordinates = points.stream()
                .map(GisUtils::BD09ToWGS84)
                .map(point -> {
                    Coordinate coordinate = new Coordinate(point.getLng().doubleValue(),
                            point.getLat().doubleValue());
                    return coordinate;
                })
                .collect(Collectors.toList()).toArray(new Coordinate[points.size()]);
        Point first = points.get(0);
        Point last = ListUtils.last(points);
        if (!first.equals(last)) {
            coordinates = ArrayUtils.add(coordinates, coordinates[0]);
        }

        return coordinates;
    }

    /**
     * String to geometry list.
     *
     * @param string the string
     * @return the list
     * @throws ParseException the parse exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static List<Point> StringToGeometry(String string) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        Geometry read = reader.read(string);
        List<Point> points = toListPoint(read);
        return points;
    }

    /**
     * To list point list.
     *
     * @param geometry the geometry
     * @return the list
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static List<Point> toListPoint(Geometry geometry) {
        Coordinate[] coordinates = geometry.getCoordinates();
        return Arrays.stream(coordinates)
                .map(temp -> {
                    double x = temp.x;
                    double y = temp.y;
                    return WGS84ToBD09(Point.get(x, y));
                }).collect(Collectors.toList());
        //.subList(0, length - 1);
    }

    /**
     * Cycle and polygon list.
     *
     * @param center the center
     * @param radius the radius
     * @param points the points
     * @return the list
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static List<Point> cycleAndPolygon(Point center, double radius, List<Point> points) {
        try {
            List<Point> cycle = toCycle(center, radius);
            return intersection(points, cycle);
        } catch (Exception ex) {
            return points;
        }
    }

    /**
     * Intersection list.
     *
     * @param polygon1 the polygon 1
     * @param polygon2 the polygon 2
     * @return the list
     * @author ErebusST
     * @since 2022 -01-07 15:36:27
     */
    public static List<Point> intersection(List<Point> polygon1, List<Point> polygon2) {
        boolean allIn = polygon1.stream().allMatch(item -> GisUtils.checkPointInPolygon(item, polygon2));
        if (allIn) {
            return polygon1;
        }

        allIn = polygon2.stream().allMatch(item -> GisUtils.checkPointInPolygon(item, polygon1));
        if (allIn) {
            return polygon2;
        }
        Geometry geometry1 = fixPolygon(polygon1);

        Geometry geometry2 = fixPolygon(polygon2);

        Geometry intersection = geometry1.intersection(geometry2);
        return toListPoint(intersection);
    }

    private static Polygon fixPolygon(List<Point> points) {
        Polygon polygon = toPolygon(points);
        if (polygon.isValid()) {
            return polygon;
        } else {
            int size = points.size();
            for (int i = 0; i < size; i++) {
                Point point = points.get(i);
                points.remove(i);
                Polygon temp = toPolygon(points);
                if (temp.isValid()) {
                    break;
                } else {
                    points.add(i, point);
                }
            }
            return toPolygon(points);
        }
    }


    /**
     * To polygon polygon.
     *
     * @param points the points
     * @return the polygon
     * @author ErebusST
     * @since 2022 -01-07 15:36:28
     */
    public static Polygon toPolygon(List<Point> points) {
        Coordinate[] coordinates = toCoordinateArray(points);
        Polygon polygon = new GeometryFactory().createPolygon(coordinates);
        return polygon;
    }


    /**
     * Gets area.
     * <p>
     * 未知原因不准
     *
     * @param points the points
     * @return the area
     * @throws FactoryException   the factory exception
     * @throws TransformException the transform exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:28
     */
    public static double getArea(List<Point> points) throws FactoryException, TransformException {
        Polygon polygon = toPolygon(points);
        // WGS84(一般项目中常用的是CSR:84和EPSG:4326)
        CoordinateReferenceSystem sourceCRS = CRS.decode("CRS:84");
        // Pseudo-Mercator(墨卡托投影)
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, false);
        Geometry geometryMercator = JTS.transform(polygon, transform);
        return geometryMercator.getArea();
    }
    //endregion


    /**
     * Get polygon square range list.
     *
     * @param polygon the polygon
     * @return the polygon square range
     * @author ErebusST
     * @since 2022 -01-07 15:36:28
     */
    public static List<Point> getPolygonSquareRange(List<Point> polygon) {
        try {
            Optional<BigDecimal> lngMaxOptional = polygon
                    .stream()
                    .map(point -> point.getLng())
                    .max(BigDecimal::compareTo);

            Optional<BigDecimal> lngMinOptional = polygon.stream()
                    .map(point -> point.getLng())
                    .min(BigDecimal::compareTo);

            Optional<BigDecimal> latMaxOptional = polygon.stream()
                    .map(point -> point.getLat())
                    .max(BigDecimal::compareTo);

            Optional<BigDecimal> latMinOptional = polygon.stream()
                    .map(point -> point.getLat())
                    .min(BigDecimal::compareTo);

            /**
             * 分布图
             * point4       point1
             *
             *
             * point3       point2
             */
            //取等时圈多边形内边缘最小的矩形，也可能是正方形
            Point point1 = new Point(lngMaxOptional.get(), latMaxOptional.get());
            Point point2 = new Point(lngMaxOptional.get(), latMinOptional.get());
            Point point3 = new Point(lngMinOptional.get(), latMinOptional.get());
            Point point4 = new Point(lngMinOptional.get(), latMaxOptional.get());

            List<Point> points = ListUtils.newArrayList(point1, point2, point3, point4);

            return points;
        } catch (Exception ex) {
            log.error("计算区域分布出错:" + DataSwitch.convertObjectToJsonArray(polygon), ex);
            throw ex;
        }
    }

    /**
     * Check cycle in square boolean.
     *
     * @param center the center
     * @param radius the radius
     * @param square the square
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:28
     */
    public static boolean checkCycleInSquare(Point center, BigDecimal radius, List<Point> square) {
        Point north = getPointByRadius(center, 0, radius.doubleValue());
        Point north1 = getPointByRadius(center, 45, radius.doubleValue());
        Point east = getPointByRadius(center, 90, radius.doubleValue());
        Point east1 = getPointByRadius(center, 90 + 45, radius.doubleValue());
        Point south = getPointByRadius(center, 180, radius.doubleValue());
        Point south1 = getPointByRadius(center, 180 + 45, radius.doubleValue());
        Point west = getPointByRadius(center, 270, radius.doubleValue());
        Point west1 = getPointByRadius(center, 270 + 45, radius.doubleValue());
        return ListUtils.newArrayList(north, north1, east, east1, south, south1, west, west1)
                .stream().anyMatch(point -> !checkPointInPolygon(point, square));
    }


    /**
     * To cycle list.
     *
     * @param center the center
     * @param radius the radius
     * @return the list
     * @author ErebusST
     * @since 2022 -04-15 13:55:03
     */
    public static List<Point> toCycle(Point center, Number radius) {
        List<Point> list = new ArrayList<>(36);
        for (int i = 0; i < 360; i = i + 10) {
            Point point = getPointByRadius(center, i, radius.doubleValue());
            list.add(point);
        }
        return list;
    }

    /**
     * 根据圆形中心点经纬度、半径生成圆形（类圆形，32边多边形）
     *
     * @param x      中心点经度
     * @param y      中心点纬度
     * @param radius 半径（米）
     * @return polygon
     * @author ErebusST
     * @since 2022 -01-07 15:36:28
     */
    public static Polygon toCycle(BigDecimal x, BigDecimal y, final double radius) {
        //Point temp = BD09ToWGS84(Point.get(x, y));
        Point temp = Point.get(x, y);
        List<Point> list = toCycle(temp, radius);
        return toPolygon(list);
        ////将半径转换为度数
        //double radiusDegree = parseYLengthToDegree(radius);
        ////生成工厂类
        //GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        ////设置生成的类圆形边数
        //shapeFactory.setNumPoints(32);
        ////设置圆形中心点经纬度
        //shapeFactory.setCentre(new Coordinate(temp.getLng().doubleValue(), temp.getLat().doubleValue()));
        ////设置圆形直径
        //shapeFactory.setSize(radiusDegree * 2);
        ////使用工厂类生成圆形
        //Polygon circle = shapeFactory.createCircle();
        //return circle;
    }


    /**
     * 将Y轴的长度（米）转换成纬度
     *
     * @param length
     * @return
     */
    private static double parseYLengthToDegree(double length) {
        //这种方式不对 半径会偏长
        //将length长度转换为度数
//        double yDegree = length / EARTH_RADIUS * 360;
//        return yDegree;

        //使用Y轴做计量 会形成一个椭圆弧 这里在X轴也组处理暂时 没有找到方案
        Double degree = ((2 * Math.PI * 3959) * 1609) / 360.0;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * length;
        return radiusLat;
    }

}
