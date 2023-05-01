package com.example.settlement.settle.model.valueobj;

import java.util.Map;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 11:09
 */
public record SettleModel(
        long userId,
        Map<String, SettleConfig> configs, // 当前商户的所有结算配置
        Map<SettleId, SettleBillInfo> bills // 所有未结算完的结算单
) {
}
