package com.example.settlement.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/4/26 22:10
 */
@AllArgsConstructor
@Getter
public enum SettleModeEnum {
    CYCLE(0, "周期结算"),
    REAL_TIME(1, "实时结算"),
    ;
    private int value;
    private String desc;

    public static SettleModeEnum getByValue(int value) {
        for (SettleModeEnum modeEnum : SettleModeEnum.values()) {
            if (modeEnum.getValue() == value) {
                return modeEnum;
            }
        }
        return null;
    }

}
