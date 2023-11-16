/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 操作文件常用工具类
 *
 * @author 司徒彬
 * @date 2017年1月16日10 :42:16
 */
@Slf4j
public class FileUtils {


    /**
     * Is exist boolean.
     *
     * @param path the path
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static boolean isExist(String path) {
        try {
            File file = new File(path);
            return file.exists();
        } catch (Exception ex) {
            log.error("path:" + path, ex);
            throw ex;
        }
    }

    /**
     * 如果传入是文件夹，则直接创建文件夹 如果传入的文件，则创建该文件的父文件夹 <p> 文件夹必须以 / 或者 \\ 结尾
     *
     * @param filePath the path
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static void createDirectory(String filePath) throws IOException {
        Path file = Paths.get(filePath);
        Path parentRootPath = file;
        if (!isDirectory(filePath)) {
            parentRootPath = file.getParent();
        }
        if (!Files.exists(parentRootPath)) {
            Files.createDirectories(parentRootPath);
        }
    }


    /**
     * Gets parent dir path.
     *
     * @param file the file
     * @return the parent dir path
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static String getParentDirPath(String file) {
        Path path = Paths.get(file);
        String dirPath = path.getParent().toString();
        dirPath = formatDirectoryPath(dirPath);
        return dirPath;
    }

    /**
     * Create file.
     *
     * @param filePath the file path
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static void createFile(String filePath) throws IOException {
        try {
            createDirectory(filePath);
            Path file = Paths.get(filePath);
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Is directory boolean.
     *
     * @param filePath the file path
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static boolean isDirectory(@Nonnull String filePath) {
        try {
            return Files.isDirectory(Paths.get(filePath)) || filePath.endsWith(StaticValue.FILE_SEPARATOR);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 获得文件或文件夹的占用空间的大小，单位字节
     *
     * @param dirPath the dir path
     * @return the long
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static long countFileSizeInDirectory(String dirPath) throws Exception {
        long size;
        File directory = new File(dirPath);
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            throw new Exception("路径 [" + dirPath + "] 不存在!");
        }

        if (isDirectory(dirPath)) {
            File[] files = directory.listFiles();
            size = Arrays.stream(files).filter(File::isFile).mapToLong(File::length).sum();
            List<String> errorList = new ArrayList<>();
            Stream<String> dirList = Arrays.stream(files).filter(File::isDirectory).map(File::getAbsolutePath);
            size += dirList.mapToLong(dir ->
            {
                try {
                    return countFileSizeInDirectory(dir);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorList.add(e.getMessage());
                    return 0;
                }
            }).sum();
            if (errorList.size() > 0) {
                throw new Exception(StringUtils.getCombineString(StaticValue.LINE_SEPARATOR, errorList));
            }
        } else {
            size = directory.length();
        }
        return size;
    }


    /**
     * 递归求取目录文件个数
     *
     * @param dirPath the dir path
     * @return the file count in dir
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static long countFileNumberInDirectory(String dirPath) throws Exception {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            throw new Exception("路径 [" + dirPath + "] 不存在!");
        }
        long count;
        if (isDirectory(dirPath)) {
            File[] files = directory.listFiles();
            count = Arrays.stream(files).filter(File::isFile).count();
            List<String> errorList = new ArrayList<>();
            count += Arrays.stream(files).filter(File::isDirectory).map(File::getAbsolutePath).mapToLong(file ->
            {
                try {
                    return countFileNumberInDirectory(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorList.add(e.getMessage());
                    return 0;
                }
            }).sum();
            if (errorList.size() > 0) {
                throw new Exception(StringUtils.getCombineString(StaticValue.LINE_SEPARATOR, errorList));
            }
        } else {
            count = 1;
        }
        return count;
    }

    /**
     * 列出某文件夹下所有文件列表，不包含传入的pathStr
     *
     * @param pathStr the path str
     * @return the list
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static List<Path> listFiles(String pathStr) throws IOException {
        try {
            List<Path> fileList = listFilesContainSelf(pathStr);
            fileList.remove(Paths.get(pathStr));
            return fileList;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 获得某文件夹下所有文件列表，包含传去的pathStr
     *
     * @param pathStr the path str
     * @return the list
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static List<Path> listFilesContainSelf(String pathStr) throws IOException {
        try {
            List<Path> fileList = new ArrayList<>();

            Path path = Paths.get(pathStr);
            if (isDirectory(path.toString())) {
                List<Path> dirInDirPath = Files.list(path).collect(Collectors.toList());
                fileList.add(path);
                for (Path pathInDir : dirInDirPath) {
                    fileList.addAll(listFilesContainSelf(pathInDir.toString()));

                }
            } else {
                fileList.add(path);
            }


            return fileList;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 格式化文件大小
     *
     * @param size the size
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static String formatSize(long size) {
        // 转换文件大小
        DecimalFormat df = new DecimalFormat("0.00");
        if (size < 1024) {
            return df.format((double) size) + "B";
        } else if (size < 1048576) {
            return df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            return df.format((double) size / 1048576) + "MB";
        } else {
            return df.format((double) size / 1073741824) + "GB";
        }
    }

    /**
     * 复制文件到指定目录 <p> 需指定文件名
     *
     * @param sourceFilePath the source file path 源路径
     * @param targetFilePath the target file path 目标路径
     * @param fileName       the file name 文件名
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static boolean copyFile(String sourceFilePath, String targetFilePath, String fileName) throws Exception {
        try {
            return copyFile(sourceFilePath, targetFilePath + StaticValue.FILE_SEPARATOR + fileName);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 复制文件或文件夹到指定目录 <p> 如果复制文件，则 sourceFilePath和targetFilePath都是具体的文件路径 <p> 如果复制文件夹，则 sourceFilePath和targetFilePath都是具体的文件夹路径
     *
     * @param sourceFilePath the source file path
     * @param targetFilePath the target file path
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static boolean copyFile(String sourceFilePath, String targetFilePath) throws Exception {
        try {
            if (targetFilePath.trim().equals(sourceFilePath)) {
                return true;
            }
            Path sourceFile = Paths.get(sourceFilePath);
            Path targetFile = Paths.get(targetFilePath);
            if (isDirectory(sourceFilePath)) {
                List<String> errorList = new ArrayList<>();
                if (!isDirectory(targetFilePath)) {
                    throw new Exception("源路径 [" + sourceFilePath + "] 是一个文件夹，目标路径 [" + targetFilePath + "] 必须也是文件夹!");
                }
                File[] files = sourceFile.toFile().listFiles();
                Arrays.stream(files).map(File::getAbsolutePath).forEach(sourcePath ->
                {
                    Path tempTargetFile = Paths.get(targetFilePath, sourcePath.replace(sourceFilePath, ""));
                    try {
                        createDirectory(tempTargetFile.toString() + StaticValue.FILE_SEPARATOR);
                        copyFile(sourcePath, tempTargetFile.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorList.add(e.getMessage());
                    }
                });
                if (errorList.size() > 0) {
                    throw new Exception(StringUtils.getCombineString(StaticValue.LINE_SEPARATOR, errorList));
                }
            } else {
                createDirectory(targetFilePath);
                Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }

            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Clear directory boolean.
     * <p>
     * 如果文件夹不存在则创建一个文件夹
     *
     * @param filePath the file path
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static boolean clearDirectory(String filePath) throws Exception {
        try {
            createDirectory(filePath);
            filePath = formatDirectoryPath(filePath);
            deleteFile(filePath);
            createDirectory(filePath);
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Delete file boolean.
     *
     * @param filePaths the file paths
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static boolean deleteFile(String... filePaths) {
        try {

            for (String filePath : filePaths) {
                if (StringUtils.isEmpty(filePath)) {
                    continue;
                }
                Path file = Paths.get(filePath);

                if (isDirectory(filePath)) {
                    File[] files = file.toFile().listFiles();
                    if (ObjectUtils.isNotNull(files)) {
                        for (File tempPath : files) {
                            deleteFile(tempPath.getAbsolutePath());
                        }
                    }


                    file.toFile().delete();
                } else {
                    file.toFile().delete();

                }
            }

            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * 获得文件扩展名 <p> 2016年10月25日23:37:57
     *
     * @param fileName the filename
     * @return the extension name 返回格式 ex: .exe .jpg .png
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static String getFileType(String fileName) {
        if (fileName.contains(".")) {
            return StringUtils.substring(fileName, fileName.lastIndexOf("."));
        } else {
            return "";
        }

    }


    /**
     * 获得不带扩展的文件名
     * <p>
     * 2016年10月25日23:38:19
     *
     * @param fileName the fileName
     * @return the file name no ex
     * @author ErebusST
     * @since 2022 -01-07 15:36:00
     */
    public static String getFileName(String fileName) {
        int startIndex = formatFilePath(fileName).lastIndexOf(StaticValue.FILE_SEPARATOR);
        startIndex = startIndex == -1 ? 0 : startIndex + 1;
        int lastIndex = fileName.lastIndexOf(".");
        lastIndex = lastIndex == -1 ? fileName.length() : lastIndex;
        return StringUtils.substring(fileName, startIndex, lastIndex);
    }

