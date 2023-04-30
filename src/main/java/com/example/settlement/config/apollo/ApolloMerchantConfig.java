package com.example.settlement.config.apollo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 11:21
 */
@Slf4j
// @ApolloNamespace("merchant") todo: 集成 apollo
public class ApolloMerchantConfig {

    private static final Map<String, MerchantConfig> CONFIG_MAP = new ConcurrentHashMap<>();

    public MchBaseConfig getMerchantBaseConfig(String userId) {
        MerchantConfig merchantConfig = getMerchantConfig(userId);
        if (merchantConfig == null) {
            return null;
        }
        return merchantConfig.getMchBaseConfig();
    }

    private MerchantConfig getMerchantConfig(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        if (!CONFIG_MAP.containsKey(userId)) {
            return null;
        }
        return CONFIG_MAP.get(userId);
    }

    // // 初始化配置
    // @Override
    // public void change(Config config) {
    //     if (config == null) {
    //         log.error("apollo config is null");
    //         return ;
    //     }
    //     String merchantId = config.getConfigName(); // merchantId
    //     if (merchantId == null) {
    //         log.error("apollo config name is null");
    //         return ;
    //     }
    //     ConfigValue<MerchantConfig> merchantConfig = config.getConfigValue();
    //     if (merchantConfig != null && merchantConfig.getValue() != null) {
    //        CONFIG_MAP.put(merchantId, merchantConfig.getValue());
    //     }
    // }
}
