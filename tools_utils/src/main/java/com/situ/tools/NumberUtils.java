/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.situ.entity.bo.DataItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
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
     * Init operation.
     *
     * @param value the value
     * @return the operation
     * @author ErebusST
     * @since 2022 -02-27 12:31:47
     */
    public static Operation init(Object value) {
        return new Operation(value);
    }


    /**
     * The type Operation.
     */
    public static class Operation {
        private BigDecimal value = BigDecimal.ZERO;

        /**
         * Instantiates a new Operation.
         *
         * @param value the value
         */
        public Operation(Object value) {
            this.value = DataSwitch.convertObjectToBigDecimal(value);
        }

        /**
         * Add operation.
         *
         * @param values the values
         * @return the operation
         * @author ErebusST
         * @since 2022 -02-27 12:31:48
         */
        public Operation add(Object... values) {
            Object[] temp = ArrayUtils.add(values, value);
            this.value = NumberUtils.add(temp);
            return this;
        }

        /**
         * Subtract operation.
         *
         * @param value the value
         * @return the operation
         * @author ErebusST
         * @since 2022 -02-27 12:31:48
         */
        public Operation subtract(Object value) {
            this.value = NumberUtils.subtract(this.value, value);
            return this;
        }

        /**
         * Multiply operation.
         *
         * @param values the values
         * @return the operation
         * @author ErebusST
         * @since 2022 -02-27 12:31:48
         */
        public Operation multiply(Object... values) {
            Object[] temp = ArrayUtils.add(values, value);
            this.value = NumberUtils.multiply(temp);
            return this;
        }

        /**
         * Divide operation.
         *
         * @param value the value
         * @return the operation
         * @author ErebusST
         * @since 2022 -02-27 12:31:48
         */
        public Operation divide(Object value) {
            this.value = NumberUtils.divide(this.value, value);
            return this;
        }

        /**
         * Get big decimal.
         *
         * @return the big decimal
         * @author ErebusST
         * @since 2022 -02-27 12:31:48
         */
        public BigDecimal get() {
            return value;
        }
    }

    /**
     * The constant Hundred.
     */
    public final static BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /**
     * The constant THOUSAND.
     */
    public final static BigDecimal THOUSAND = BigDecimal.valueOf(1000);
    /**
     * The constant TEN_THOUSAND.
     */
    public final static BigDecimal TEN_THOUSAND = BigDecimal.valueOf(10000);

    public final static BigDecimal NEGATIVE_ONE = BigDecimal.valueOf(-1);
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
        return values.filter(ObjectUtils::isNotNull).reduce(BigDecimal.ZERO, BigDecimal::add);
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
        return divide(value1, value2, null, null);
    }

    /**
     * Divide big decimal.
     * <p>
     * 除法
     *
     * @param value1 the value 1
     * @param value2 the value 2
     * @param scale  the scale
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 11:54:56
     */
    public static BigDecimal divide(Object value1, Object value2, Integer scale) {
        return divide(value1, value2, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Divide big decimal.
     *
     * @param value1       the value 1
     * @param value2       the value 2
     * @param scale        the scale
     * @param roundingMode the rounding mode
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-19 17:34:02
     */
    public static BigDecimal divide(Object value1, Object value2, Integer scale, Integer roundingMode) {
        BigDecimal operation = operation(value1, value2, NumberOperation.DIVIDE);
        if (ObjectUtils.isNotNull(scale) && ObjectUtils.isNotNull(roundingMode)) {
            return operation.setScale(scale, roundingMode);
        } else {
            return operation;
        }
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
    //public static <T> BigDecimal total(List<T> list, Function<T, BigDecimal> getter) {
    //    return add(list.stream().map(getter));
    //}
    public static <T, E extends Number> BigDecimal total(List<T> list, Function<T, E> getter) {
        Stream<BigDecimal> stream = list.stream().map(item -> getter.apply(item)).map(DataSwitch::convertObjectToBigDecimal);
        return add(stream);
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
        if (ObjectUtils.isNull(value1)) {
            value1 = BigDecimal.ZERO;
        }
        if (ObjectUtils.isNull(value2)) {
            value2 = BigDecimal.ZERO;
        }
        BigDecimal decimal1 = DataSwitch.convertObjectToBigDecimal(value1);
        if (ObjectUtils.isNull(decimal1)) {
            decimal1 = BigDecimal.ZERO;
        }
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
                return decimal1.divide(decimal2, 20, BigDecimal.ROUND_HALF_UP);
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
        return calcAverage(list.stream(), getter);
    }

    /**
     * Calc average big decimal.
     *
     * @param <T>    the type parameter
     * @param stream the stream
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-19 18:05:37
     */
    public static <T> BigDecimal calcAverage(Stream<T> stream, Function<T, ? extends Number> getter) {
        return calcAverage(stream, getter, null);
    }

    /**
     * Calc average big decimal.
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @param scale  the scale
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-19 17:59:47
     */
    public static <T> BigDecimal calcAverage(List<T> list, Function<T, ? extends Number> getter, Integer scale) {
        return calcAverage(list.stream(), getter, scale);
    }

    /**
     * Calc average big decimal.
     *
     * @param <T>    the type parameter
     * @param stream the stream
     * @param getter the getter
     * @param scale  the scale
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-19 18:04:28
     */
    public static <T> BigDecimal calcAverage(Stream<T> stream, Function<T, ? extends Number> getter, Integer scale) {
        return calcAverage(stream, getter, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calc average big decimal.
     *
     * @param <T>          the type parameter
     * @param list         the list
     * @param getter       the getter
     * @param scale        the scale
     * @param roundingMode the rounding mode
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-19 17:56:11
     */
    public static <T> BigDecimal calcAverage(List<T> list, Function<T, ? extends Number> getter, Integer scale, Integer roundingMode) {
        BigDecimal result = calcAverage(list.stream(), getter, scale, roundingMode);
        return result;
    }

    /**
     * Calc average big decimal.
     *
     * @param <T>          the type parameter
     * @param stream       the stream
     * @param getter       the getter
     * @param scale        the scale
     * @param roundingMode the rounding mode
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-19 18:03:45
     */
    public static <T> BigDecimal calcAverage(Stream<T> stream, Function<T, ? extends Number> getter, Integer scale, Integer roundingMode) {
        BigDecimal result = calcAverage(stream.map(getter), scale, roundingMode);
        return result;
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
    public static BigDecimal calcAverage(List<? extends Number> list, Integer scale) {
        return calcAverage(list, scale, BigDecimal.ROUND_HALF_UP);
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
    public static BigDecimal calcAverage(List<? extends Number> list, Integer scale, Integer roundingMode) {
        return calcAverage(list.stream(), scale, roundingMode);
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
        return calcAverage(stream, 10);
    }

    /**
     * Calc average big decimal.
     *
     * @param stream the stream
     * @param scale  the scale
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-19 17:58:12
     */
    public static BigDecimal calcAverage(Stream<? extends Number> stream, Integer scale) {
        return calcAverage(stream, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calc average big decimal.
     *
     * @param stream       the stream
     * @param scale        the scale
     * @param roundingMode the rounding mode
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -03-19 17:58:13
     */
    public static BigDecimal calcAverage(Stream<? extends Number> stream, Integer scale, Integer roundingMode) {
        double average = stream.mapToDouble(Number::doubleValue)
                .average().orElse(0D);
        BigDecimal result = BigDecimal.valueOf(average);
        if (ObjectUtils.isNotNull(scale)) {
            result = result.setScale(scale, roundingMode);
        }
        return result;
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
     * 计算标准正态分布密度
     *
     * @param value             the value
     * @param average           the 算术平均数
     * @param standardDeviation the 标准差
     * @return the double
     * @author ErebusST
     * @since 2022 -02-25 17:33:03
     */
    public static double calcStdDensity(double value, double average, double standardDeviation) {
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


    /**
     * Fix percent list.
     *
     * @param list the list
     * @return the list
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static List<DataItem> fixPercent(List<DataItem> list) {
        return fixPercent(list, false);
    }


    /**
     * Fix percent list.
     *
     * @param list   the list
     * @param sorted the sorted
     * @return the list
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static List<DataItem> fixPercent(List<DataItem> list, boolean sorted) {
        BigDecimal total = total(list.stream().map(DataItem::getCount));
        if (total.equals(BigDecimal.ZERO)) {
            return list;
        }
        BigDecimal temp = list
                .stream()
                .map(item -> {
                    BigDecimal percent = item.getPercent();
                    if (ObjectUtils.isNull(percent)) {
                        percent = NumberUtils.percent(item.getCount(), total);
                        item.setPercent(percent);
                    }
                    return percent;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal diff = BigDecimal.ONE.subtract(temp);

        if (BigDecimal.ZERO.compareTo(diff) != 0) {
            list.stream()
                    .filter(item -> {
                        BigDecimal percent = item.getPercent();
                        return BigDecimal.ZERO.compareTo(percent) < 0 && percent.compareTo(diff.abs()) > 0;
                    })
                    .collect(Collectors.groupingBy(DataItem::getCount))
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparing(entry -> entry.getValue().size()))
                    .filter(entry -> entry.getKey().doubleValue() > 0D)
                    .flatMap(entry -> entry.getValue().stream())
                    .findFirst()
                    .ifPresent(item -> {
                        BigDecimal percent = DataSwitch.convertObjectToBigDecimal(item.getPercent(), 4);
                        percent = percent.add(diff);
                        item.setPercent(percent);
                    });
        }

        if (sorted) {
            list = list.stream().sorted(Comparator.comparing(DataItem::getCount).reversed()).collect(Collectors.toList());
        }
        return list;
    }

    /**
     * Min big decimal.
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static <T> BigDecimal min(List<T> list, Function<T, ? extends Number> getter) {
        return min(list.stream(), getter);

    }

    /**
     * Min big decimal.
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static <T> BigDecimal min(Stream<T> list, Function<T, ? extends Number> getter) {
        return minOrMax(list, getter, Type.min);
    }

    /**
     * Max big decimal.
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static <T> BigDecimal max(List<T> list, Function<T, ? extends Number> getter) {
        return max(list.stream(), getter);
    }

    /**
     * Max big decimal.
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static <T> BigDecimal max(Stream<T> list, Function<T, ? extends Number> getter) {
        return minOrMax(list, getter, Type.max);
    }

    /**
     * Min or max big decimal.
     *
     * @param <T>    the type parameter
     * @param stream the stream
     * @param getter the getter
     * @param type   the type
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static <T> BigDecimal minOrMax(Stream<T> stream, Function<T, ? extends Number> getter, Type type) {
        T entity = minOrMaxEntity(stream, getter, type);
        if (ObjectUtils.isNotNull(entity)) {
            return DataSwitch.convertObjectToBigDecimal(getter.apply(entity));
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Min big decimal.
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static <T> BigDecimal minEntity(List<T> list, Function<T, ? extends Number> getter) {
        return minEntity(list.stream(), getter);

    }

    /**
     * Min big decimal.
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static <T> BigDecimal minEntity(Stream<T> list, Function<T, ? extends Number> getter) {
        return minOrMax(list, getter, Type.min);
    }

    /**
     * Max big decimal.
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static <T> T maxEntity(List<T> list, Function<T, ? extends Number> getter) {
        return maxEntity(list.stream(), getter);
    }

    /**
     * Max big decimal.
     *
     * @param <T>    the type parameter
     * @param list   the list
     * @param getter the getter
     * @return the big decimal
     * @author ErebusST
     * @since 2022 -02-27 14:54:15
     */
    public static <T> T maxEntity(Stream<T> list, Function<T, ? extends Number> getter) {
        return minOrMaxEntity(list, getter, Type.max);
    }


    /**
     * Min or max entity t.
     *
     * @param <T>    the type parameter
     * @param stream the stream
     * @param getter the getter
     * @param type   the type
     * @return the t
     * @author ErebusST
     * @since 2022 -09-13 18:19:41
     */
    public static <T> T minOrMaxEntity(Stream<T> stream, Function<T, ? extends Number> getter, Type type) {
        Stream<T> bigDecimalStream = stream
                .filter(ObjectUtils::isNotNull)
                .filter(item -> {
                    Number value = getter.apply(item);
                    return ObjectUtils.isNotNull(value);
                });
        switch (type) {
            case max:
                return bigDecimalStream.max(Comparator.comparing(item -> getter.apply(item).doubleValue())).orElse(null);
            case min:
                return bigDecimalStream.min(Comparator.comparing(item -> getter.apply(item).doubleValue())).orElse(null);
            default:
                log.error("未知的操作类型:" + type);
                return null;

        }
    }

    private enum Type {
        /**
         * Min type.
         */
        min,
        /**
         * Max type.
         */
        max
    }
}
