/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.situ.tools.StaticValue.ENCODING;

/**
 * @author 司徒彬
 * @date 2023/2/27 15:16
 */
@Slf4j
public class CsvUtils {

    private static final String SPLIT = ",";

    /**
     * Read boolean.
     *
     * @param file     the file
     * @param callback the callback
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2023 -02-27 18:54:44
     */
    public static boolean read(String file, Function<List<String>, Boolean> callback) throws Exception {
        return read(file, 1L, callback);
    }


    /**
     * Read boolean.
     *
     * @param file     the file
     * @param skip     the skip
     * @param callback the callback
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2023 -02-27 15:27:36
     */
    public static boolean read(String file, Long skip, Function<List<String>, Boolean> callback) throws Exception {
        Path path = Paths.get(file);
        if (!Files.exists(path)) {
            return false;
        }

        try (CSVReader csvReader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(Files.newInputStream(new File(file).toPath()), Charset.forName(ENCODING)))).build()) {
            Iterator<String[]> iterator = csvReader.iterator();
            for (int i = 0; i < skip; i++) {
                iterator.next();
            }
            while (iterator.hasNext()) {
                List<String> row = Arrays.stream(iterator.next()).collect(Collectors.toList());
                Boolean success = callback.apply(row);
                if (!success) {
                    throw new RuntimeException("读取csv出现异常:{}" + row.stream().collect(Collectors.joining(",")));
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return true;

        //List<Boolean> status = new ArrayList<>(100);
        //Files.lines(path)
        //        .skip(skip)
        //        .forEach(str -> {
        //            List<String> row = StringUtils.splitToList(str, split, false);
        //            Boolean success = callback.apply(row);
        //            if (!success) {
        //                throw new RuntimeException(StringUtils.format("执行处理程序失败：{}", str));
        //            }
        //            status.add(success);
        //        });
        //Map<Boolean, List<Boolean>> collect = status.stream().collect(Collectors.groupingBy(str -> str));
        //String result = collect
        //        .entrySet()
        //        .stream()
        //        .map(entry -> {
        //            String state = entry.getKey() ? "success" : "error";
        //            int count = entry.getValue().size();
        //            return StringUtils.concat(state, ":", count);
        //        })
        //        .collect(Collectors.joining(" "));
        //log.info(result);
        //
        //return !collect.containsKey(false);

    }
}
