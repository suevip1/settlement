package com.example.settlement.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 13:40
 */
@Getter
@AllArgsConstructor
public enum FundDirectEnum {
    DIRECT_PAYMENT(1, "正向交易"),
    DIRECT_REFUND(2, "逆向交易");
    private final int value;
    private final String desc;
}
