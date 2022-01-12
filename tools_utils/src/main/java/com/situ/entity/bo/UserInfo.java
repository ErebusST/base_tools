/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import com.google.gson.JsonObject;
import com.situ.tools.DataSwitch;
import com.situ.tools.ObjectUtils;
import com.situ.tools.ReflectionUtils;
import com.situ.tools.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 用户信息
 *
 * @author 司徒彬
 * @date 2020 /7/24 10:58
 */
@Getter
@Setter
public class UserInfo {
    private String uKey;
    private Long userId;
    private String loginName;
    private String userName;
    private String password;
    private String type;
    private String phone;
    private String sex;

    private String permissionCodes;
    private String requestIp;

    private String requestUrl;

    private PayloadEntity payloadEntity;
    private String unionId;


    private Long loginTime = 0L;
    private Long lastOperationTime = 0L;


    @Override
    public String toString() {
        return "UserInfo{" +
                "uKey='" + uKey + '\'' +
                ", userId=" + userId +
                ", loginName='" + loginName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                ", phone='" + phone + '\'' +
                ", sex='" + sex + '\'' +
                ", permissionCodes='" + permissionCodes + '\'' +
                ", requestIp='" + requestIp + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", payloadEntity=" + payloadEntity +
                ", unionId='" + unionId + '\'' +
                ", loginTime=" + loginTime +
                ", lastOperationTime=" + lastOperationTime +
                '}';
    }



    /**
     * Get login info json object.
     *
     * @return the login info
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public JsonObject getLoginInfo() {
        Field[] fields = ReflectionUtils.getFields(this.getClass());

        JsonObject object = new JsonObject();

        Arrays.stream(fields)
                .forEach(field -> {
                    Object value = ReflectionUtils.getFieldValue(this, field.getName());
                    if(ObjectUtils.isNotNull(value)){
                        object.addProperty(field.getName(), DataSwitch.convertObjectToString(value));
                    }
                });
        return object;
    }

}
