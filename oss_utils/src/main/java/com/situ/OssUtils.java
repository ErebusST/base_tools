/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.*;
import com.situ.config.OssConfig;
import com.situ.tools.DateUtils;
import com.situ.tools.FileUtils;
import com.situ.tools.ObjectUtils;
import com.situ.tools.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 司徒彬
 * @date 2022/6/9 10:07
 */
@Component
@Slf4j
public class OssUtils {


    @Autowired
    OssConfig configTemp;

    private static OssConfig config;

    @PostConstruct
    public void init() {
        config = configTemp;
    }

    /**
     * Init .
     *
     * @author ErebusST
     * @since 2022 -06-09 10:11:50
     */
    public static OSS getClient() {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        return ossClient;
    }

    /**
     * Shutdown .
     *
     * @param client the client
     * @author ErebusST
     * @since 2022 -06-09 18:43:49
     */
    public static void shutdown(OSS client) {
        if (ObjectUtils.isNotNull(client)) {
            client.shutdown();
        }
    }

    /**
     * Upload string.
     *
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @param stream     the stream
     * @return the string
     * @author ErebusST
     * @since 2022 -06-09 17:56:51
     */
    public static String upload(String bucketName, String objectKey, InputStream stream) {
        OSS client = null;
        try {
            client = getClient();
            return upload(client, bucketName, objectKey, stream);
        } catch (Exception ex) {
            throw ex;
        } finally {
            shutdown(client);
        }
    }

    /**
     * Upload string.
     *
     * @param client     the client
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @param stream     the stream
     * @return the string
     * @author ErebusST
     * @since 2022 -06-09 17:58:19
     */
    public static String upload(OSS client, @Nonnull String bucketName, @Nonnull String objectKey, InputStream stream) {
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, stream);
            client.putObject(request);
            return objectKey;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Delete boolean.
     *
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -06-09 18:10:32
     */
    public static boolean delete(@Nonnull String bucketName, @Nonnull String objectKey) {
        OSS client = null;
        try {
            client = getClient();
            return delete(client, bucketName, objectKey);
        } catch (Exception ex) {
            throw ex;
        } finally {
            shutdown(client);
        }
    }

    /**
     * Delete boolean.
     *
     * @param client     the client
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -06-09 18:08:34
     */
    public static boolean delete(OSS client, @Nonnull String bucketName, @Nonnull String objectKey) {
        List<String> list = list(client, bucketName, objectKey);
        if (list.size() > 0) {
            for (String fileKey : list) {
                deleteSingle(client, bucketName, fileKey);
            }
        }
        deleteSingle(client, bucketName, objectKey);
        return true;
    }


    /**
     * Delete single boolean.
     *
     * @param client     the client
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -06-09 18:08:37
     */
    private static boolean deleteSingle(OSS client, @Nonnull String bucketName, @Nonnull String objectKey) {
        client.deleteObject(bucketName, objectKey);
        return true;
    }

    private static final Integer MAX_KEYS = 2;


