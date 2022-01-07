
/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;

/**
 * DES加密解密
 *
 * @author 司徒彬
 * @date 2014 -4-15
 */
@Component
public class DesUtils {
    @Autowired
    private Environment environment;
    private final static String MODE = "DES";

    /**
     * Init .
     *
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    @PostConstruct
    public void init() {
        KEY = environment.getProperty("des.key");
        IV = environment.getProperty("des.iv");
    }

    // 密钥
    private static String KEY;
    // 偏移量

    private static String IV;

    /**
     * Encrypt string.
     *
     * @param encryptString the encrypt string
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static String encrypt(String encryptString) throws Exception {
        if (StringUtils.isEmpty(encryptString)) {
            return "";
        }

        if (StringUtils.isEmpty(KEY)) {
            KEY = "AD67EA2F3BE6E5ADD368DFE03120B5DF";
        }
        if (StringUtils.isEmpty(IV)) {
            IV = "AS3F1AE4";
        }
        int length = KEY.length();
        if (length == 0 || length % 8 != 0) {
            throw new Exception("密钥长度不能为0且为8的整数倍!");
        }

        int count = length / 8;
        return encrypt(encryptString, count);
    }

    /**
     * 功能简介：加密
     *
     * @param encryptString the encrypt string
     * @param count         the count
     * @return string string
     * @throws Exception the exception
     * @author 司徒彬
     * @date 2016 /12/21 10:35
     * @since 2022 -01-07 15:35:59
     */
    public static String encrypt(String encryptString, int count) throws Exception {

        if (StringUtils.isEmpty(KEY)) {
            KEY = "AD67EA2F3BE6E5ADD368DFE03120B5DF";
        }
        if (StringUtils.isEmpty(IV)) {
            IV = "AS3F1AE4";
        }

        byte[] encryptStringByteArr = encryptString.getBytes(StaticValue.ENCODING);
        for (int i = 0; i < count; i++) {
            // 偏移量
            byte[] ivTemp = getKeyByStr(IV);
            IvParameterSpec iv = new IvParameterSpec(ivTemp);
            // 密钥
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MODE);
            String str = KEY.substring(8 * i, 8 * (i + 1));
            byte[] keyBytes = getKeyByStr(str);
            DESKeySpec dks = new DESKeySpec(keyBytes);
            SecretKey key = keyFactory.generateSecret(dks);
            // 加密
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            encryptStringByteArr = cipher.doFinal(encryptStringByteArr);
            encryptStringByteArr = Base64.encode(encryptStringByteArr).getBytes(StaticValue.ENCODING);
        }
        return Base64.encode(encryptStringByteArr);
    }


    /**
     * Decrypt string.
     *
     * @param decryptString the decrypt string
     * @return the string
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static String decrypt(String decryptString) throws Exception {

        if (StringUtils.isEmpty(KEY)) {
            KEY = "AD67EA2F3BE6E5ADD368DFE03120B5DF";
        }
        if (StringUtils.isEmpty(IV)) {
            IV = "AS3F1AE4";
        }

        if (StringUtils.isEmpty(decryptString)) {
            return "";
        }
        int length = KEY.length();
        if (length == 0 || length % 8 != 0) {
            throw new Exception("密钥长度不能为0且为8的整数倍!");
        }

        int count = length / 8;
        return decrypt(decryptString, count);
    }

    /**
     * 功能简介：解密 <p>
     *
     * @param decryptString the decrypt string
     * @param count         the count
     * @return string string
     * @throws Exception the exception
     * @author 司徒彬
     * @date 2016 /12/21 10:35
     * @since 2022 -01-07 15:35:59
     */
    public static String decrypt(String decryptString, int count) throws Exception {
        decryptString = new String(Base64.decode(decryptString), StaticValue.ENCODING);
        for (int i = count - 1; i >= 0; i--) {
            byte[] decryptStringByteArr = Base64.decode(decryptString);
            // 偏移量
            byte[] ivTemp = getKeyByStr(IV);
            IvParameterSpec iv = new IvParameterSpec(ivTemp);

            // 密钥
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MODE);
            String str = KEY.substring(8 * i, 8 * (i + 1));
            byte[] keyBytes = getKeyByStr(str);
            DESKeySpec dks = new DESKeySpec(keyBytes);
            SecretKey key = keyFactory.generateSecret(dks);

            // 解密
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            decryptStringByteArr = cipher.doFinal(decryptStringByteArr);
            decryptString = new String(decryptStringByteArr, StaticValue.ENCODING);
        }
        return new String(decryptString);
    }

    /**
     * 输入密码的字符形式，返回字节数组形式。 如输入字符串：AD67EA2F3BE6E5AD 返回字节数组：{173,103,234,47,59,230,229,173}
     *
     * @throws UnsupportedEncodingException
     */
    private static byte[] getKeyByStr(String str) throws UnsupportedEncodingException {
        return str.getBytes(StaticValue.ENCODING);
    }

}