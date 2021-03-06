package com.situ.entity.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 编号实体
 *
 * @author 司徒彬
 * @date 2017 -08-20 11:03
 */
@Setter
@Getter
public class NoEntity  implements Serializable {
    private int countLength = 4;

    private String type;

    private String dateStr;

    private Long count = 1L;


    /**
     * Get no string.
     *
     * @return the no
     * @author ErebusST
     * @since 2022 -01-07 15:36:09
     */
    public String getNO() {
        String countStr = count.toString();
        int length = countStr.length();
        if (length < countLength) {
            for (int i = 0; i < countLength - length; i++) {
                countStr = "0" + countStr;
            }
        }
        return type + dateStr + countStr;
    }

    @Override
    public String toString() {
        return "NoEntity{" +
                "countLength=" + countLength +
                ", type='" + type + '\'' +
                ", dateStr='" + dateStr + '\'' +
                ", count=" + count +
                '}';
    }
}
