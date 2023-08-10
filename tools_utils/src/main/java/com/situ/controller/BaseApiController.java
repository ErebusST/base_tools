/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.situ.exception.MyException;
import com.situ.tools.ObjectUtils;
import com.situ.tools.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础api类
 *
 * @author 司徒彬
 * @date 2020 /6/20 17:40
 */
public class BaseApiController {
    /**
     * The constant FAILED_STR.
     */
    public static final String FAILED_STR = "failed";
    /**
     * The constant SUCCESS_STR.
     */
    public static final String SUCCESS_STR = "success";
    /**
     * The constant RESULT_KEY.
     */
    public static final String RESULT_KEY = "result";
    private static final String MESSAGE_KEY = "message";
    /**
     * The constant DATA_KEY.
     */
    public static final String DATA_KEY = "data";


    /**
     * Check boolean.
     *
     * @param object the object
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public boolean check(JsonObject object) {
        if (object.has(RESULT_KEY)) {
            String asString = object.get(RESULT_KEY).getAsString();
            return StringUtils.equalsIgnoreCase(asString, SUCCESS_STR);
        } else {
            return false;
        }
    }

    /**
     * Gets is used result.
     *
     * @return the is used result
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getIsUsedResult() {
        return this.getIsUsedResult("");
    }

    /**
     * Gets is used result.
     *
     * @param message the message
     * @return the is used result
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getIsUsedResult(String message) {
        JsonObject jsonObject = this.getResultJson("isUsed");
        if (ObjectUtils.isNotEmpty(message)) {
            jsonObject.addProperty(MESSAGE_KEY, message);
        }
        return jsonObject;
    }


    /**
     * Gets operation result json.
     *
     * @param isSuccess the is success
     * @return the operation result json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getOperationResultJson(boolean isSuccess) {
        return isSuccess ? getSuccessJson() : getFailedJson();
    }

    /**
     * Gets validate exist result json.
     *
     * @param isExist the is exist
     * @return the validate exist result json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getValidateExistResultJson(boolean isExist) {
        return isExist ? getExistJson() : getNotExistJson();
    }

    /**
     * Gets success json.
     *
     * @return the success json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getSuccessJson() {
        JsonObject result = getResultJson(SUCCESS_STR);
        result.addProperty("status", 200);
        return result;
    }

    /**
     * Gets success json.
     *
     * @param message the message
     * @return the success json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getSuccessJson(String message) {
        JsonObject result = this.getResultJson(SUCCESS_STR, message);
        result.addProperty("status", 200);
        return result;
    }


    /**
     * 获得返回的结果Json
     *
     * @param jsonElement the json element
     * @return the result json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getSuccessJson(JsonElement jsonElement) {
        JsonObject result = this.getResultJson(SUCCESS_STR, jsonElement);
        result.addProperty("status", 200);
        return result;
    }

    /**
     * Gets result json.
     *
     * @param jsonElement the result
     * @return the result json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getFailedJson(JsonElement jsonElement) {
        JsonObject result = this.getResultJson(FAILED_STR, jsonElement);
        result.addProperty("status", 400);
        return result;
    }

    /**
     * Gets result json.
     *
     * @param result the result
     * @return the result json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getResultJson(String result) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RESULT_KEY, result);
        return jsonObject;
    }

    /**
     * Gets result json.
     *
     * @param result  the result
     * @param message the message
     * @return the result json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getResultJson(String result, String message) {
        JsonObject jsonObject = getResultJson(result);
        jsonObject.addProperty(MESSAGE_KEY, message);
        return jsonObject;
    }

    /**
     * Gets result json.
     *
     * @param result      the result
     * @param jsonElement the json element
     * @return the result json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getResultJson(String result, JsonElement jsonElement) {
        JsonObject jsonObject = this.getResultJson(result);
        jsonObject.add(DATA_KEY, jsonElement);
        return jsonObject;
    }


    /**
     * Gets exist json.
     *
     * @return the exist json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getExistJson() {
        JsonObject result = getResultJson("exist");
        result.addProperty("status", 200);
        return result;
    }

    /**
     * Gets exist json.
     *
     * @param field the field
     * @return the exist json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getExistJson(String field) {
        JsonObject result = getExistJson();
        result.addProperty("field", field);
        String message = String.format("The field named [%s] value is exist.", field);
        result.addProperty(MESSAGE_KEY, message);
        result.addProperty("status", 400);
        return result;
    }


    /**
     * Gets not exist json.
     *
     * @return the not exist json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getNotExistJson() {
        return getResultJson("no_exist");
    }

    /**
     * Gets failed json.
     *
     * @return the failed json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getFailedJson() {
        JsonObject result = getResultJson(FAILED_STR);
        result.addProperty("status", 400);
        return result;
    }

    /**
     * Gets failed json.
     *
     * @param message the message
     * @return the failed json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getFailedJson(String message) {
        JsonObject jsonObject = getFailedJson();
        jsonObject.addProperty(MESSAGE_KEY, message);
        return jsonObject;
    }


    /**
     * 获得异常的结果Json
     *
     * @param e       the e
     * @param clazz   the clazz
     * @param message the message
     * @param params  the params
     * @return the result json
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getFailedJson(Exception e, Class clazz, String message, Object... params) {
        return new MyException(e, clazz, message, params).getPageMessage();
    }


    /**
     * Convert request to entity t.
     *
     * @param <T>     the type parameter
     * @param clazz   the clazz
     * @param request the request
     * @return the t
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:10
     */
    public <T> T convertRequestToEntity(Class<T> clazz, HttpServletRequest request) {
        try {
            //T t = clazz.newInstance();

            String data = request.getParameter("config");

            Gson gson = new Gson();
            T t = gson.fromJson(data, clazz);
            return t;
        } catch (Exception ex) {
            throw ex;
        }
    }


}
