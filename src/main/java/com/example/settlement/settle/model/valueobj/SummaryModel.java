package com.example.settlement.settle.model.valueobj;

import com.alibaba.fastjson2.util.UUIDUtils;
import com.example.settlement.clear.model.ClearingSetting;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.settle.infra.SettlementErrorNo;
import com.example.settlement.settle.model.event.SummaryStarted;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 汇总模型
 * @author yangwu_i
 * @date 2023/5/1 09:33
 */
public record SummaryModel(
        long userId,
        Map<String, SettleConfig> configs, // 当前商户的所有结算配置
        Map<SummaryId, SummaryInfo> summaries // 尚在控制范围内的汇总详情单
) {

    // 最近控制时段的开始时间（前3天）
    public Date getStartAtTimeOfRecentPeriod(int userProduct, Date transTime) {
        SettleConfig config = getConfig(userProduct, transTime);
        return config.getStartAtTimeOfRecentPeriod();
    }

    private SettleConfig getConfig(int userProduct, Date transTime) {
         return configs.values().stream().filter(e -> e.match(userId, userProduct, transTime))
                 .max(SettleConfig::compareTo).orElseThrow(() -> new ErrorNoException(SettlementErrorNo.CONFIG_NOT_FOUND, "配置未找到" + userId));
    }

    public SummaryInfo getDetailId(Integer productType, Integer tradeType, Date transTime) {
        SettleConfig config = getConfig(productType, transTime);
        Date summaryTime = config.getSummaryTime(transTime);
        return summaries.get(new SummaryId(productType, tradeType, summaryTime));
    }

    // 判断汇总详情单是否已结算
    public boolean hasAttached2BillAndSettled(SummaryInfo info) {
        return info != null && info.state() == 1;
    }

    public SummaryStarted summaryInitialized(Integer productType, Integer tradeType, Date transTime) {
        SettleConfig config = getConfig(productType, transTime);
        Date summaryTime = config.getSummaryTime(transTime);
        ClearingSetting setting = config.setting();
        return new SummaryStarted(
                setting.getCountryCode(),
                setting.getUserId(),
                setting.getUserType(),
                setting.getUserProduct(),
                tradeType,
                setting.getConfigId(),
                UUID.randomUUID().toString(),
                setting.getSettleMode(),
                summaryTime,
                0,
                setting.getCurrency(),
                "");
    }
}
