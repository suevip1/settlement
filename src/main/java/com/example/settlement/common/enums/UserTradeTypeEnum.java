package com.example.settlement.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 15:50
 */
@AllArgsConstructor
@Getter
public enum UserTradeTypeEnum {
    PAY(1, "支付"),
    REFUND(2, "退款");
    private final Integer value;
    private String desc;
}
