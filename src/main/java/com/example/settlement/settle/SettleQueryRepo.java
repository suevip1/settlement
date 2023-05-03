package com.example.settlement.settle;

import com.example.settlement.config.apollo.ApolloMerchantConfig;
import com.example.settlement.config.apollo.MchBaseConfig;
import com.example.settlement.config.entity.TaxRuleConfig;
import com.example.settlement.settle.infra.db.entity.SettleBillEntity;
import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import com.example.settlement.settle.infra.db.mapper.SettleBillMapper;
import com.example.settlement.settle.infra.db.mapper.SettleDetailMapper;
import com.example.settlement.settle.model.domain.BillModel;
import com.example.settlement.settle.model.valueobj.DetailInfo;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 10:40
 */
@Slf4j
@Service
public class SettleQueryRepo {

    @Resource
    private ApolloMerchantConfig merchantConfig;
    @Resource
    private SettleBillMapper billMapper;
    @Resource
    private SettleDetailMapper detailMapper;

    public List<TaxRuleConfig> fetchTaxRule(long userId) {
        try {
            MchBaseConfig baseConfig = merchantConfig.getMerchantBaseConfig(String.valueOf(userId));
            if (baseConfig == null) {
                return Lists.newArrayList();
            }
            return baseConfig.getTaxRate();
        } catch (Exception e) {
            log.error("fetchTaxRule error", e);
            return Lists.newArrayList();
        }
    }

    public BillModel getSettleBindModel(Long userId, String settleId) {
        SettleBillEntity billEntity = billMapper.selectOne(userId, settleId);
        if (billEntity == null) {
            return null;
        }

        List<SettleDetailEntity> details = detailMapper.selectDetails(userId, settleId);
        List<DetailInfo> detailInfos = details.stream().map(this::toDetailInfo).collect(Collectors.toList());
        // todo: 获取税费配置
        return new BillModel(billEntity, detailInfos);
    }

    private DetailInfo toDetailInfo(SettleDetailEntity entity) {
        return new DetailInfo(entity.getDetailId(), entity.getSummaryTime(), entity.getState(), entity.getUserTradeType(), entity.getTotalCount());
    }

    public List<DetailInfo> getUnbindDetails(Long userId, Integer userProduct) {
        return detailMapper.selectUnbindDetails(userId, userProduct).stream()
                .map(detail -> new DetailInfo(detail.getDetailId(), detail.getSummaryTime(), detail.getState(), detail.getUserTradeType(), detail.getTotalCount()))
                .collect(Collectors.toList());
    }
}
