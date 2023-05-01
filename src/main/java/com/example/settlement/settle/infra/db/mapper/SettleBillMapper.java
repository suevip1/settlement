package com.example.settlement.settle.infra.db.mapper;

import com.example.settlement.settle.infra.db.entity.SettleBillEntity;
import com.example.settlement.settle.model.valueobj.SettleBillInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 10:46
 */
@Mapper
// @Master
// @ShardingRule(shardingColumn = "settle_id", shardingAlgorithm = "settleIdShardingAlgorithm")
public interface SettleBillMapper {

    List<SettleBillInfo> selectAllSettleInfo(Long userId, List<Integer> statusList);

    int insertSelective(SettleBillEntity entity);
}
