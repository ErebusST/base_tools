/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;


import com.situ.enumeration.MessageDigestEnum;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * MessageDigest计算工具类
 *
 * @author 司徒彬
 */
public class MessageDigestUtils {
    private static final int BUFFER_SIZE = 5 * 1024;

    /**
     * 获取MD5签名1
     *
     * @param signParameters the sign parameters
     * @return the signature by md 5
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String getSignatureByMd5(Map<String, Object> signParameters)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            return getSignature(signParameters, MessageDigestEnum.MD5);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets signature by sha 1.
     *
     * @param signParameters the sign parameters
     * @return the signature by sha 1
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String getSignatureBySha1(Map<String, Object> signParameters)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            return getSignature(signParameters, MessageDigestEnum.SHA1);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets signature.
     *
     * @param signParameters    the sign parameters
     * @param messageDigestEnum the message digest enumration
     * @return the signature
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String getSignature(Map<String, Object> signParameters, MessageDigestEnum messageDigestEnum)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return getSignature(signParameters, messageDigestEnum, true);
    }

    /**
     * 获取签名
     *
     * @param signParameters     the sign parameters
     * @param messageDigestEnum  the message digest enumration
     * @param isContainSplitChar the is contain split char
     * @return the signature
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String getSignature(Map<String, Object> signParameters, MessageDigestEnum messageDigestEnum, boolean isContainSplitChar)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            StringBuilder signBuilder = new StringBuilder();
            signParameters.keySet().stream().sorted().forEach(key ->
            {
                Object parameter = signParameters.get(key);
                if (isContainSplitChar == true) {
                    signBuilder.append("&").append(key).append("=").append(parameter);
                } else {
                    signBuilder.append(key).append("=").append(parameter);
                }
            });

            String signStr =
                    signBuilder.charAt(0) != '&' ? signBuilder.toString() : signBuilder.deleteCharAt(0).toString();
            signStr = messageDigest(signStr, messageDigestEnum);
            return signStr;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 把字符串MD5签名
     *
     * @param str the str
     * @return the string
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String md5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return messageDigest(str, MessageDigestEnum.MD5);
    }

    /**
     * 把字符串sha1签名
     *
     * @param str the str
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String sha1(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return messageDigest(str, MessageDigestEnum.SHA1);
    }

    /**
     * Message digest string.
     *
     * @param str               the str
     * @param messageDigestEnum the message digest enumration
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String messageDigest(String str, MessageDigestEnum messageDigestEnum)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            MessageDigest messageDigest = createMessageDigest(messageDigestEnum);
            byte[] bytes = messageDigest.digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(bytes.length << 1);
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Character.forDigit((bytes[i] >> 4) & 0xf, 16));
                sb.append(Character.forDigit(bytes[i] & 0xf, 16));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Create message digest message digest.
     *
     * @param messageDigestEnum the message digest enum
     * @return the message digest
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static MessageDigest createMessageDigest(MessageDigestEnum messageDigestEnum) throws NoSuchAlgorithmException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(messageDigestEnum.getValue());
            return messageDigest;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 计算文件的MD5
     *
     * @param inputFile the input file
     * @return the string
     * @throws IOException              the io exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String countFileMD5(String inputFile) throws IOException, NoSuchAlgorithmException {
        // 缓冲区大小（这个可以抽出一个参数）
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = createMessageDigest(MessageDigestEnum.MD5);
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[BUFFER_SIZE];
            while (digestInputStream.read(buffer) > 0) {
                ;
            }
            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return byteArrayToHex(resultByteArray);
        } catch (Exception e) {
            throw e;
        } finally {
            if (digestInputStream != null) {
                digestInputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    /**
     * 下面这个函数用于将字节数组换成成16进制的字符串
     *
     * @param byteArray the byte array
     * @return the string
     */
    private static String byteArrayToHex(byte[] byteArray) {
        String hs = "";
        String temp;
        for (int n = 0; n < byteArray.length; n++) {
            temp = (Integer.toHexString(byteArray[n] & 0XFF));
            if (temp.length() == 1) {
                hs = hs + "0" + temp;
            } else {
                hs = hs + temp;
            }
            if (n < byteArray.length - 1) {
                hs = hs + "";
            }
        }
        return hs;
    }

    private final static String[] hexDigits = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f"
    };

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串 string
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换byte到16进制
     *
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * MD5编码
     *
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果 string
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public static String WeChatMD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(resultString.getBytes("UTF-8"));
            resultString = byteArrayToHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

}
