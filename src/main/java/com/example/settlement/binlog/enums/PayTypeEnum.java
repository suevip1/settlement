package com.example.settlement.binlog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 00:59
 */
@AllArgsConstructor
@Getter
public enum PayTypeEnum {
    CARD_TYPE(1, "银行卡"),
    BALANCE(2, "余额"),
    NONE(3, "无");
    private final int value;
    private final String desc;

    public static PayTypeEnum valueOf(int value) {
        for (PayTypeEnum payTypeEnum : PayTypeEnum.values()) {
            if (payTypeEnum.value == value) {
                return payTypeEnum;
            }
        }
        return null;
    }
}

