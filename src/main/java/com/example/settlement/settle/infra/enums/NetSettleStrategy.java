package com.example.settlement.settle.infra.enums;

import lombok.AllArgsConstructor;

/**
 *
 * @author yangwu_i
 * @date 2023/5/7 14:49
 */
@AllArgsConstructor
public enum NetSettleStrategy {
    TO_CARD(1, "到卡"),
    TO_CASH_ACCT(2, "到现金账户余额");
    private int value;
    private String desc;

    public static NetSettleStrategy valueOf(Integer netSettleStrategy) {
        if (netSettleStrategy == null) {
            return null;
        }
        for (NetSettleStrategy strategy : NetSettleStrategy.values()) {
            if (strategy.value == netSettleStrategy) {
                return strategy;
            }
        }
        return null;
    }
}
