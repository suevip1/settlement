package com.example.settlement.charge.domain.feecomputer.feecomputer;

import java.math.BigDecimal;

/**
 * a + bx 计费模型
 * @author yangwu_i
 * @date 2023/4/10 23:21
 */
public record LinearComputer (long feeBase, float feeRate, long feeMax, long feeMin)
        implements IFeeComputer{
    public LinearComputer {
        feeBase = feeBase < 0 ? 0 : feeBase;
        feeRate = feeRate < 0 ? 0 : feeRate;
        feeMax = feeMax < 0 ? 0 : feeMax;
        feeMin = feeMin < 0 ? 0 : feeMin;
    }

    @Override
    public Long computeFee(Long tradeAmount) {
        long fee = new BigDecimal(feeBase + tradeAmount * feeRate)
                .setScale(0, BigDecimal.ROUND_HALF_EVEN)
                .longValue();
        if (fee > feeMax) {
            return feeMax;
        }
        if (fee < feeMin) {
            return feeMin;
        }
        return fee;
    }
}
