/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.situ.entity.bo.HttpHeader;
import com.situ.enumeration.ContentType;
import com.situ.enumeration.HttpMethod;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Request tools.
 *
 * @author 司徒彬
 * @date 2020 /6/26 18:25
 */
@Slf4j
public class RequestTools {

    /**
     * 单位:秒
     */
    private final static int TIME_OUT = 1000;

    /**
     * Get string.
     *
     * @param url     the url
     * @param headers the headers
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:07
     */
    public static Response get(@NonNull String url, HttpHeader... headers) throws Exception {
        return get(url, null, headers);
    }

    /**
     * Get string.
     *
     * @param url       the url
     * @param parameter the parameter
     * @param headers   the headers
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:07
     */
    public static Response get(@NonNull String url, Map<String, String> parameter, HttpHeader... headers) throws Exception {
        if (ObjectUtils.isNotNull(parameter) && parameter.size() > 0) {
            String para = parameter.entrySet()
                    .stream()
                    .map(entry -> {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        return StringUtils.concat(key, "=", value);
                    })
                    .collect(Collectors.joining("&"));
            if (StringUtils.isNotEmpty(para)) {
                para = URLEncoder.encode(url, "UTF-8");
                url = StringUtils.concat(url, "?", para);
            }
            //url = URLEncoder.encode(url, "UTF-8");
        }
        return call(HttpMethod.Get, null, url, null, headers);
    }

    /**
     * Post string.
     *
     * @param url     the url
     * @param headers the headers
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Response post(@NonNull String url, HttpHeader... headers) throws Exception {
        return post(url, getUrlParameter(url), headers);
    }

    /**
     * Post string.
     *
     * @param url       the url
     * @param parameter the parameter
     * @param headers   the headers
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Response post(@NonNull String url, JsonElement parameter, HttpHeader... headers) throws Exception {
        return call(HttpMethod.Post, ContentType.application_json, url, parameter, headers);
    }

    /**
     * Post form string.
     *
     * @param url     the url
     * @param form    the form
     * @param headers the headers
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Response postForm(@NonNull String url, Map<String, Object> form, HttpHeader... headers) throws Exception {
        return call(HttpMethod.Post, ContentType.text_plain, url, form, headers);
    }

    /**
     * Put string.
     *
     * @param url     the url
     * @param headers the headers
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Response put(@NonNull String url, HttpHeader... headers) throws Exception {
        return put(url, getUrlParameter(url), headers);
    }

    /**
     * Put string.
     *
     * @param url       the url
     * @param parameter the parameter
     * @param headers   the headers
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Response put(@NonNull String url, JsonElement parameter, HttpHeader... headers) throws Exception {
        return call(HttpMethod.Put, ContentType.application_json, url, parameter, headers);
    }

    /**
     * Delete string.
     *
     * @param url     the url
     * @param headers the headers
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Response delete(@NonNull String url, HttpHeader... headers) throws Exception {

        return delete(url, getUrlParameter(url), headers);
    }

    private static JsonObject getUrlParameter(String url) {
        JsonObject jsonObject = new JsonObject();
        if (url.contains("?")) {
            List<String> temp = StringUtils.splitToList(url, "?");
            temp = StringUtils.splitToList(temp.get(1), "&");
            temp.stream().forEach(str -> {
                List<String> array = StringUtils.splitToList(str, "=");
                if (array.size() == 2 && StringUtils.isNotEmpty(array.get(0)) && StringUtils.isNotEmpty(array.get(1))) {
                    jsonObject.addProperty(array.get(0), array.get(1));
                }
            });
        }
        return jsonObject;
    }

    /**
     * Delete string.
     *
     * @param url       the url
     * @param parameter the parameter
     * @param headers   the headers
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Response delete(@NonNull String url, JsonElement parameter, HttpHeader... headers) throws Exception {
        return call(HttpMethod.Delete, ContentType.application_json, url, parameter, headers);
    }


    private static Response call(HttpMethod method, ContentType contentType, @NonNull String url, Object parameters, HttpHeader... headers) throws Exception {
        if (parameters == null) {
            parameters = new Object();
        }
        Class<?> parametersClass = parameters.getClass();
        if (contentType == null && (parametersClass.equals(JsonElement.class)
                || (ObjectUtils.isNotNull(parametersClass.getSuperclass())
                && parametersClass.getSuperclass().equals(JsonElement.class)))) {
            contentType = ContentType.application_json;
        } else if (contentType == null) {
            contentType = ContentType.text_plain;
        }
        Request.Builder builder = new Request.Builder();
        for (HttpHeader header : headers) {
            builder.addHeader(header.getKey(), header.getValue());
        }
        builder = builder.url(url);
        if (method.equals(HttpMethod.Get)) {
            builder = builder.get();
        } else {
            RequestBody requestBody = createRequestBody(contentType, parameters);
            builder.method(method.getMethod(), requestBody);
        }

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();
        Response response = client.newCall(request).execute();
        return  response;
//        if (ObjectUtils.isNotNull(response.body())) {
//            return response.body().string();
//        } else {
//            JsonObject result = new JsonObject();
//            result.addProperty("status", response.code());
//            result.addProperty("message", response.message());
//            return result.toString();
//        }
    }


    private static RequestBody createRequestBody(ContentType contentType, Object parameters) throws Exception {
        Class<?> parametersClass = parameters.getClass();
        RequestBody body;
        if (ObjectUtils.equals(contentType, ContentType.application_json)) {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            body = RequestBody.create(mediaType,
                    DataSwitch.convertObjectToJsonElement(parameters, false).toString());

        } else if (ObjectUtils.equals(contentType, ContentType.text_plain)) {

            if (!parametersClass.equals(Map.class)) {
                throw new Exception(contentType + "表单提交时，参数应为Map<String,String>。现在为:" + parametersClass.getName());
            }
            MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            ((Map<String, Object>) parameters).entrySet().stream()
                    .filter(entry -> StringUtils.isNotEmpty(entry.getValue()))
                    .forEach(entry -> {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        Class<?> clazz = value.getClass();
                        if (clazz.equals(File.class)) {
                            String fileName = FileUtils.getFileName(value.toString());
                            builder.addFormDataPart(key, fileName,
                                    RequestBody.create(MediaType.parse("application/octet-stream"), new File(value.toString())));
                        } else {
                            builder.addFormDataPart(key, value.toString());
                        }
                    });
            body = builder.build();
        } else if (ObjectUtils.equals(contentType, ContentType.application_x_www_form_urlencoded)) {
            if (!parametersClass.equals(Map.class)) {
                throw new Exception(contentType + "表单提交时，参数应为Map<String,String>。现在为:" + parametersClass.getName());
            }
            MediaType mediaType = MediaType.parse(ContentType.application_x_www_form_urlencoded.getValue().concat("; charset=utf-8"));
            String collect = ((Map<String, Object>) parameters).entrySet().stream()
                    .filter(entry -> StringUtils.isNotEmpty(entry.getValue()))
                    .map(entry -> {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        try {
                            return key.concat("=").concat(URLEncoder.encode(value, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            return key.concat("=").concat(value);
                        }
                    }).collect(Collectors.joining("&"));
            body = RequestBody.create(mediaType, collect);
        } else {
            MediaType mediaType = MediaType.parse(contentType.getValue().concat("; charset=utf-8"));
            body = RequestBody.create(mediaType, parameters.toString());
        }
        return body;
    }
}
