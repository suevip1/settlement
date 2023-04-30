package com.example.settlement.charge.domain.feecomputer.match;

import com.example.settlement.charge.domain.feecharge.TradeInfo;

import java.util.Objects;

/**
 * 匹配策略接口
 * @author yangwu_i
 * @date 2023/4/10 22:42
 */
public interface IMatcherPolicyUtil {
    IMatcherPolicyUtil PRODUCT_TYPE_MATCH_POLICY = (tradeMatcher, tradeInfo) -> Objects.equals(tradeMatcher.productType(), tradeInfo.userProduct());
    IMatcherPolicyUtil TRADE_TYPE_MATCH_POLICY = (tradeMatcher, tradeInfo) -> Objects.equals(tradeMatcher.tradeType(), tradeInfo.userTradeType());
    IMatcherPolicyUtil PAY_TYPE_MATCH_POLICY = (tradeMatcher, tradeInfo) -> Objects.equals(tradeMatcher.payType(), tradeInfo.userPayType());
    IMatcherPolicyUtil TRADE_CURRENCY_MATCH_POLICY = (tradeMatcher, tradeInfo) -> Objects.equals(tradeMatcher.currency(), tradeInfo.tradeCurrency());
    IMatcherPolicyUtil ACTIVE_DATE_MATCH_POLICY = (tradeMatcher, tradeInfo) -> tradeMatcher.activeDate().before(tradeInfo.tradeTime());
    IMatcherPolicyUtil EXPIRE_DATE_MATCH_POLICY = (tradeMatcher, tradeInfo) -> tradeMatcher.expireDate().after(tradeInfo.tradeTime());
    IMatcherPolicyUtil[] FULL_MATCH_POLICIES = {
            PRODUCT_TYPE_MATCH_POLICY, TRADE_TYPE_MATCH_POLICY, PAY_TYPE_MATCH_POLICY,
            TRADE_CURRENCY_MATCH_POLICY, ACTIVE_DATE_MATCH_POLICY, EXPIRE_DATE_MATCH_POLICY };

    /**
        * 匹配策略
        * @param tradeMatcher
        * @param tradeInfo
        * @return
        */
    boolean match(TradeMatcher tradeMatcher, TradeInfo tradeInfo);

}
