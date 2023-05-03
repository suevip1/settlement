package com.example.settlement.settle.infra.db.mapper;

import com.example.settlement.settle.infra.db.entity.SettleBillEntity;
import com.example.settlement.settle.model.valueobj.SettleBillInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    List<String> selectAllSettleId(@Param("userId") Long userId, @Param("statuses") List<Integer> statuses);

    SettleBillEntity selectOne(Long userId, String settleId);

    int updateSelective(SettleBillEntity bill, Long userId, String settleId, Integer version);
}
