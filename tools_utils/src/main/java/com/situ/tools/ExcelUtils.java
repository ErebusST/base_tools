package com.situ.tools;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.situ.annotation.Fill;
import com.situ.entity.bo.ExportExcelSetting;
import com.situ.handle.MergeHandle;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The type Excel utils.
 *
 * @author 司徒彬
 * @date 2021 /11/1 15:42
 */
@Slf4j
public class ExcelUtils {
    /**
     * Fill .
     *
     * @param <ExcelData> the type parameter
     * @param clazz       the clazz
     * @param template    the template
     * @param target      the target
     * @param data        the data
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -05-15 21:49:46
     */
    public static <ExcelData> void fill(Class<ExcelData> clazz, String template, String target, ExcelData data) {
        FileUtils.deleteFile(target);
        ExcelWriterBuilder builder = EasyExcel.write(target).withTemplate(template);
        fill(clazz, ListUtils.newArrayList(data), builder);
    }

    /**
     * Fill .
     *
     * @param <ExcelData> the type parameter
     * @param clazz       the clazz
     * @param template    the template
     * @param target      the target
     * @param dataSet     the data set
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -05-15 21:49:48
     */
    public static <ExcelData> void fill(Class<ExcelData> clazz, String template, String target, List<ExcelData> dataSet) {
        FileUtils.deleteFile(target);
        ExcelWriterBuilder builder = EasyExcel.write(target).withTemplate(template);
        fill(clazz, dataSet, builder);
    }

    /**
     * Fill .
     *
     * @param <ExcelData> the type parameter
     * @param clazz       the clazz
     * @param template    the template
     * @param fileName    the file name
     * @param data        the data
     * @param response    the response
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -05-15 21:50:38
     */
    public static <ExcelData> void fill(Class<ExcelData> clazz, String template, String fileName, ExcelData data, HttpServletResponse response) throws IOException {
        fill(clazz, template, fileName, ListUtils.newArrayList(data), response);
    }

    /**
     * Fill .
     *
     * @param <ExcelData> the type parameter
     * @param clazz       the clazz
     * @param fileName    the file name
     * @param dataSet     the data set
     * @param response    the response
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -05-15 21:49:49
     */
    public static <ExcelData> void fill(Class<ExcelData> clazz, String template, String fileName, List<ExcelData> dataSet, HttpServletResponse response) throws IOException {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        //response.setContentType("application/octet-stream");
        //response.setContentType("application/vnd.ms-excel");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        ExcelWriterBuilder builder = EasyExcel.write(response.getOutputStream()).withTemplate(template);
        fill(clazz, dataSet, builder);
    }

    /**
     * Create excel.
     *
     * @param <ExcelData> the type parameter
     * @param clazz       the clazz
     * @param dataSet     the data set
     * @param builder     the builder
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -05-15 21:48:08
     */
    public static <ExcelData> void fill(Class<ExcelData> clazz, List<ExcelData> dataSet, ExcelWriterBuilder builder) {
        ExcelWriter writer = builder.autoCloseStream(true)
                .excelType(ExcelTypeEnum.XLSX)
                .registerWriteHandler(new MergeHandle()).build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();

        Field[] fields = ReflectionUtils.getFields(clazz);
        int size = dataSet.size();
        for (int i = 0; i < size; i++) {
            ExcelData data = dataSet.get(i);
            WriteSheet sheet = EasyExcel.writerSheet(i).build();
            Map<String, Object> map = new HashMap<>(fields.length);
            for (Field field : fields) {
                String fieldName = field.getName();
                Object fieldValue = ReflectionUtils.getFieldValue(data, field.getName());
                if (ObjectUtils.isNull(fieldValue)) {
                    continue;
                }
                Class<?> fieldClass = fieldValue.getClass();
                if (fieldClass.equals(List.class) || fieldClass.equals(ArrayList.class)) {
                    Fill fill = field.getAnnotation(Fill.class);
                    String prefix;
                    if (ObjectUtils.isNotNull(fill)) {
                        prefix = fill.prefix();
                    } else {
                        prefix = fieldName;
                    }
                    FillWrapper fillWrapper = new FillWrapper(prefix, (List) fieldValue);
                    writer.fill(fillWrapper, fillConfig, sheet);
                } else {
                    map.put(fieldName, fieldValue);
                }
            }

            if (map.size() > 0) {
                writer.fill(map, sheet);
            }
        }

        writer.finish();

    }


