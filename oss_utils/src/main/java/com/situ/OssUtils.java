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
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.*;
import com.situ.config.OssConfig;
import com.situ.tools.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The type Oss utils.
 *
 * @author 司徒彬
 * @date 2022 /6/9 10:07
 */
@Component
@Slf4j
public class OssUtils {


    /**
     * The Config temp.
     */
    @Autowired
    OssConfig configTemp;

    private static OssConfig config;

    /**
     * Init .
     *
     * @author ErebusST
     * @since 2023 -04-03 11:14:35
     */
    @PostConstruct
    public void init() {
        config = configTemp;
    }

    /**
     * Init .
     *
     * @return the client
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

    private static final Integer MAX_KEYS = 100;


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
    @Deprecated
    public static List<String> list(OSS client, @Nonnull String bucketName, @Nonnull String folder) {
        try {
            String prefix;
            if (StringUtils.endsWithIgnoreCase(folder, "/")) {
                prefix = folder;
            } else {
                prefix = folder.concat("/");
            }
            String nextContinuationToken = null;
            ListObjectsV2Result result = null;
            List<String> files = new ArrayList<>();

            // 分页列举指定前缀的文件。
            do {
                ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName).withMaxKeys(MAX_KEYS);
                listObjectsV2Request.setPrefix(prefix);
                listObjectsV2Request.setContinuationToken(nextContinuationToken);
                result = client.listObjectsV2(listObjectsV2Request);

                result.getObjectSummaries().stream().forEach(summary -> {
                    files.add(summary.getKey());
                });
                nextContinuationToken = result.getNextContinuationToken();
            } while (result.isTruncated());
            return files;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * List list.
     *
     * @param bucketName the bucket name
     * @param prefix     the prefix
     * @param start      the start
     * @param size       the size
     * @param ergodic    the ergodic 是否遍历，如果为 true 则直接返回该目录下所有的文件，如果是 false 则只返回该目录下一级的目录
     * @return the list
     * @author ErebusST
     * @since 2023 -06-18 16:01:00
     */
    public static List<String> list(@Nonnull String bucketName, @Nonnull String prefix,
                                    String start, @Nonnull Integer size, @Nonnull boolean ergodic) {
        OSS client = null;
        try {
            client = getClient();
            return list(client, bucketName, prefix, start, size, ergodic);
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
     * @param prefix     the prefix
     * @param start      the start
     * @param size       the size
     * @param ergodic    the ergodic
     * @return the list
     * @author ErebusST
     * @since 2023 -06-19 18:08:41
     */
    public static List<String> list(OSS client, @Nonnull String bucketName, @Nonnull String prefix,
                                    String start, @Nonnull Integer size, @Nonnull boolean ergodic) {
        try {
            if (StringUtils.endsWithIgnoreCase(prefix, "/")) {
                prefix = prefix;
            } else {
                prefix = prefix.concat("/");
            }
            ListObjectsRequest request = new ListObjectsRequest(bucketName)
                    .withPrefix(prefix)
                    .withMarker(start)
                    .withDelimiter("/")
                    .withMaxKeys(size + 1);

            ObjectListing result = client.listObjects(request);

            return result.getCommonPrefixes();

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
                string = StringUtils.replace(string, "https://", "");
                string = StringUtils.replace(string, "http://", "");
                string = string.replace(bucketName + "." + config.getEndpoint(), config.getUrl());
            }

            if (StringUtils.equalsIgnoreCase("https", config.getProtocol())) {
                string = StringUtils.replace(string, "http:", "https:");
            }
            return string.concat("?t=" + System.currentTimeMillis());
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
        Boolean exist = exist(client, bucketName, objectKey);
        if (exist) {
            OSSObject ossObject = client.getObject(bucketName, objectKey);
            return ossObject;
        } else {
            return null;
        }
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
    public static boolean download(@Nonnull String bucketName, @Nonnull String objectKey,
                                   @Nonnull String fileName, HttpServletResponse response) throws IOException {
        OSS client = null;
        try {
            client = getClient();
            return download(client, bucketName, objectKey, fileName, response);
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
        try {
            OSSObject object = getObject(client, bucketName, objectKey);
            InputStream inputStream = object.getObjectContent();
            FileUtils.download(inputStream, fileName, response);
        } catch (Exception ex) {
            throw ex;
        }
        return true;
    }

    /**
     * Copy boolean.
     *
     * @param bucket  the bucket
     * @param fromKey the from key
     * @param toKey   the to key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -11-09 10:40:29
     */
    public static boolean copy(@Nonnull String bucket, @Nonnull String fromKey, @Nonnull String toKey) {
        return copy(bucket, fromKey, bucket, toKey);
    }

    /**
     * Copy boolean.
     *
     * @param client  the client
     * @param bucket  the bucket
     * @param fromKey the from key
     * @param toKey   the to key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -11-09 10:40:32
     */
    public static boolean copy(OSS client, @Nonnull String bucket, @Nonnull String fromKey, @Nonnull String toKey) {
        return copy(client, bucket, fromKey, bucket, toKey);
    }

    /**
     * Copy boolean.
     *
     * @param fromBucket the from bucket
     * @param fromKey    the from key
     * @param toBucket   the to bucket
     * @param toKey      the to key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -11-09 10:38:02
     */
    public static boolean copy(@Nonnull String fromBucket, @Nonnull String fromKey,
                               @Nonnull String toBucket, @Nonnull String toKey) {
        OSS client = null;
        try {
            client = getClient();
            return copy(client, fromBucket, fromKey, toBucket, toKey);
        } catch (Exception ex) {
            throw ex;
        } finally {
            shutdown(client);
        }
    }

    /**
     * Copy boolean.
     *
     * @param client     the client
     * @param fromBucket the from bucket
     * @param fromKey    the from key
     * @param toBucket   the to bucket
     * @param toKey      the to key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -11-09 10:38:16
     */
    public static boolean copy(OSS client, @Nonnull String fromBucket, @Nonnull String fromKey,
                               @Nonnull String toBucket, @Nonnull String toKey) {
        try {
            CopyObjectRequest request = new CopyObjectRequest(fromBucket, fromKey, toBucket, toKey);
            CopyObjectResult result = client.copyObject(request);
        } catch (Exception ex) {
            throw ex;
        }
        return true;
    }

    /**
     * Move boolean.
     *
     * @param bucket  the bucket
     * @param fromKey the from key
     * @param toKey   the to key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -11-09 10:48:16
     */
    public static boolean move(@Nonnull String bucket, @Nonnull String fromKey,
                               @Nonnull String toKey) {
        return move(bucket, fromKey, bucket, toKey);
    }


    /**
     * Move boolean.
     *
     * @param client  the client
     * @param bucket  the bucket
     * @param fromKey the from key
     * @param toKey   the to key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -11-09 10:45:44
     */
    public static boolean move(OSS client, @Nonnull String bucket, @Nonnull String fromKey,
                               @Nonnull String toKey) {
        return move(client, bucket, fromKey, bucket, toKey);
    }

    /**
     * Move boolean.
     *
     * @param fromBucket the from bucket
     * @param fromKey    the from key
     * @param toBucket   the to bucket
     * @param toKey      the to key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -11-09 10:44:23
     */
    public static boolean move(@Nonnull String fromBucket, @Nonnull String fromKey,
                               @Nonnull String toBucket, @Nonnull String toKey) {
        OSS client = null;
        try {
            client = getClient();
            move(client, fromBucket, fromKey, toBucket, toKey);
        } catch (Exception ex) {
            throw ex;
        } finally {
            shutdown(client);
        }
        return true;
    }

    /**
     * Move boolean.
     *
     * @param client     the client
     * @param fromBucket the from bucket
     * @param fromKey    the from key
     * @param toBucket   the to bucket
     * @param toKey      the to key
     * @return the boolean
     * @author ErebusST
     * @since 2022 -11-09 10:44:28
     */
    public static boolean move(OSS client, @Nonnull String fromBucket, @Nonnull String fromKey,
                               @Nonnull String toBucket, @Nonnull String toKey) {
        copy(client, fromBucket, fromKey, toBucket, toKey);
        delete(client, fromBucket, fromKey);
        return true;
    }


    /**
     * Save web file to oss boolean.
     *
     * @param webUrl the web url
     * @param bucket the bucket
     * @param key    the key
     * @return the boolean
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -04-03 11:14:37
     */
    public static boolean saveWebFileToOss(@Nonnull String webUrl, @Nonnull String bucket, @Nonnull String key) throws IOException {
        OSS client = null;
        try {
            client = getClient();
            return saveWebFileToOss(client, webUrl, bucket, key);
        } catch (Exception ex) {
            throw ex;
        } finally {
            shutdown(client);
        }
    }

    /**
     * Save web file to oss boolean.
     *
     * @param client the client
     * @param webUrl the web url
     * @param bucket the bucket
     * @param key    the key
     * @return the boolean
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -11-09 12:28:15
     */
    public static boolean saveWebFileToOss(OSS client, @Nonnull String webUrl, @Nonnull String bucket, @Nonnull String key) throws IOException {
        InputStream inputStream = null;
        try {
            URL url = new URL(webUrl);
            inputStream = url.openStream();
            upload(client, bucket, key, inputStream);
        } catch (Exception ex) {
            throw ex;
        } finally {
            try {
                if (ObjectUtils.isNotNull(inputStream)) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Write boolean.
     *
     * @param content the content
     * @param bucket  the bucket
     * @param key     the key
     * @return the boolean
     * @author ErebusST
     * @since 2023 -04-03 11:14:37
     */
    public static boolean write(@Nonnull String content, @Nonnull String bucket, @Nonnull String key) {
        OSS client = null;
        try {
            client = getClient();
            return write(client, content, bucket, key);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(client)) {
                shutdown(client);
            }
        }
    }

    private static final List<String> BLANK_CONTENT = new ArrayList<>(3);

    static {
        BLANK_CONTENT.add("\r");
        BLANK_CONTENT.add("\t");
        BLANK_CONTENT.add("\n");
    }

    /**
     * Write boolean.
     *
     * @param client  the client
     * @param content the content
     * @param bucket  the bucket
     * @param key     the key
     * @return the boolean
     * @author ErebusST
     * @since 2023 -04-03 11:14:37
     */
    public static boolean write(OSS client, @Nonnull String content, @Nonnull String bucket, @Nonnull String key) {
        Boolean exist = exist(client, bucket, key);
        if (exist) {
            delete(client, bucket, key);
        }
        content = StringUtils.replace(content, BLANK_CONTENT, "");
        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        PutObjectRequest request = new PutObjectRequest(bucket, key, stream);
        PutObjectResult result = client.putObject(request);
        return true;
    }

    /**
     * Read string.
     *
     * @param bucket the bucket
     * @param key    the key
     * @return the string
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -04-03 11:17:28
     */
    public static String read(@Nonnull String bucket, @Nonnull String key) throws IOException {
        OSS client = null;
        try {
            client = getClient();
            return read(client, bucket, key);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (ObjectUtils.isNotNull(client)) {
                shutdown(client);
            }
        }
    }

    /**
     * Read string.
     *
     * @param client the client
     * @param bucket the bucket
     * @param key    the key
     * @return the string
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2023 -04-03 11:16:52
     */
    public static String read(OSS client, @Nonnull String bucket, @Nonnull String key) throws IOException {
        if (!exist(client, bucket, key)) {
            return "";
        }
        byte[] bytes = getBytes(client, bucket, key);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
