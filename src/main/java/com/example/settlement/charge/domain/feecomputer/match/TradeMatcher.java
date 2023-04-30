package com.example.settlement.charge.domain.feecomputer.match;

import com.example.settlement.charge.domain.feecharge.TradeInfo;

import java.util.Date;

/**
 * 收费匹配子域，负责收费匹配逻辑
 * @author yangwu_i
 * @date 2023/4/10 22:10
 */
public record TradeMatcher(
        Integer productType,
        Integer tradeType,
        Integer payType,
        String currency,
        Date activeDate,
        Date expireDate) {
    public boolean match(TradeInfo tradeInfo) {
        for (IMatcherPolicyUtil policy : IMatcherPolicyUtil.FULL_MATCH_POLICIES) {
            if (!policy.match(this, tradeInfo)) {
                return false;
            }
        }
        return true;
    }
}