    /**
     * Format file path string.
     *
     * @param path the path
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static String formatFilePath(String path) {
        return StringUtils.replace(path, "\\", StaticValue.FILE_SEPARATOR);
    }

    /**
     * Format directory path string.
     *
     * @param path the path
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static String formatDirectoryPath(String path) {
        if (path.length() == 0) {
            return "";
        }
        path = formatFilePath(path);
        return path.endsWith(StaticValue.FILE_SEPARATOR) ? path : path + StaticValue.FILE_SEPARATOR;
    }


    /**
     * Download file by url boolean.
     *
     * @param url      the url
     * @param savePath the save path
     * @param saveFile the save file
     * @return the boolean
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static boolean downloadFileByUrl(String url, String savePath, String saveFile) throws IOException {

        try {
            savePath = formatDirectoryPath(savePath);
            return downloadFileByUrl(url, savePath + saveFile);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Download file by url boolean.
     *
     * @param urlStr       the url str
     * @param saveFilePath the save file path
     * @return the boolean
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static boolean downloadFileByUrl(String urlStr, String saveFilePath) throws IOException {
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            //urlStr = StringUtils.substring(urlStr, 0, urlStr.indexOf("?"));
            saveFilePath = formatFilePath(saveFilePath);
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            inputStream = conn.getInputStream();
            createFile(saveFilePath);

            outputStream = new FileOutputStream(saveFilePath);
            //缓存数组
            byte[] buffer = new byte[StaticValue.BUFFER_SIZE];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            return true;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Write content to txt boolean.
     *
     * @param pathStr the path str
     * @param content the content
     * @return the boolean
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static boolean writeContentToTxt(String pathStr, String content) throws IOException {
        try {
            Path path = Paths.get(pathStr);
            createFile(pathStr);
            List<CharSequence> lineList = new ArrayList<>();
            lineList.add(content);
            Files.write(path, lineList, Charset.forName(StaticValue.ENCODING));
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Move file boolean.
     *
     * @param source the source
     * @param target the target
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static boolean moveFile(String source, String target) throws Exception {
        copyFile(source, target);
        deleteFile(source);
        return true;
    }

    /**
     * Gets file item from request.
     *
     * @param request the request
     * @return the file item from request
     * @throws FileUploadException the file upload exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static FileItem getFileItemFromRequest(HttpServletRequest request) throws FileUploadException {
        try {

            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            Optional<FileItem> fileItemOptional =
                    upload.parseParameterMap(request).entrySet().stream().map(Map.Entry::getValue)
                            .flatMap(List::stream)
                            .filter(fileItem -> !fileItem.isFormField())
                            .findFirst();
            return fileItemOptional.equals(Optional.empty()) ? null : fileItemOptional.get();

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * File upload string.
     *
     * @param request  the request
     * @param filePath the file path
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static String fileUpload(HttpServletRequest request, String filePath) throws Exception {
        String fileNameRet = "";
        String maxSize = request.getParameter("maxSize");
        String fileType = request.getParameter("fileType");
        FileUtils.createDirectory(filePath);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //最大缓存
        factory.setSizeThreshold(5 * 1024);
        //设置文件目录
        factory.setRepository(new File(filePath));
        ServletFileUpload upload = new ServletFileUpload(factory);

        if (StringUtils.isNotEmpty(maxSize)) {
            //文件最大上限
            upload.setSizeMax(Integer.valueOf(maxSize) * 1024 * 1024);
        } else {
            //文件最大上限
            upload.setSizeMax(10 * 1024 * 1024);
        }
        //获取所有文件列表

        List<FileItem> items = upload.parseParameterMap(request).entrySet().stream().map(Map.Entry::getValue)
                .flatMap(List::stream)
                .filter(fileItem -> !fileItem.isFormField()).collect(Collectors.toList());
        for (FileItem item : items) {
            if (!item.isFormField()) {
                //文件名
                String fileName = item.getName();
                //检查文件后缀格式
                String fileEnd = getFileType(fileName);
                if (StringUtils.isNotEmpty(fileType)) {
                    List<String> arrType = StringUtils.splitToList(fileType, ",");
                    boolean typeChecked = arrType.stream().filter(type -> ("." + type).equalsIgnoreCase(fileEnd)).count() > 0;

                    if (!typeChecked) {
                        throw new Exception("文件格式不正确");
                    }
                }
                //创建文件唯一名称
                String uuid = DataSwitch.getUUID();
                //真实上传路径
                StringBuffer sbRealPath = new StringBuffer();
                sbRealPath.append(filePath).append(uuid).append(fileEnd);
                //写入文件
                File file = new File(sbRealPath.toString());
                item.write(file);
                //上传成功，更新数据库数据
                fileNameRet = uuid + fileEnd;
            }
        }
        return fileNameRet;
    }


    /**
     * File upload string.
     *
     * @param fileItem the file item
     * @param filePath the file path
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static String fileUpload(FileItem fileItem, String filePath) throws Exception {
        String fileNameRet = "";
        FileUtils.createDirectory(filePath);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //最大缓存
        factory.setSizeThreshold(5 * 1024);
        //设置文件目录
        factory.setRepository(new File(filePath));
        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setSizeMax(10 * 1024 * 1024);
        //获取所有文件列表

        List<FileItem> items = ListUtils.newArrayList(fileItem);
        for (FileItem item : items) {
            if (!item.isFormField()) {
                //文件名
                String fileName = item.getName();
                //检查文件后缀格式
                String fileEnd = getFileType(fileName);

                //创建文件唯一名称
                String uuid = DataSwitch.getUUID();
                //真实上传路径
                StringBuffer sbRealPath = new StringBuffer();
                sbRealPath.append(filePath).append(uuid).append(fileEnd);
                //写入文件
                File file = new File(sbRealPath.toString());
                item.write(file);
                //上传成功，更新数据库数据
                fileNameRet = uuid + fileEnd;
            }
        }
        return fileNameRet;
    }


    /**
     * Download.
     *
     * @param reportPath the report path
     * @param realName   the real name
     * @param response   the response
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static void download(String reportPath, String realName, HttpServletResponse response) throws IOException {
        Path path = Paths.get(reportPath);
        try {
            InputStream inputStream = Files.newInputStream(path);
            download(inputStream, realName, response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Download .
     *
     * @param inputStream the input stream
     * @param fileName    the file name
     * @param response    the response
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -07-04 17:45:53
     */
    public static void download(InputStream inputStream, String fileName, HttpServletResponse response) throws IOException {
        try {
            // 清空response
            String realName = URLEncoder.encode(fileName, StaticValue.ENCODING);
            //response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + realName);
            response.setCharacterEncoding("utf8");
            response.setContentType("application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();

            byte[] buffer = new byte[2048];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(inputStream)) {
                inputStream.close();
            }
        }
    }


