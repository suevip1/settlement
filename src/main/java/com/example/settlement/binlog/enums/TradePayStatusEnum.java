package com.example.settlement.binlog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.management.loading.MLetContent;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 00:44
 */
@Getter
@AllArgsConstructor
public enum TradePayStatusEnum {
    PAY_SUCCESS(1, "支付成功");
    private final int value;
    private final String desc;
}
