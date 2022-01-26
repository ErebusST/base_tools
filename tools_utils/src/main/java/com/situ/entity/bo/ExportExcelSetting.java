/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 司徒彬
 * @date 2022/1/21 17:16
 */
@Getter
@Setter
public class ExportExcelSetting<T> {
    /**
     * Get export excel.
     *
     * @param <T>       the type parameter
     * @param clazz     the clazz
     * @param data      the data
     * @param sheetName the sheet name
     * @return the export excel
     * @author ErebusST
     * @since 2022 -01-21 17:21:17
     */
    public static <T> ExportExcelSetting get(Class<T> clazz, List<T> data, String sheetName) {
        ExportExcelSetting setting = new ExportExcelSetting();
        setting.setClazz(clazz);
        setting.setData(data);
        setting.setSheetName(sheetName);
        return setting;
    }

    private Class<T> clazz;
    private List<T> data;
    String sheetName;
}
