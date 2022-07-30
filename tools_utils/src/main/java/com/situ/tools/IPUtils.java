/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.lionsoul.ip2region.xdb.Searcher;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * IP Util
 *
 * @author 司徒彬
 * @date 2017 -03-10 13:55
 */
@Slf4j
public class IPUtils {

    /**
     * 获取本机IP
     *
     * @return ip addr
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static String getIpAddr() {
        String hostAddress = InetAddress.getLoopbackAddress().getHostAddress();
        return hostAddress;
    }

    /**
     * 获取访问端IP ErebusST 2017年2月15日23:43:57
     *
     * @param request the request
     * @return ip addr
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.split(":").length >= 2) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    public static String DB_PATH = "/Users/erebus/Downloads/ip2region.xdb";

    @Test
    public void test(){
        String s = toAddress("120.244.236.77");
        log.info(s);
    }

    public static String toAddress(String ip) {
        try {
            if (StringUtils.isEmpty(DB_PATH)) {
                DB_PATH = SpringContextUtils.getApplicationContext().getEnvironment().getProperty("ip.db");
                if (StringUtils.isEmpty(DB_PATH)) {
                    return "";
                }
            }
            Searcher searcher = Searcher.newWithFileOnly(DB_PATH);
            return searcher.search(ip);
        } catch (Exception ex) {
            return "";
        }
    }
}
