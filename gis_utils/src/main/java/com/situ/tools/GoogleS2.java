/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.google.common.collect.Lists;
import com.google.common.geometry.*;
import com.situ.entity.bo.Point;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.situ.tools.GisUtils.toListPoint;

/**
 * 必须使用-S2使用的是WGS84坐标
 * 如果你获得的是WGS84坐标-百度或者高德的地理坐标，请将其转换为GPS-WGS84坐标
 * 由于google s2默认使用gps坐标系，在国内无法使用，需要转换为国内的gcj坐标或者bd09坐标
 * 主要包含3类方法：
 * getS2RegionByXXX
 * 获取给定经纬度坐标对应的S2Region,该region可用于获取cellId,或用于判断包含关系
 * getCellIdList
 * 获取给定region的cellId,并通过childrenCellId方法控制其严格遵守minLevel
 * contains
 * 对于指定S2Region,判断经纬度或CellToken是否在其范围内
 *
 * @author situ
 */
/*
包
	<dependency>
        <groupId>io.sgr</groupId>
        <artifactId>s2-geometry-library-java</artifactId>
        <version>1.0.0</version>
    </dependency>
 */
@Slf4j
public class GoogleS2 {

    /**
     * 经纬度 转 S2CellId
     *
     * @param lat          维度
     * @param lng          经度
     * @param currentLevel level选择级别
     */
    public static S2CellId latLonToS2LatLng(double lat, double lng, int currentLevel) {
        S2LatLng s2LatLng = S2LatLng.fromDegrees(lat, lng);
        S2CellId cellId = S2CellId.fromLatLng(s2LatLng).parent(currentLevel);
        return cellId;
    }

    /**
     * 经纬度 转 CellId
     *
     * @param lat          维度
     * @param lng          经度
     * @param currentLevel level选择级别
     */
    public static Long latLonToCellId(double lat, double lng, int currentLevel) {
        S2LatLng s2LatLng = S2LatLng.fromDegrees(lat, lng);
        S2CellId cellId = S2CellId.fromLatLng(s2LatLng).parent(currentLevel);
        return cellId.id();
    }