    /**
     * Export string.
     *
     * @param settings the settings
     * @param filePath the file root path
     * @return the string
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-21 17:18:47
     */
    public static String export(List<ExportExcelSetting> settings, String filePath) throws IOException {
        //String fileName = DataSwitch.getUUID().concat(".xlsx");
        String write_path = filePath;
        Files.deleteIfExists(Paths.get(write_path));
        FileUtils.createDirectory(filePath);
        ExcelWriter writer = EasyExcel.write(write_path).autoCloseStream(true).build();

        AtomicInteger sheetIndex = new AtomicInteger(0);

        for (ExportExcelSetting setting : settings) {
            Class clazz = setting.getClazz();
            List list = setting.getData();
            String sheetName = setting.getSheetName();
            if (list.size() > 0) {
                WriteSheet sheet = EasyExcel.writerSheet(sheetIndex.getAndIncrement(), sheetName)
                        .head(clazz)
                        .useDefaultStyle(false)
                        .needHead(true)
                        .build();
                writer.write(list, sheet);

            }
        }
        writer.finish();
        return write_path;
    }

    /**
     * Export string.
     *
     * @param <T>      the type parameter
     * @param clazz    the clazz
     * @param data     the data
     * @param filePath the file root path
     * @return the string
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:06
     */
    public static <T> String export(Class<T> clazz, Map<String, List<T>> data, String filePath) throws IOException {
        //String fileName = DataSwitch.getUUID().concat(".xlsx");
        //Files.deleteIfExists(Paths.get(filePath));
        String write_path = filePath;
        Files.deleteIfExists(Paths.get(write_path));
        FileUtils.createDirectory(filePath);
        ExcelWriter writer = EasyExcel.write(write_path).autoCloseStream(true).build();

        AtomicInteger sheetIndex = new AtomicInteger(0);

        for (String sheetName : data.keySet()) {
            List<T> list = data.get(sheetName);
            if (list.size() > 0) {
                WriteSheet sheet = EasyExcel.writerSheet(sheetIndex.getAndIncrement(), sheetName)
                        .head(clazz)
                        .useDefaultStyle(false)
                        .needHead(true)
                        .build();
                writer.write(list, sheet);
                writer.finish();
            }
        }
        return write_path;
    }


    /**
     * Export .
     *
     * @param <T>      the type parameter
     * @param clazz    the clazz
     * @param list     the list
     * @param filePath the file path
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -05-10 15:34:45
     */
    public static <T> void export(Class<T> clazz, List<T> list, String filePath) throws IOException {
        Map<String, List<T>> data = new HashMap<>();
        data.put("sheet", list);
        export(clazz, data, filePath);
    }

    /**
     * Export .
     *
     * @param <T>      the type parameter
     * @param clazz    the clazz
     * @param data     the data
     * @param response the response
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:06
     */
    public static <T> void export(Class<T> clazz, Map<String, List<T>> data, HttpServletResponse response) throws IOException {
        ExcelWriter writer = EasyExcel.write(response.getOutputStream(), clazz).autoCloseStream(true).build();

        AtomicInteger sheetIndex = new AtomicInteger(0);

        for (String sheetName : data.keySet()) {
            List<T> list = data.get(sheetName);
            if (list.size() > 0) {
                WriteSheet sheet = EasyExcel.writerSheet(sheetIndex.getAndIncrement(), sheetName)
                        .head(clazz)
                        .useDefaultStyle(false)
                        .needHead(true)
                        .build();
                writer.write(list, sheet);
                writer.finish();
            }
        }
    }

    /**
     * Index integer.
     *
     * @param column the column
     * @return the integer
     * @author ErebusST
     * @since 2022 -01-07 15:36:06
     */
    public static Integer index(String column) {
        int length = column.length();
        return IntStream.range(0, length)
                .mapToObj(index -> {
                    char ch = column.charAt(length - index - 1);
                    int num = (int) (ch - 'A' + 1);
                    num *= Math.pow(26, index);
                    return num;
                })
                .mapToInt(num -> num)
                .sum() - 1;
    }


    /**
     * Import data .
     *
     * @param path       the path
     * @param sheetIndex the sheet index 从0开始
     * @param callback   the callback
     * @author ErebusST
     * @since 2023 -02-27 12:34:53
     */
    public static void importData(String path, Integer sheetIndex, Function<LinkedHashMap<Integer, String>, Boolean> callback) {
        if (!FileUtils.isExist(path)) {
            return;
        }
        ExcelReaderBuilder builder = EasyExcel
                .read(path, new AnalysisEventListener<LinkedHashMap<Integer, String>>() {
                    @Override
                    public void invoke(LinkedHashMap<Integer, String> item, AnalysisContext analysisContext) {
                        Boolean success = callback.apply(item);
                        if (!success) {
                            throw new RuntimeException(StringUtils.format("执行处理程序失败：{}", item.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.joining(","))));
                        }
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                    }
                });
        builder.ignoreEmptyRow(true);
        builder.autoCloseStream(true);
        builder.autoTrim(true);
        builder.excelType(ExcelTypeEnum.XLSX);
        ExcelReader reader = builder.build();

        reader.read(new ReadSheet(sheetIndex));
        reader.finish();
        log.info("导入数据成功");
    }
}
