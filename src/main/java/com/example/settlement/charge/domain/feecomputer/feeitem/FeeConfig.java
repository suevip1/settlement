package com.example.settlement.charge.domain.feecomputer.feeitem;

/**
 * 收费子项配置，用于描述收费子项的配置信息
 * @author yangwu_i
 * @date 2023/4/10 23:10
 */
public record FeeConfig(
        Integer feeCode,
        String configId,
        String currency) {
}
