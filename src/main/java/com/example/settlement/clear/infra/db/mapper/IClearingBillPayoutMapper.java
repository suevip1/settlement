package com.example.settlement.clear.infra.db.mapper;

import com.example.settlement.clear.infra.db.entity.ClearingBillPayoutEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 20:55
 */
@Mapper
// @Master
// @ShardingRule(talbe="clearing_bill_payout", column="userId", strategy=ShardingStrategy100.class)
public interface IClearingBillPayoutMapper {

    List<ClearingBillPayoutEntity> selectByOriginTradeIdAndFeeCodeList(Long userId, String tradeId, Integer tradeType, List<Integer> feeTypes);
}
