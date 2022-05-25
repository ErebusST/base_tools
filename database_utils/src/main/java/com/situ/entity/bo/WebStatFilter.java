/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 司徒彬
 * @date 2022/5/25 17:45
 */
@Getter
@Setter
public class WebStatFilter {
    String urlPattern;
    String exclusions;

    public Map toMap(){
        Map<String,Object> setting=new HashMap<>(2);
        setting.put("urlPattern",urlPattern);
        setting.put("exclusions",exclusions);
        return setting;
    }
}
