package com.example.settlement.settle.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;

/**
 *
 * @author yangwu_i
 * @date 2023/5/2 11:27
 */
@AllArgsConstructor
@Getter
public enum SettleTypeEnum {
    PAY_INT_NET_SETTLE(1, "商户收单净额结算单"),
    PAY_OUT_NET_SETTLE(2, "商户代付净额结算单"),
    ;

    private int value;
    private String desc;
}
