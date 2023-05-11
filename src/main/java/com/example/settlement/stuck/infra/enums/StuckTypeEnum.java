package com.example.settlement.stuck.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 12:24
 */
@AllArgsConstructor
@Getter
public enum StuckTypeEnum {

    Clearing(100, "清算单"),
    Settlement(200, "结算单");

    private int code;
    private String desc;

    public static StuckTypeEnum valueOf(int code) {
        for (StuckTypeEnum stuckTypeEnum : StuckTypeEnum.values()) {
            if (stuckTypeEnum.code == code) {
                return stuckTypeEnum;
            }
        }
        return null;
    }

}