    /**
     * Download .
     *
     * @param inputStream the input stream
     * @param savePath    the save path
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -09-14 11:08:41
     */
    public static void download(InputStream inputStream, String savePath) throws IOException {
        try {
            FileUtils.deleteFile(savePath);
            File file = new File(savePath);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[2048];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(inputStream)) {
                inputStream.close();
            }
        }
    }

    /**
     * Get web file content string.
     *
     * @param urlPath the url path
     * @return the web file content
     * @author ErebusST
     * @since 2022 -08-24 18:08:19
     */
    public static String getWebFileContent(String urlPath) {
        String result = "";
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlPath);
            inputStream = url.openStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            result = bufferedReader.lines().collect(Collectors.joining(StaticValue.LINE_SEPARATOR));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ObjectUtils.isNotNull(bufferedReader)) {
                    bufferedReader.close();
                }
                if (ObjectUtils.isNotNull(inputStream)) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Get web file bytes byte [ ].
     *
     * @param urlPath the url path
     * @return the byte [ ]
     * @author ErebusST
     * @since 2022 -11-09 12:23:02
     */
    public static byte[] getWebFileBytes(String urlPath) {
        InputStream inputStream = null;
        byte[] bytes = null;
        try {
            URL url = new URL(urlPath);
            inputStream = url.openStream();
            bytes = IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ObjectUtils.isNotNull(inputStream)) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }


    @Test
    public void test() {
        String webFileContent = getWebFileContent("https://storage.data-dance.com/js/init.js");
        log.info(webFileContent);
    }

    public void test1() {
        String webFileContent = getWebFileContent("https://imagepub.swguancha.com/mall/2022-03-18/d5677b8bc0864f3f9dba02de00195e8a.jpg");
    }


}
