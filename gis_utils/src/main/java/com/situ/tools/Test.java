/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.google.common.geometry.S2CellId;
import com.google.gson.JsonArray;
import com.situ.entity.bo.Point;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 司徒彬
 * @date 2022/9/19 11:40
 */
@Slf4j
public class Test {

    private static final Integer LEVEL = 14;


    @org.junit.Test
    public void testGeohash() {
        List<String> list = ListUtils.newArrayList("wx4eqx", "wx4eqt", "wx4eqy", "wx4eqq", "wx4eqr", "wx4eqz", "wx4eqv", "wx4eqm", "wx4eqw");
        List<Point> points1 = GisUtils.toListPoint("[[113.556416,34.84386],[113.555419,34.844308],[113.554687,34.843597],[113.556151,34.843328],[113.555877,34.840592],[113.556754,34.839589],[113.556416,34.834001],[113.555418,34.834997],[113.552427,34.834657],[113.549437,34.835074],[113.54844,34.833168],[113.546031,34.833189],[113.545449,34.834559],[113.543455,34.832869],[113.542459,34.833754],[113.541915,34.830612],[113.542231,34.82961],[113.541669,34.828402],[113.535236,34.827873],[113.534365,34.827628],[113.53606,34.827202],[113.53594,34.826163],[113.531443,34.825687],[113.531028,34.824103],[113.530111,34.823635],[113.53108,34.823214],[113.531253,34.822631],[113.529632,34.821635],[113.530096,34.821228],[113.529991,34.820143],[113.52883,34.819959],[113.528507,34.821364],[113.527696,34.815447],[113.519446,34.814657],[113.52012,34.813654],[113.519793,34.811398],[113.517548,34.81107],[113.516527,34.811687],[113.515834,34.808381],[113.51416,34.808266],[113.513563,34.80987],[113.513152,34.808081],[113.511725,34.807673],[113.514409,34.806513],[113.514559,34.805978],[113.515184,34.807035],[113.516809,34.806916],[113.517546,34.803703],[113.518306,34.806891],[113.519724,34.806834],[113.520021,34.804645],[113.520535,34.804025],[113.521193,34.804641],[113.521356,34.806819],[113.523661,34.806773],[113.523569,34.804635],[113.524309,34.80363],[113.524126,34.80263],[113.523523,34.802064],[113.520957,34.801639],[113.523863,34.800971],[113.52452,34.798563],[113.525214,34.800928],[113.527707,34.800817],[113.527839,34.799616],[113.528783,34.798332],[113.525391,34.797621],[113.529759,34.796867],[113.529665,34.794606],[113.530497,34.793311],[113.532179,34.79391],[113.535732,34.793842],[113.535977,34.789584],[113.536475,34.788799],[113.53972,34.78883],[113.540461,34.785012],[113.541013,34.788015],[113.542454,34.788895],[113.544063,34.788562],[113.544018,34.782556],[113.544447,34.781812],[113.545172,34.784828],[113.546646,34.784758],[113.546794,34.782194],[113.544997,34.781553],[113.547437,34.781116],[113.548051,34.780545],[113.548434,34.77902],[113.54917,34.780805],[113.55258,34.780694],[113.552786,34.77653],[113.551884,34.775531],[113.552421,34.772024],[113.55274,34.774528],[113.553993,34.775527],[113.554043,34.780907],[113.559402,34.780898],[113.560398,34.779977],[113.562445,34.780465],[113.563391,34.78143],[113.564492,34.781514],[113.563391,34.781757],[113.563103,34.782228],[113.563392,34.787723],[113.563898,34.786027],[113.56439,34.78578],[113.566836,34.786062],[113.567382,34.787784],[113.568576,34.787711],[113.569378,34.78686],[113.570652,34.786786],[113.571373,34.783481],[113.571951,34.787508],[113.570104,34.788512],[113.570892,34.788995],[113.571157,34.790512],[113.571978,34.790906],[113.572372,34.791691],[113.572862,34.789],[113.575728,34.786501],[113.575055,34.78519],[113.576687,34.784823],[113.57836,34.783279],[113.580577,34.783271],[113.580572,34.784494],[113.57967,34.785808],[113.576844,34.788984],[113.574942,34.790081],[113.574936,34.792078],[113.573198,34.793513],[113.574239,34.794643],[113.577008,34.795865],[113.578199,34.79751],[113.578045,34.799195],[113.576267,34.799417],[113.576084,34.800234],[113.575276,34.800426],[113.5751,34.801251],[113.574285,34.801435],[113.574298,34.803592],[113.578125,34.803754],[113.578237,34.805518],[113.579091,34.805787],[113.57936,34.806663],[113.585352,34.806712],[113.586349,34.800897],[113.587348,34.806735],[113.590344,34.806794],[113.590898,34.806061],[113.592599,34.805763],[113.593342,34.804933],[113.595741,34.804905],[113.597337,34.803815],[113.597819,34.805021],[113.598517,34.805503],[113.595516,34.805682],[113.59434,34.806944],[113.592343,34.807476],[113.591344,34.808189],[113.569325,34.808479],[113.569324,34.809591],[113.571199,34.809709],[113.571316,34.810593],[113.572192,34.810714],[113.572283,34.811533],[113.572193,34.812352],[113.570378,34.812448],[113.569559,34.812359],[113.569296,34.811537],[113.569319,34.813601],[113.571178,34.813736],[113.571276,34.814538],[113.571178,34.81534],[113.569283,34.815541],[113.569158,34.819321],[113.568273,34.819548],[113.568384,34.821686],[113.56867,34.820835],[113.570585,34.820751],[113.570699,34.819865],[113.572549,34.819716],[113.572739,34.818904],[113.573501,34.818666],[113.573777,34.817943],[113.575516,34.817682],[113.575843,34.81701],[113.577542,34.816708],[113.578363,34.815818],[113.581635,34.815803],[113.582356,34.814174],[113.583961,34.814521],[113.583354,34.814666],[113.58283,34.815524],[113.584668,34.816208],[113.585352,34.817178],[113.58635,34.816385],[113.587209,34.816521],[113.586415,34.816587],[113.586419,34.81759],[113.585352,34.818853],[113.584811,34.818066],[113.583538,34.817709],[113.579361,34.817222],[113.578848,34.818017],[113.577209,34.818377],[113.576919,34.819087],[113.575236,34.819404],[113.574985,34.820154],[113.573198,34.820366],[113.573037,34.821206],[113.571005,34.821171],[113.570788,34.821955],[113.570058,34.822225],[113.569381,34.823408],[113.567302,34.823468],[113.567301,34.82464],[113.56812,34.824817],[113.568252,34.825554],[113.568268,34.829674],[113.572549,34.829725],[113.572375,34.82913],[113.570177,34.828757],[113.56996,34.827553],[113.570379,34.827132],[113.573374,34.826912],[113.574755,34.827161],[113.575211,34.829707],[113.577408,34.829587],[113.577946,34.828543],[113.577681,34.827542],[113.578364,34.826611],[113.578915,34.827989],[113.580594,34.82854],[113.579362,34.828812],[113.579072,34.829833],[113.581359,34.830135],[113.582357,34.8292],[113.58271,34.830187],[113.583455,34.830539],[113.578948,34.831128],[113.568384,34.831373],[113.568164,34.833563],[113.567386,34.834343],[113.56661,34.833565],[113.566389,34.831377],[113.564282,34.831455],[113.564194,34.833569],[113.563996,34.834171],[113.563397,34.834371],[113.562797,34.834173],[113.562599,34.833572],[113.562513,34.831456],[113.558409,34.831349],[113.55818,34.838816],[113.563123,34.839577],[113.558155,34.840331],[113.556935,34.841591],[113.556416,34.84386]]");

        List<String> list1 = GisUtils.splitPolygonToGeohash(points1);
        JsonArray collect = list1.stream()
                .map(str -> {
                    Point polygon = GisUtils.getGeoHashCenter(str);
                    JsonArray array = GisUtils.toJsonArray(GisUtils.toRectangleByGeohash(str));
                    return MapBuilders.newInstance().add("hash", str).add("border", array).add("center", polygon.toArray()).get();
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), DataSwitch::convertObjectToJsonArray));
        log.info("{}", collect);

    }

    @org.junit.Test
    public void testSplitToGeoHashThread() {
        try {
            String json = Files.readAllLines(Paths.get("/Users/erebus/Works/Workspaces/小工具/base_tools/gis_utils/src/main/java/com/situ/tools/border.json"))
                    .stream().collect(Collectors.joining());

            GisUtils.splitPolygonToGeohash(GisUtils.toListPoint(json), hash -> {
                log.info(hash);
                return true;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public  void toBorder(){
        List<String> list = ListUtils.newArrayList("wm7ddp", "wm7ddn", "wm7d9y", "wm7ddj", "wm7d9v");
        String s = list.stream()
                .map(geohash -> {
                    List<Point> points = GisUtils.toRectangleByGeohash(geohash);
                    return GisUtils.toJsonArray(points);
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), DataSwitch::convertObjectToJsonArray)).toString();
        log.info(s);
    }


    @org.junit.Test
    public void testGeohashCallback() {
        List<Point> points = GisUtils.toListPoint("[[117.222805,39.164493],[117.228847,39.164083],[117.234706,39.162865],[117.240203,39.160877],[117.245170,39.158178],[117.249458,39.154852],[117.252935,39.150999],[117.255496,39.146736],[117.257063,39.142193],[117.257589,39.137508],[117.257058,39.132823],[117.255487,39.128281],[117.252923,39.124019],[117.249445,39.120168],[117.245157,39.116843],[117.240191,39.114147],[117.234698,39.112160],[117.228843,39.110943],[117.222805,39.110533],[117.216767,39.110943],[117.210912,39.112160],[117.205419,39.114147],[117.200453,39.116843],[117.196165,39.120168],[117.192687,39.124019],[117.190123,39.128281],[117.188552,39.132823],[117.188021,39.137508],[117.188547,39.142193],[117.190114,39.146736],[117.192675,39.150999],[117.196152,39.154852],[117.200440,39.158178],[117.205407,39.160877],[117.210904,39.162865],[117.216763,39.164083]]");

        List<String> list = new ArrayList<>();
        GisUtils.splitPolygonToGeohash(points, (hash) -> {
            log.info(hash);
            list.add(hash);
            return true;
        }, 6,false);
        JsonArray collect = list.stream()
                .map(str -> {
                    Point polygon = GisUtils.getGeoHashCenter(str);
                    JsonArray array = GisUtils.toJsonArray(GisUtils.toRectangleByGeohash(str));
                    return MapBuilders.newInstance().add("hash", str).add("border", array).add("center", polygon.toArray()).get();
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), DataSwitch::convertObjectToJsonArray));
        log.info("{}", collect);

        log.info("{}",GisUtils.toGeohash(Point.get(121.289063,31.289063),12).toBase32());

        log.info( "wtw600000006".substring(0,6));
    }


    @org.junit.Test
    public void testGoogleS2() {
        /**
         addressLng	addressLat
         lng	lat
         Point.get( 116.328138,	40.254076),
         Point.get( 116.327940,	40.253877),
         Point.get( 116.327991,	40.254118),
         Point.get( 116.328147,	40.254134),
         Point.get( 116.328068,	40.254211),
         Point.get(  116.328473,	40.256354),
         Point.get(  116.328210	,40.256179),
         Point.get(  116.328499	4,0.257591)


         11:46:45.250 [main] INFO com.situ.tools.Test - 5857922920264761344
         11:46:45.254 [main] INFO com.situ.tools.Test - 5857923538740051968
         11:46:45.254 [main] INFO com.situ.tools.Test - 5857917663224791040
         11:46:45.254 [main] INFO com.situ.tools.Test - 5858220132001644544
         11:46:45.254 [main] INFO com.situ.tools.Test - 5857917491426099200
         11:46:45.254 [main] INFO com.situ.tools.Test - 5857923538740051968
         11:46:45.255 [main] INFO com.situ.tools.Test - 5858204292162256896
         11:46:45.255 [main] INFO com.situ.tools.Test - 5857917766304006144
         11:46:45.255 [main] INFO com.situ.tools.Test - 5857923916697174016
         11:46:45.255 [main] INFO com.situ.tools.Test - 5857923744898482176
         */

        List<Point> points = ListUtils.newArrayList(
                Point.get(116.328138, 40.254076),
                Point.get(116.327940, 40.253877),
                Point.get(116.327991, 40.254118),
                Point.get(116.328147, 40.254134),
                Point.get(116.328068, 40.254211),
                Point.get(116.328473, 40.256354),
                Point.get(116.328210, 40.256179),
                Point.get(116.328499, 40.257591)
        );


        String p = "[[116.327373,40.258138],[116.344039,40.258138],[116.344037,40.245420],[116.327375,40.245420]]";

        List<S2CellId> list = GoogleS2.childrenCellId(GisUtils.toListPoint(p), 13);

        for (S2CellId item : list) {

            log.info("{}-{}-{}", item.toToken(), item.childBegin(LEVEL).id(), item.childEnd(LEVEL).id());
        }
        String collect1 = list.stream()//.filter(item -> cap.contains(item.toPoint()))

                .map(S2CellId::toToken)
                .collect(Collectors.joining(","));
        log.info(collect1);


    }

    @org.junit.Test
    public void test() {
        List<String> list = new ArrayList<>();
        splitPolygonToGeohash(new ArrayList<>(), (Function<String, Boolean>) list::add);
        splitPolygonToGeohash(new ArrayList<>(), (hash, point) -> {
            log.info(hash + point);
            return true;
        });

        log.info(DataSwitch.convertObjectToJsonArray(list).toString());
    }

    public static void splitPolygonToGeohash(List<Point> polygon, Function<String, Boolean> callback) {

        log.info("{}", callback.getClass().getGenericInterfaces().length);
        callback.apply("ssssss");
    }


    public static void splitPolygonToGeohash(List<Point> polygon, BiFunction<String, Point, Boolean> callback) {
        log.info("{}", callback.getClass().getConstructors().length);
        callback.apply("ssss", Point.get(1, 2));
    }


}
