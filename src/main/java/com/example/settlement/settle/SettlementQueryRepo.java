package com.example.settlement.settle;

import com.example.settlement.config.apollo.ApolloMerchantConfig;
import com.example.settlement.config.apollo.MchBaseConfig;
import com.example.settlement.config.entity.TaxRuleConfig;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 10:40
 */
@Slf4j
@Service
public class SettlementQueryRepo {

    @Resource
    private ApolloMerchantConfig merchantConfig;

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

}
