package com.example.settlement.charge.domain.feecharge;

import java.util.Date;

/**
 * 交易信息，来自上游交易系统，用于匹配收费规则，计算手续费
 * @author yangwu_i
 * @date 2023/4/10 22:31
 */
public record TradeInfo(
        Long userId,
        Integer userType,
        Integer userProduct,
        Integer userTradeType,
        Integer userPayType,
        String countryCode,
        Integer cardGroup,
        Date tradeTime,
        Long tradeAmount,
        Long tradeFee,
        String tradeCurrency) {
}
