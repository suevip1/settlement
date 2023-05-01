package com.example.settlement.settle.model.valueobj;

import com.example.settlement.clear.model.ClearingSetting;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 09:37
 */
public record SettleConfig (Instant SETTLE_ZERO, ChronoUnit TRADE_CLEARING_SUMMARY_MODE, ClearingSetting setting)
        implements Comparable<SettleConfig> {
    public SettleConfig(ClearingSetting setting) {
        // 汇总模式：目前只支持天级别汇总
        this(Instant.parse("2022-01-01T00:00:00Z"), ChronoUnit.DAYS, setting);
    }
    @Override
    public int compareTo(SettleConfig o) {
        return setting.getActivationTime().compareTo(o.setting().getActivationTime());
    }

    // 当前交易是否匹配结算配置
    public boolean match(long userId, int userProduct, Date transTime) {
        return setting.getUserId().equals(userId)
                && setting.getUserProduct().equals(userProduct)
                && setting.getActivationTime().compareTo(transTime) <= 0;
    }

    // 最近（3天）一段时间的起始时间（根据配置来处理最近结算详情单）
    public Date getStartAtTimeOfRecentPeriod() {
        if (TRADE_CLEARING_SUMMARY_MODE == ChronoUnit.DAYS) {
            return Date.from(Instant.now().atZone(setting.getZoneId()).truncatedTo(ChronoUnit.DAYS).minusDays(2).toInstant());
        }
        throw new ErrorNoException(ErrorNo.SERVER_ERROR, "暂不支持的结算配置");
    }

    // 取交易日期当天0点
    public Date getSummaryTime(Date transTime) {
         if (TRADE_CLEARING_SUMMARY_MODE == ChronoUnit.DAYS) {
             return Date.from(transTime.toInstant().atZone(setting.getZoneId()).truncatedTo(ChronoUnit.DAYS).toInstant());
         }
         throw new ErrorNoException(ErrorNo.SERVER_ERROR, "暂不支持的结算配置");
    }
}
