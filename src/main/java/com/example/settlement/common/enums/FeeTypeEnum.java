package com.example.settlement.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 13:33
 */
@Getter
@AllArgsConstructor
public enum FeeTypeEnum {
    PROCESSED_TRADE_FEE(1, "已处理交易费"),
    UNPROCESSED_TRADE_FEE(2, "未处理交易费（期末统一结算）"),
    PROCESSED_INSTALLMENT_FEE(3, "已处理分期费"),
    UNPROCESSED_INSTALLMENT_FEE(4, "未处理分期费（期末统一结算）"),
    PROCESSED_TAX_FEE(5, "已处理税费"),
    UNPROCESSED_TAX_FEE(6, "已处理税费"),
    SETTLED_NET_AMOUNT(7, "已结算净额"),
    UNSETTLED_NET_AMOUNT(8, "未结算净额（期末统一结算）"),
    ;
    private final int code;
    private final String desc;

    public static FeeTypeEnum valueOf(int code) {
        for (FeeTypeEnum feeTypeEnum : FeeTypeEnum.values()) {
            if (feeTypeEnum.getCode() == code) {
                return feeTypeEnum;
            }
        }
        return null;
    }
}
