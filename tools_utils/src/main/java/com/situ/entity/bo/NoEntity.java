package com.situ.entity.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * 编号实体
 *
 * @author 司徒彬
 * @date 2017-08-20 11:03
 */
@Setter
@Getter
public class NoEntity {
    private int countLength = 4;

    private String type;

    private String dateStr;

    private Long count = 1L;


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
