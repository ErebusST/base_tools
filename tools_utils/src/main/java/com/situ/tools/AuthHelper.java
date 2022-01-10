/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import com.situ.entity.bo.PayloadEntity;
import com.situ.entity.bo.UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Auth token
 *
 * @author 司徒彬
 * @date 2020 /7/24 11:00
 */
@Slf4j
public class AuthHelper {
    //payloadEntity.sub
    private static final String AUDIENCE = "Get Json Web Token For Java,The Exp:30 Days";

    private static final String ISSUER = "Swhl.com";

    //AccessKey
    private static final String SIGNING_KEY = "SWHL";

    /**
     * Create token string.
     *
     * @param userInfo    the user info
     * @param durationDay the duration days 天数
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static String createToken(UserInfo userInfo, Long durationDay) throws Exception {

        PayloadEntity payloadEntity = new PayloadEntity();
        //获得注册时间戳 按毫秒计算
        Long currentTimeMillis = System.currentTimeMillis();
        String iatString = DataSwitch.convertObjectToString(currentTimeMillis);
        payloadEntity.setIat(iatString);

        //获得过期时间时间戳  按毫秒计算
        String expString = DataSwitch.convertObjectToString(currentTimeMillis + durationDay * 24 * 60 * 60 * 1000);
        payloadEntity.setExp(expString);
        //objEntity.setExp(expString);

        //iss
        payloadEntity.setIss(ISSUER);

        //sub
        payloadEntity.setSub(AUDIENCE);

        userInfo.setPayloadEntity(payloadEntity);

        String sign = DataSwitch.convertObjectToJsonString(userInfo);
        String token = DesUtils.encrypt(sign, 0);
        return SIGNING_KEY.concat(token);
    }

    /**
     * Verify token user info.
     *
     * @param token the token
     * @return the user info
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static <T extends UserInfo> T verifyToken(Class<T> clazz, String token) throws Exception {
        //判断特殊字符
        if (token.indexOf(SIGNING_KEY) == -1) {
            log.error("没有包含特定的头信息,非法访问 :{} token:{}", SIGNING_KEY, token);
            throw new RuntimeException("没有包含特定的头信息,非法访问 : " + SIGNING_KEY + "," + token);
        } else {
            token = token.substring(SIGNING_KEY.length());

            String sign = DesUtils.decrypt(token, 0);
            T userInfo = DataSwitch.convertJsonStringToEntity(sign, clazz);
            PayloadEntity payloadEntity = userInfo.getPayloadEntity();
            if (StringUtils.equalsIgnoreCase(payloadEntity.getIss(), ISSUER)) {
                return userInfo;
            } else {
                return null;
            }
        }
    }


    /**
     * Verify token user info.
     *
     * @param token the token
     * @return the user info
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-10 11:45:12
     */
    public static UserInfo verifyToken(String token) throws Exception {
        return verifyToken(UserInfo.class, token);
    }

    /**
     * Validate auth rights boolean.
     *
     * @param authValue       the auth value
     * @param permissionCodes the permission codes
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:01
     */
    public static boolean validateAuthRights(String authValue, String permissionCodes) {
        List<String> permissionCodeList = StringUtils.splitToList(permissionCodes, ",")
                .stream().map(String::trim).collect(Collectors.toList());
        List<String> authValues = StringUtils.splitToList(authValue.trim(), ",");

        boolean hasRight = permissionCodeList.stream()
                .filter(pCode -> authValues.contains(pCode))
                .count() > 0;
        return hasRight;
    }
}
