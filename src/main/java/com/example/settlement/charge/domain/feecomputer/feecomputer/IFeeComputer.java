package com.example.settlement.charge.domain.feecomputer.feecomputer;

/**
 * 计费模型接口
 * @author yangwu_i
 * @date 2023/4/10 23:17
 */
public interface IFeeComputer {
    /**
     * 计算手续费
     * @param tradeAmount 交易金额
     * @return 手续费
     */
    Long computeFee(Long tradeAmount);
}
