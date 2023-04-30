package com.example.settlement.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 各种手续费退费模式
 * @author yangwu_i
 * @date 2023/4/30 20:37
 */
@Getter
@AllArgsConstructor
public enum RefundModeEnum {
    TRADE_FEE_REFUND(1, "退交易手续费"),
    TRADE_FEE_UNREFUND(2, "不退交易手续费"),
    INSTALLMENT_FEE_REFUND(3, "退分期手续费"),
    INSTALLMENT_FEE_UNREFUND(4, "不退分期手续费"),
    ;
    private int value;
    private String desc;
}
