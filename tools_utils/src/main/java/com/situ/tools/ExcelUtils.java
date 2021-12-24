package com.situ.tools;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 司徒彬
 * @date 2021/11/1 15:42
 */
@Slf4j
public class ExcelUtils {

    /**
     * Export string.
     *
     * @param <T>          the type parameter
     * @param clazz        the clazz
     * @param data         the data
     * @param fileRootPath the file root path
     * @return the string
     * @throws IOException the io exception
     */
    public static <T> String export(Class<T> clazz, Map<String, List<T>> data, String fileRootPath) throws IOException {
        String fileName = DataSwitch.getUUID().concat(".xlsx");
        String write_path = fileRootPath + fileName;
        Files.deleteIfExists(Paths.get(write_path));
        FileUtils.createDirectory(fileRootPath);
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

    public static <T> void export(Class<T> clazz, Map<String, List<T>> data, HttpServletResponse response) throws IOException {
        ExcelWriter writer =  EasyExcel.write(response.getOutputStream(),clazz).autoCloseStream(true).build();

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
}
