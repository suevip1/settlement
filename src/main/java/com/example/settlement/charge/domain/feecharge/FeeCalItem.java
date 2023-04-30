package com.example.settlement.charge.domain.feecharge;

import com.example.settlement.charge.domain.feecomputer.feeitem.FeeConfig;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 16:56
 */
public record FeeCalItem(FeeConfig config, long feeAmount, String currency) {
}
