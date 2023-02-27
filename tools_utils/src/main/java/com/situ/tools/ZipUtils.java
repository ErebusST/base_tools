/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.situ.tools.StaticValue.BUFFER_SIZE;
import static com.situ.tools.StaticValue.ENCODING;

/**
 * @author 司徒彬
 * @date 2023/2/27 09:39
 */
public class ZipUtils {

    @Test
    public void test() {
        try {
            decompress("/mnt/poi_data/酒泉市.zip", "/Users/situ/Works/mnt/a/",Charset.forName("GBK"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final byte[] buf = new byte[1024];

    public static void decompress(String file, String toPath, Charset charset) throws Exception {
        Path path = Paths.get(file);
        if (!Files.exists(path)) {
            return;
        }
        FileUtils.createDirectory(toPath);
        if (toPath.endsWith("/")) {
            toPath = StringUtils.substring(toPath, 0, toPath.length() - 1);
        }
        toPath = toPath.concat("/").concat(FileUtils.getFileName(file));
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(path.toFile(), charset);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = toPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(toPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();

                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[BUFFER_SIZE];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void compress(String directory, String outputPath) throws IOException {

        //ZipOutputStream zos = null;
        //File sourceFile = new File(directory);
        //if(!sourceFile.exists()){
        //    return;
        //}
        //if (sourceFile.isFile()) {
        //    // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
        //    zos.putNextEntry(new ZipEntry(name));
        //    // copy文件到zip输出流中
        //    int len;
        //    FileInputStream in = new FileInputStream(sourceFile);
        //    while ((len = in.read(buf)) != -1) {
        //        zos.write(buf, 0, len);
        //    }
        //    // Complete the entry
        //    zos.closeEntry();
        //    in.close();
        //} else {
        //    File[] listFiles = sourceFile.listFiles();
        //    if (listFiles == null || listFiles.length == 0) {
        //        // 需要保留原来的文件结构时,需要对空文件夹进行处理
        //        if (KeepDirStructure) {
        //            // 空文件夹的处理
        //            zos.putNextEntry(new ZipEntry(name + "/"));
        //            // 没有文件，不需要文件的copy
        //            zos.closeEntry();
        //        }
        //    } else {
        //        for (File file : listFiles) {
        //            // 判断是否需要保留原来的文件结构
        //            if (KeepDirStructure) {
        //                // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
        //                // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
        //                compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
        //            } else {
        //                compress(file, zos, file.getName(), KeepDirStructure);
        //            }
        //
        //        }
        //    }
        //}

    }
}
