package com.example.settlement.config.apollo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 11:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantConfig {
    private MchBaseConfig mchBaseConfig;
    private MchKey mchKey;
}
