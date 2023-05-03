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
    // 费项方向，扣除B商户的钱
    DIRECT_DEDUCTION(-1, "直接扣除");
    private final int value;
    private final String desc;
}
