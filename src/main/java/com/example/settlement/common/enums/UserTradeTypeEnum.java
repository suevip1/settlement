package com.example.settlement.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.KeyValueHolder;

import static com.example.settlement.common.enums.FundDirectEnum.*;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 15:50
 */
@AllArgsConstructor
@Getter
public enum UserTradeTypeEnum {
    PAY(1, "支付", DIRECT_PAYMENT),
    REFUND(2, "退款", DIRECT_DEDUCTION),
    RISK_DEDUCT(3, "风险拒接扣除", DIRECT_DEDUCTION),
    ;
    private final int value;
    private String desc;
    private final FundDirectEnum direct;

    public static UserTradeTypeEnum valueOf(int value) {
        for (UserTradeTypeEnum userTradeTypeEnum : UserTradeTypeEnum.values()) {
            if (userTradeTypeEnum.value == value) {
                return userTradeTypeEnum;
            }
        }
        return null;
    }

}
