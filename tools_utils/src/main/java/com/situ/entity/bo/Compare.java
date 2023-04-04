/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import com.situ.tools.DataSwitch;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Compare.
 *
 * @author 司徒彬
 * @date 2023 /4/3 18:55
 */
@Data
@Slf4j
public class Compare {

    /**
     * Asc integer.
     *
     * @param pair1 the pair 1
     * @param pair2 the pair 2
     * @return the integer
     * @author ErebusST
     * @since 2023 -04-04 10:04:07
     */
    public static Integer asc(@Nonnull Pair pair1, @Nonnull Pair pair2) {
        Object key1 = pair1.getLeft();
        Object key2 = pair2.getRight();
        return asc(key1, key2);
    }

    /**
     * Desc integer.
     *
     * @param pair1 the pair 1
     * @param pair2 the pair 2
     * @return the integer
     * @author ErebusST
     * @since 2023 -04-04 10:04:09
     */
    public static Integer desc(@Nonnull Pair pair1, @Nonnull Pair pair2) {
        Object key1 = pair1.getLeft();
        Object key2 = pair2.getRight();
        return desc(key1, key2);
    }

    /**
     * Asc integer.
     *
     * @param key1 the key 1
     * @param key2 the key 2
     * @return the integer
     * @author ErebusST
     * @since 2023 -04-04 10:04:11
     */
    public static Integer asc(@Nonnull Object key1, @Nonnull Object key2) {
        if (String.class.equals(key1.getClass())) {
            return ((String) key1).trim().compareTo(key2.toString());
        }
        BigDecimal value1 = DataSwitch.convertObjectToBigDecimal(key1, 10, BigDecimal.ZERO);
        BigDecimal value2 = DataSwitch.convertObjectToBigDecimal(key2, 10, BigDecimal.ZERO);
        return value1.compareTo(value2);
    }

    /**
     * Desc integer.
     *
     * @param key1 the key 1
     * @param key2 the key 2
     * @return the integer
     * @author ErebusST
     * @since 2023 -04-04 10:04:13
     */
    public static Integer desc(@Nonnull Object key1, @Nonnull Object key2) {
        return -asc(key1, key2);
    }


    /**
     * Test .
     *
     * @author ErebusST
     * @since 2023 -04-04 14:47:19
     */
    @Test
    public void test() {
        List<Integer> list = new ArrayList<>();
        list.add(22);
        list.add(555);
        list.add(111);
        list.add(534);
        List<Integer> collect = list.stream().sorted(Compare::desc).collect(Collectors.toList());
        log.info("{}", DataSwitch.convertObjectToJsonElement(collect));
    }
}