    /**
     * 经纬度 转 cellToken
     *
     * @param lat          维度
     * @param lng          经度
     * @param currentLevel level选择级别
     */
    public static String latLonToCellToken(double lat, double lng, int currentLevel) {
        try {
            S2LatLng s2LatLng = S2LatLng.fromDegrees(lat, lng);
            S2CellId cellId = S2CellId.fromLatLng(s2LatLng).parent(currentLevel);
            return cellId.toToken();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * CellId 转 经纬度
     *
     * @param cellId 是 S2CellId.id();
     * @return
     */
    public static Point cellIdToLatLon(Long cellId) {
        S2LatLng s2LatLng = new S2CellId(cellId).toLatLng();
        double lat = s2LatLng.latDegrees();
        double lng = s2LatLng.lngDegrees();
        return Point.get(lng, lat);
    }

    /**
     * cellToken 转 经纬度
     *
     * @param cellToken
     * @return
     */
    public static Point cellTokenToLatLon(String cellToken) {
        S2LatLng latLng = new S2LatLng(S2CellId.fromToken(cellToken).toPoint());
        return Point.get(latLng.latDegrees(), latLng.lngDegrees());
    }

    /**
     * 判断region是否包含指定经纬度坐标
     *
     * @param region
     * @param lat
     * @param lon
     * @return
     */
    public static boolean contains(S2Region region, double lat, double lon) {
        S2LatLng s2LatLng = S2LatLng.fromDegrees(lat, lon);
        try {
            boolean contains = region.contains(new S2Cell(s2LatLng));
            return contains;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断当前cellId的level
     *
     * @param cellId
     * @return
     */
    public static int getLevel(long cellId) {
        int n = 0;
        while (cellId % 2 == 0) {
            cellId = cellId / 2;
            n++;
        }
        return 30 - n / 2;
    }

    /**
     * 获取任意形状内所有S2块
     * 可以用于区域内目标检索，根据cellid建立索引,查询区域内cellid in （list）的区域
     *
     * @param vertices 形成多边形的点集合
     * @return
     */
    private static List<Long> vertices(List<Point> vertices) {
        //因为x一般表示经度 y轴表示纬度所以这儿需要参数需要对应一下
        List<S2Point> collect = vertices.stream()
                .map(e -> S2LatLng.fromDegrees(e.getLat().doubleValue(), e.getLng().doubleValue()).toPoint()).collect(Collectors.toList());
        S2Loop s2Loop = new S2Loop(collect);
        S2Polygon cap = new S2Polygon(s2Loop); //创建多边形
        //S2Region cap 任意区域
        S2RegionCoverer coverer = S2RegionCoverer.builder().setMinLevel(7).setMaxLevel(15).setMaxCells(500).build();
        //最小格子和最大格子，总格子数量
        //coverer.setMinLevel(7);//设置最小级别
        //coverer.setMaxLevel(15);//设置最大级别
        //coverer.setMaxCells(500);//设置最大Cell
        List<S2CellId> list = coverer.getCovering(cap).cellIds();
//        for (S2CellId s : list) {
//            System.out.println(s.id());
//        }
        return list.stream().map(S2CellId::id).collect(Collectors.toList());
    }


    /**
     * 不同等级S2块包含的S2子块
     *
     * @param s        自己的点
     * @param level    自己的等级
     * @param desLevel 被计算的格子等级,注意:等级越大算的就越多
     * @return
     */
    public static List<S2CellId> childrenCellId(Point s, Integer level, Integer desLevel) {
        S2LatLng s2LatLng = S2LatLng.fromDegrees(s.getLat().doubleValue(), s.getLng().doubleValue());
        S2CellId cellId = S2CellId.fromLatLng(s2LatLng).parent(level);
        return childrenCellId(cellId, cellId.level(), desLevel);
    }

    //递归调用，每个格子一分为四
    private static List<S2CellId> childrenCellId(S2CellId s2CellId, Integer curLevel, Integer desLevel) {
        if (curLevel < desLevel) {
            //计算当前格子每个格子的差值
            long interval = (s2CellId.childEnd().id() - s2CellId.childBegin().id()) / 4;
            List<S2CellId> s2CellIds = Lists.newArrayList();
            for (int i = 0; i < 4; i++) {
                long id = s2CellId.childBegin().id() + interval * i;
                s2CellIds.addAll(childrenCellId(new S2CellId(id), curLevel + 1, desLevel));
            }
            return s2CellIds;
        } else {
            return Lists.newArrayList(s2CellId);
        }
    }

    /**
     * 任意形状内所有指定等级的S2块
     *
     * @param vertices 多边形的点
     * @param desevel  需要计算的内部的s2块的等级
     * @return
     */
    public static List<S2CellId> childrenCellId(List<Point> vertices, int desevel) {
        List<S2Point> collect = vertices.stream()
                .map(e -> S2LatLng.fromDegrees(e.getLat().doubleValue(), e.getLng().doubleValue()).toPoint()).collect(Collectors.toList());
        S2Loop s2Loop = new S2Loop(collect);
        S2Polygon polygon = new S2Polygon(s2Loop);

        S2RegionCoverer coverer = S2RegionCoverer.builder().setMaxCells(500).setMaxLevel(11).setMinLevel(6).build();
        //设置cell
        //coverer.setMinLevel(6);//设置最小级别  108km~151km
        //coverer.setMaxLevel(11);//设置最大级别 3km~5km
        //coverer.setMaxCells(500);//设置最大Cell
        S2CellUnion covering = coverer.getCovering(polygon);
        List<S2CellId> s2CellIds = covering.cellIds();
        int i = 0;
        List<S2CellId> list = new ArrayList<>();
        for (S2CellId s2CellId : s2CellIds) {
            List<S2CellId> s2CellIds1 = childrenCellId(s2CellId, s2CellId.level(), desevel);
            list.addAll(s2CellIds1);
        }
        return list;
    }

    @Test
    public void test() {
        List<Point> p1 = toListPoint(" [[116.475334, 39.997534], [116.476627, 39.998315], [116.478603, 39.99879], [116.478529, 40.000296], [116.475082, 40.000151], [116.473421, 39.998717]]");
        List<Point> p2 = toListPoint("[[116.475082,40.000151],[116.473687,39.99993],[116.473467,40.000686],[116.476122,40.001]]");

        List<S2Point> collect1 = p1.stream().map(p -> S2LatLng.fromDegrees(p.getLat().doubleValue(), p.getLng().doubleValue()).toPoint()).collect(Collectors.toList());


        List<S2Point> collect2 = p2.stream().map(p -> S2LatLng.fromDegrees(p.getLat().doubleValue(), p.getLng().doubleValue()).toPoint()).collect(Collectors.toList());
        S2Loop s2Loop1 = new S2Loop(collect1);
        S2Loop s2Loop2 = new S2Loop(collect2);


        S2Polygon polygon1 = new S2Polygon(s2Loop1);
        S2Polygon polygon2 = new S2Polygon(s2Loop2);
        log.info("{}", polygon1.intersects(polygon2));


    }

    //public static boolean check(List<Point> )

}

