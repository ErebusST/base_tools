/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

/**
 * XML文件操作类
 *
 * @author 司徒彬
 * @date 2016 /10/17 22:53
 */
public class XmlUtils {
    /**
     * Gets filter entities.
     *
     * @param key    the key
     * @param xmlStr the xml str
     * @return the filter entities
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:59
     */
    public static String getKeyValue(String key, String xmlStr) throws Exception {
        try {
            SAXReader reader = new SAXReader();
            Element rootElement = reader.read(new ByteArrayInputStream(xmlStr
                    .getBytes("UTF-8"))).getRootElement();
            List<Element> elements = rootElement.elements();
            Optional<Element> optional = elements.stream().filter(element -> element.getName().equalsIgnoreCase(key)).findFirst();
            if (optional.equals(Optional.empty())) {
                return null;
            } else {
                return optional.get().getTextTrim();
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

}
