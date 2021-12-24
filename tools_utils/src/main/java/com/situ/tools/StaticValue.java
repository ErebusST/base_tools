package com.situ.tools;

/**
 * 系统中使用的静态常量
 *
 * @author 司徒彬
 * @date 2018/8/25 19:04
 */
public class StaticValue {
    public final static String ALL = "0";
    public static final String TAB = "\t";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    public final static String ENCODING = "UTF-8";// System.getProperty("file.encoding");
    public static final String FILE_SEPARATOR = "/";// System.getProperty("file.separator", "/");
    public static final int BUFFER_SIZE = 5 * 1024;

}
