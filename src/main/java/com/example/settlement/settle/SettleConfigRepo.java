package com.example.settlement.settle;

import com.example.settlement.clear.model.ClearingSetting;
import com.example.settlement.common.enums.UserProductTypeEnum;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.config.ConfigMaintainService;
import com.example.settlement.config.entity.SettlementConfigEntity;
import com.example.settlement.settle.infra.SettleErrorNo;
import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import com.example.settlement.settle.infra.db.mapper.SettleDetailMapper;
import com.example.settlement.settle.infra.db.mapper.SettleBillMapper;
import com.example.settlement.settle.infra.enums.SettleStatusEnum;
import com.example.settlement.settle.model.domain.GenerateBillModel;
import com.example.settlement.settle.model.domain.SummaryModel;
import com.example.settlement.settle.model.valueobj.*;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 10:09
 */
public class SettleConfigRepo {
    @Resource
    private ConfigMaintainService configMaintainService;
    @Resource
    private SettleBillMapper settleBillMapper;

    @Resource
     private SettleDetailMapper settleDetailMapper;

    public GenerateBillModel getUserSettleModel(Long userId) {
        // 1. 获取当前生效的 settleConfig
        List<SettlementConfigEntity> configs = configMaintainService.getSettleConfig(userId);
        if (CollectionUtils.isEmpty(configs)) {
            throw new ErrorNoException(SettleErrorNo.CONFIG_NOT_FOUND, "配置未找到" + userId);
        }

        Map<String, SettleConfig> configMap = Maps.newHashMap();
        configs.forEach(config -> configMap.put(
                config.getConfigId(),
                new SettleConfig(toSetting(config))));

        // 2. 获取所有未结算的单子（按 product 分组）
        List<SettleBillInfo> bills = settleBillMapper.selectAllSettleInfo(userId, SettleStatusEnum.unSettled());
        if (bills.stream().anyMatch(e -> !configMap.containsKey(e.configId()))) {
            throw new ErrorNoException(SettleErrorNo.CONFIG_NOT_FOUND, "配置未找到" + userId);
        }

        Map<SettleKey, SettleBillInfo> refer = new HashMap<>();
        bills.forEach(bill -> refer.put(
                new SettleKey(bill.settleTime(), configMap.get(bill.configId()).setting().getUserProduct()),
                bill));
        return new GenerateBillModel(userId, configMap, refer);
    }

    public SummaryModel getUserSummaryModel(Long userId) {
        // 1. 获取当前生效的 settleConfig
        List<SettlementConfigEntity> configs = configMaintainService.getSettleConfig(userId);
        if (CollectionUtils.isEmpty(configs)) {
            throw new ErrorNoException(SettleErrorNo.CONFIG_NOT_FOUND, "配置未找到" + userId);
        }

        Map<String, SettleConfig> configMap = Maps.newHashMap();
        configs.forEach(config -> configMap.put(
                config.getConfigId(), new SettleConfig(toSetting(config))));

        Map<SummaryId, SummaryInfo> refer = new HashMap<>();
        SummaryModel model = new SummaryModel(userId, configMap, refer);

        // 2. 找 recent（3天） 之内的 details（
        List<SettleDetailEntity> details = settleDetailMapper.selectRecentDetails(userId, model.getStartAtTimeOfRecentPeriod(UserProductTypeEnum.PAY_IN.getValue(), new Date()));
        details.forEach(detail -> refer.put(
                new SummaryId(detail.getUserProduct(), detail.getUserTradeType(), detail.getSummaryTime()),
                new SummaryInfo(detail.getDetailId(), detail.getState())));
        return model;
    }

    private ClearingSetting toSetting(SettlementConfigEntity config) {
        if (config == null) {
            return null;
        }
        ClearingSetting setting = new ClearingSetting();
        BeanUtils.copyProperties(config, setting);
        // mock
        setting.setZoneId(ZoneId.systemDefault());
        setting.setCityId(0L); // mock 北京cityId
        return setting;
    }
}
