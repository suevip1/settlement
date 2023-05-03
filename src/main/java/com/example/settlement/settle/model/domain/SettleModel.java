package com.example.settlement.settle.model.domain;

import com.example.settlement.clear.model.ClearingSetting;
import com.example.settlement.common.enums.UserProductTypeEnum;
import com.example.settlement.common.event.ErrorNoUnexpectedEvent;
import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.settle.infra.SettleErrorNo;
import com.example.settlement.settle.infra.enums.SettleStatusEnum;
import com.example.settlement.settle.infra.enums.SettleTypeEnum;
import com.example.settlement.settle.model.event.SettleBillInited;
import com.example.settlement.settle.model.valueobj.SettleBillInfo;
import com.example.settlement.settle.model.valueobj.SettleConfig;
import com.example.settlement.settle.model.valueobj.SettleKey;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

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
        // 去重
        SettleBillInfo info = bills.get(new SettleKey(settleTime, userProduct));
        return Optional.ofNullable(info).map(SettleBillInfo::settleId).orElse(null);
    }

    private SettleConfig getMatchConfig(Integer userProduct, Date transTime) {
        return configs.values().stream().filter(e -> e.match(userId, userProduct, transTime))
                .max(SettleConfig::compareTo)
                .orElseThrow(() -> new ErrorNoException(SettleErrorNo.CONFIG_NOT_FOUND, ""));
    }

    public Pair<SettleBillInited, UnexpectedEvent> billInited(Integer userProduct, Date transTime) {
        SettleConfig config = getMatchConfig(userProduct, transTime);
        if (config == null) {
            return ImmutablePair.right(new ErrorNoUnexpectedEvent(SettleErrorNo.CONFIG_NOT_FOUND, ""));
        }

        // 判断上一个结算单状态,是否没有进入结算环节（没有进行资金处理）
        if (isLastBillBeingAudited(config.setting().getConfigId())) {
            return ImmutablePair.right(new ErrorNoUnexpectedEvent(SettleErrorNo.LAST_BILL_IS_BEING_AUDITED, ""));
        }

        Date settleTime = config.getSettleTime(transTime);
        Date liquidStartTime = config.getLiquidStartTime(transTime);
        Date liquidEndTime = config.getLiquidEndTime(transTime);

        SettleBillInited billInited = build(config.setting());

        billInited.setSettleId(UUID.randomUUID().toString());
        billInited.setSettleTime(settleTime);
        billInited.setSettleStartTime(settleTime);
        billInited.setLiquidStartTime(liquidStartTime);
        billInited.setLiquidEndTime(liquidEndTime);
        billInited.setSettleCycle(config.getSettleCycle().getValue());

        return ImmutablePair.left(billInited);
    }

    private SettleBillInited build(ClearingSetting setting) {
        int settleType = 0;
        if (UserProductTypeEnum.PAY_IN.getValue() == setting.getUserProduct()) {
            settleType = SettleTypeEnum.PAY_INT_NET_SETTLE.getValue();
        } else if (UserProductTypeEnum.PAY_OUT.getValue() == setting.getUserProduct()) {
            settleType = SettleTypeEnum.PAY_OUT_NET_SETTLE.getValue();
        }
        return SettleBillInited.builder()
                .configId(setting.getConfigId())
                .countryCode(setting.getCountryCode())
                .userId(setting.getUserId())
                .userType(setting.getUserType())
                .settleType(settleType)
                .currency(setting.getCurrency())
                .settleMode(setting.getSettleMode())
                .remark("")
                .build();
    }

    // todo: 待理解
    // 一个商户一个结算模型，不能同时存在两个结算单
    // 如果上一个结算单已经进行资金处理，不会影响下一个结算单的处理
    private boolean isLastBillBeingAudited(String configId) {
        return bills.values().stream()
                .filter(e -> e.configId().equals(configId))
                .max(Comparator.comparing(SettleBillInfo::settleTime))
                .map(info -> {
                    SettleStatusEnum status = SettleStatusEnum.valueOf(info.settleStatus());
                    return status != null && !status.isFundSetting();
                })
                .orElse(false);
    }
}
