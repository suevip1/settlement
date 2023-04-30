package com.example.settlement.config.apollo;

import com.example.settlement.config.entity.TaxRuleConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 10:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MchBaseConfig {
    private String mchName;
    private String mchContactInfo;
    private Integer merchantType;
    private List<TaxRuleConfig> taxRate; // 税率
    private String notifyUrl; // 商户回调地址
    private String status;
    private int testMch; // 是否是测试商户
}