    /**
     * List list.
     *
     * @param bucketName the bucket name
     * @param folder     the folder
     * @return the list
     * @author ErebusST
     * @since 2022 -06-09 17:52:50
     */
    public static List<String> list(@Nonnull String bucketName, @Nonnull String folder) {
        OSS client = null;
        try {
            client = getClient();
            return list(client, bucketName, folder);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(client)) {
                client.shutdown();
            }
        }
    }

    /**
     * List list.
     *
     * @param client     the client
     * @param bucketName the bucket name
     * @param folder     the folder
     * @return the list
     * @author ErebusST
     * @since 2022 -06-09 17:50:43
     */
    public static List<String> list(OSS client, @Nonnull String bucketName, @Nonnull String folder) {
        try {
            String prefix = folder.concat("/");
            String nextContinuationToken = null;
            ListObjectsV2Result result = null;
            List<String> files = new ArrayList<>();

            // 分页列举指定前缀的文件。
            do {
                ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName).withMaxKeys(MAX_KEYS);
                listObjectsV2Request.setPrefix(prefix);
                listObjectsV2Request.setContinuationToken(nextContinuationToken);
                result = client.listObjectsV2(listObjectsV2Request);

                List<OSSObjectSummary> summaries = result.getObjectSummaries();
                for (OSSObjectSummary summary : summaries) {
                    files.add(summary.getKey());
                }
                nextContinuationToken = result.getNextContinuationToken();
            } while (result.isTruncated());

            return files;
        } catch (Exception ex) {
            throw ex;
        }

    }


    /**
     * Get image url string.
     *
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the image url
     * @author ErebusST
     * @since 2022 -06-09 13:43:17
     */
    public static String getImageUrl(@Nonnull String bucketName, @Nonnull String objectKey) {
        OSS client = null;
        try {
            client = getClient();
            return getImageUrl(client, bucketName, objectKey);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(client)) {
                client.shutdown();
            }
        }

    }

    /**
     * Get image url string.
     *
     * @param client     the client
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the image url
     * @author ErebusST
     * @since 2022 -06-09 17:54:33
     */
    public static String getImageUrl(OSS client, @Nonnull String bucketName, @Nonnull String objectKey) {
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey, HttpMethod.GET);
            Date expiration = new Date(DateUtils.getNow().getTime() + 3600 * 1000);
            request.setExpiration(expiration);
            URL url = client.generatePresignedUrl(request);
            String string = url.toString();
            if (ObjectUtils.isNotEmpty(config.getUrl())) {
                string = string.substring(0, string.indexOf("?"));
                string = string.replace(bucketName + "." + config.getEndpoint(), config.getUrl());
            }

            if (StringUtils.equalsIgnoreCase("https", config.getProtocol())) {
                string = StringUtils.replace(string, "http:", "https:");
            }
            return string;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Exist boolean.
     *
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -06-19 20:15:06
     */
    public static Boolean exist(@Nonnull String bucketName, @Nonnull String objectKey) {
        OSS client = null;
        try {
            client = getClient();
            return exist(client, bucketName, objectKey);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(client)) {
                client.shutdown();
            }
        }
    }

    /**
     * Exist boolean.
     *
     * @param client     the client
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -06-19 20:15:08
     */
    public static Boolean exist(OSS client, @Nonnull String bucketName, @Nonnull String objectKey) {
        return client.doesObjectExist(bucketName, objectKey);
    }

    /**
     * Get object oss object.
     *
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the object
     * @author ErebusST
     * @since 2022 -06-19 20:19:31
     */
    public static OSSObject getObject(@Nonnull String bucketName, @Nonnull String objectKey) {
        OSS client = null;
        try {
            client = getClient();
            return getObject(client, bucketName, objectKey);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(client)) {
                client.shutdown();
            }
        }
    }

    /**
     * Get object oss object.
     *
     * @param client     the client
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the object
     * @author ErebusST
     * @since 2022 -06-19 20:19:32
     */
    public static OSSObject getObject(OSS client, @Nonnull String bucketName, @Nonnull String objectKey) {
        OSSObject ossObject = client.getObject(bucketName, objectKey);
        return ossObject;
    }

    /**
     * Download byte [ ].
     *
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the byte [ ]
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -06-19 20:26:28
     */
    public static byte[] getBytes(@Nonnull String bucketName, @Nonnull String objectKey) throws IOException {
        OSS client = null;
        try {
            client = getClient();
            return getBytes(client, bucketName, objectKey);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(client)) {
                client.shutdown();
            }
        }
    }

    /**
     * Download byte [ ].
     *
     * @param client     the client
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @return the byte [ ]
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -06-19 20:25:13
     */
    public static byte[] getBytes(OSS client, @Nonnull String bucketName, @Nonnull String objectKey) throws IOException {
        OSSObject object = null;
        InputStream inputStream = null;
        try {
            object = getObject(client, bucketName, objectKey);
            inputStream = object.getObjectContent();
            byte[] buffer = IOUtils.readStreamAsByteArray(inputStream);
            return buffer;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(inputStream)) {
                inputStream.close();
            }
            if (ObjectUtils.isNotNull(object)) {
                object.close();
            }
        }

    }

    /**
     * Download boolean.
     *
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @param fileName   the file name
     * @param response   the response
     * @return the boolean
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -06-27 11:50:46
     */
    public static boolean download( @Nonnull String bucketName, @Nonnull String objectKey,
                                    @Nonnull String fileName, HttpServletResponse response) throws IOException {
        OSS client = null;
        try {
            client = getClient();
            return download(client, bucketName, objectKey,fileName,response);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(client)) {
                client.shutdown();
            }
        }
    }

    /**
     * Download boolean.
     *
     * @param client     the client
     * @param bucketName the bucket name
     * @param objectKey  the object key
     * @param fileName   the file name
     * @param response   the response
     * @return the boolean
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -06-27 11:49:57
     */
    public static boolean download(OSS client, @Nonnull String bucketName, @Nonnull String objectKey,
                                   @Nonnull String fileName, HttpServletResponse response) throws IOException {
        byte[] bytes = getBytes(client, bucketName, objectKey);
        FileUtils.download(bucketName,fileName,response);
        return true;
    }

}
