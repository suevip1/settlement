package com.example.settlement.clear.infra.db.mapper;

import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author yangwu_i
 * @date 2023/4/26 22:03
 */
// @Master todo:
@Mapper
// @ShardingRule(table = "clearing_bill", column = "tradeId", strategy = ShardingStrategy1000.class) todo:
public interface IClearingBillMapper {
    ClearingBillEntity selectByTradeIdAndTradeType(@Param("tradeId") String tradeId, @Param("tradeType") Integer tradeType);

    int insertSelective(ClearingBillEntity entity);

    int updateRemark(ClearingBillEntity entity);
}
