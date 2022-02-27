/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The type Number utils.
 *
 * @author 司徒彬
 * @date 2022 /2/25 17:48
 */
@Slf4j
public class NumberUtils {

    /**
     * The constant THOUSAND.
     */
    public final static BigDecimal THOUSAND = BigDecimal.valueOf(1000);
    /**
     * The constant TEN_THOUSAND.
     */
    public final static BigDecimal TEN_THOUSAND = BigDecimal.valueOf(10000);
    /**
     * The constant NUMBER_SCALE.
     */
    public final static Integer NUMBER_SCALE = 0;
    /**
     * The constant AMOUNT_SCALE.
     */
    public final static Integer AMOUNT_SCALE = 2;
    /**
     * The constant PERCENT_SCALE.
     */
    public final static Integer PERCENT_SCALE = 4;

    /**
     * Add big decimal.
     * 加法
     *
     * @param values the values
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-25 17:59:48
     */
    public static BigDecimal add(Object... values) {
        Stream<BigDecimal> result = preExecute(values);
        return add(result);
    }

    /**
     * Add big decimal.
     * 加法
     *
     * @param values the values
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-25 18:14:36
     */
    public static BigDecimal add(Stream<BigDecimal> values) {
        return values.reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Subtract big decimal.
     * 减法
     *
     * @param value1 the value 1
     * @param value2 the value 2
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-25 18:00:56
     */
    public static BigDecimal subtract(Object value1, Object value2) {
        return operation(value1, value2, NumberOperation.SUBTRACT);
    }

    /**
     * Multiply big decimal.
     * <p>
     * 乘法
     *
     * @param values the values
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-25 18:03:44
     */
    public static BigDecimal multiply(Object... values) {
        return preExecute(values)
                .reduce((item1, item2) -> {
                    BigDecimal result = operation(item1, item2, NumberOperation.MULTIPLY);
                    return result;
                })
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Remainder big decimal.
     * <p>
     * 取余
     *
     * @param value1 the value 1
     * @param value2 the value 2
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-25 18:14:36
     */
    public static BigDecimal remainder(Object value1, Object value2) {
        return operation(value1, value2, NumberOperation.REMAINDER);
    }

    /**
     * Divide big decimal.
     * <p>
     * 除法
     *
     * @param value1 the value 1
     * @param value2 the value 2
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-25 18:04:43
     */
    public static BigDecimal divide(Object value1, Object value2) {
        return operation(value1, value2, NumberOperation.DIVIDE);
    }

    /**
     * Total big decimal.
     * <p>
     * 总数
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-25 18:14:36
     */
    public static <T> BigDecimal total(List<T> list, Function<T, BigDecimal> getter) {
        return add(list.stream().map(getter));
    }

    /**
     * Total big decimal.
     * <p>
     * 总数
     *
     * @param list the list
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-25 18:14:37
     */
    public static BigDecimal total(List<BigDecimal> list) {
        return add(list.stream());
    }

    /**
     * Total big decimal.
     * <p>
     * 总数
     *
     * @param stream the stream
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-22 15:01:11
     */
    public static BigDecimal total(Stream<BigDecimal> stream) {
        return add(stream);
    }

    /**
     * Percent big decimal.
     * <p>
     * 百分比
     *
     * @param value the value
     * @param total the total
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-25 18:07:07
     */
    public static BigDecimal percent(Object value, Object total) {
        return divide(value, total).setScale(PERCENT_SCALE, BigDecimal.ROUND_HALF_UP);
    }


    private static Stream<BigDecimal> preExecute(Object... values) {
        return Arrays.stream(values)
                .filter(ObjectUtils::isNotNull)
                .map(DataSwitch::convertObjectToBigDecimal);
    }


    private static BigDecimal operation(Object value1, Object value2, NumberOperation operation) {
        BigDecimal decimal1 = DataSwitch.convertObjectToBigDecimal(value1);
        BigDecimal decimal2 = DataSwitch.convertObjectToBigDecimal(value2);
        switch (operation) {
            case ADD:
                return decimal1.add(decimal2);
            case SUBTRACT:
                return decimal1.subtract(decimal2);
            case MULTIPLY:
                return decimal1.multiply(decimal2);
            case DIVIDE:
                if (decimal2.compareTo(BigDecimal.ZERO) == 0) {
                    return BigDecimal.ZERO;
                }
                return decimal1.divide(decimal2, 10, BigDecimal.ROUND_HALF_UP);
            case REMAINDER:
                return decimal1.remainder(decimal2);
            default:
                log.error(StringUtils.format("出现未知的运算符:{}", operation));
                return BigDecimal.ZERO;
        }

    }

    private enum NumberOperation {
        /**
         * Add number operation.
         */
        ADD,
        /**
         * Subtract number operation.
         */
        SUBTRACT,
        /**
         * Multiply number operation.
         */
        MULTIPLY,
        /**
         * Divide number operation.
         */
        DIVIDE,
        /**
         * Remainder number operation.
         */
        REMAINDER;
    }


    /**
     * Calc square deviation big decimal.
     * 计算方差
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 10:54:34
     */
    public static <T> BigDecimal calcVariance(List<T> list, Function<T, ? extends Number> getter) {
        return calcVariance(list.stream().map(getter));
    }

    /**
     * Calc square deviation big decimal.
     * 计算方差
     *
     * @param list the list
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 10:54:36
     */
    public static BigDecimal calcVariance(List<? extends Number> list) {
        return calcVariance(list.stream());
    }

    /**
     * Calc square deviation big decimal.
     * 计算方差
     *
     * @param stream the stream
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 10:54:48
     */
    public static BigDecimal calcVariance(Stream<? extends Number> stream) {
        BigDecimal total = calcAverage(
                stream
                        .filter(ObjectUtils::isNotNull)
                        .map(DataSwitch::convertObjectToBigDecimal)
                        .map(item -> item.multiply(item)));
        return total;
    }

    /**
     * Calc average big decimal.
     * <p>
     * 计算平均数
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 11:16:24
     */
    public static <T> BigDecimal calcAverage(List<T> list, Function<T, ? extends Number> getter) {
        return calcAverage(list.stream().map(getter));
    }


    /**
     * Calc average big decimal.
     * <p>
     * 计算平均数
     *
     * @param list the list
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 11:16:24
     */
    public static BigDecimal calcAverage(List<? extends Number> list) {
        return calcAverage(list.stream());
    }

    /**
     * Calc average big decimal.
     * <p>
     * 计算平均数
     *
     * @param stream the stream
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 11:08:23
     */
    public static BigDecimal calcAverage(Stream<? extends Number> stream) {
        double average = stream.mapToDouble(Number::doubleValue)
                .average().orElse(0D);
        return BigDecimal.valueOf(average);
    }

    /**
     * Calc standard deviation big decimal.
     * 计算标准差
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 10:57:20
     */
    public static <T> BigDecimal calcStandardDeviation(List<T> list, Function<T, ? extends Number> getter) {
        return calcStandardDeviation(list.stream().map(getter));
    }


    /**
     * Calc standard deviation big decimal.
     * <p>
     * 计算标准差
     *
     * @param list the list
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 11:12:40
     */
    public static BigDecimal calcStandardDeviation(List<? extends Number> list) {
        return calcStandardDeviation(list.stream());
    }

    /**
     * Calc standard deviation big decimal.
     * 计算标准差
     *
     * @param stream the stream
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 10:56:05
     */
    public static BigDecimal calcStandardDeviation(Stream<? extends Number> stream) {
        BigDecimal squareDeviation = calcVariance(stream);
        return BigDecimal.valueOf(Math.sqrt(squareDeviation.doubleValue()));
    }


    /**
     * Standard normal distribution double.
     * 计算标准正态分布
     *
     * @param value             the value
     * @param average           the 算术平均数
     * @param standardDeviation the 标准差
     * @return the double
     * @author ErebusST
     * @since 2022 -02-25 17:33:03
     */
    public static double calcStandardNormalDistribution(double value, double average, double standardDeviation) {
        double oor2pi = 1 / Math.sqrt(2.0 * Math.PI);
        double x2 = (value - average) / standardDeviation;
        double result;
        if (x2 == 0) {
            result = 0.5;
        } else {
            double t = 1 / (1.0 + 0.2316419 * Math.abs(x2));
            t = t * oor2pi * Math.exp(-0.5 * x2 * x2)
                    * (0.31938153 + t
                    * (-0.356563782 + t
                    * (1.781477937 + t
                    * (-1.821255978 + t * 1.330274429))));
            if (x2 > 0) {
                result = 1.0 - t;
            } else {
                result = t;
            }
        }
        return result;
    }
}
