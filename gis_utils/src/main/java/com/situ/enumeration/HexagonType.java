/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.enumeration;

import lombok.Getter;

/**
 * The enum Hexagon type.
 *
 * @author 司徒彬
 * @date 2020 -01-08 13:14
 */
public enum HexagonType {

    /**
     * Up hexagon type.
     */
    UP("UP", "向上"),
    /**
     * Down hexagon type.
     */
    DOWN("DOWN", "向下"),
    /**
     * Left hexagon type.
     */
    LEFT("LEFT", "向左"),
    /**
     * Right hexagon type.
     */
    RIGHT("RIGHT", "向右"),
    ;

    private HexagonType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    @Getter
    private String type;
    @Getter
    private String description;
}
