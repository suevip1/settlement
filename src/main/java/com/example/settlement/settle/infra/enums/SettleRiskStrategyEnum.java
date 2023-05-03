package com.example.settlement.settle.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结算风控拦截策略
 * @author yangwu_i
 * @date 2023/5/3 21:29
 */
@AllArgsConstructor
@Getter
public enum SettleRiskStrategyEnum {
    NORMAL(0, "全部过风控桩点"),
    MERCHANT_WHITELIST(1, "商户白名单")
    ;
    private int value;
    private String desc;

    public static SettleRiskStrategyEnum valueOf(Integer settleRiskStrategy) {
        if (settleRiskStrategy == null) {
            return NORMAL;
        }
        for (SettleRiskStrategyEnum value : values()) {
            if (value.getValue() == settleRiskStrategy) {
                return value;
            }
        }
        return NORMAL;
    }
}
