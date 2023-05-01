package com.example.settlement.settle.model.valueobj;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.settle.infra.SettleErrorNo;
import com.example.settlement.settle.model.event.SettleBillInited;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 11:09
 */
public record SettleModel(
        long userId,
        Map<String, SettleConfig> configs, // 当前商户的所有结算配置
        Map<SettleKey, SettleBillInfo> bills // 所有未结算完的结算单
) {
    public String getSettleId(Integer userProduct, Date transTime) {
        SettleConfig config = getMatchConfig(userProduct, transTime);
        Date settleTime = config.getSettleTime(transTime);
        SettleBillInfo info = bills.get(new SettleKey(settleTime, userProduct));
        return Optional.ofNullable(info).map(SettleBillInfo::settleId).orElse(null);
    }

    private SettleConfig getMatchConfig(Integer userProduct, Date transTime) {
        return configs.values().stream().filter(e -> e.match(userId, userProduct, transTime))
                .max(SettleConfig::compareTo)
                .orElseThrow(() -> new ErrorNoException(SettleErrorNo.CONFIG_NOT_FOUND, ""));
    }

    public Pair<SettleBillInited, UnexpectedEvent> billInited(Integer userProduct, Date transTime) {
        return null;
    }
}
