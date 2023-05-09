package com.example.settlement.stuck.infra.db.mapper;

import com.example.settlement.stuck.infra.db.entity.StuckFlowEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 11:05
 */
@Mapper
// @Master
public interface StuckFlowMapper {

    StuckFlowEntity selectByStuckTypeAndTradeIdAndTradeType(Integer stuckType, String tradeId, Integer tradeType);

    int insertSelective(StuckFlowEntity entity);

    List<StuckFlowEntity> selectByStatusAndMaxTradeId(List<Integer> statuses, String currentTradeId, int batchSize);

    int updateByTradeIdAndTradeType(StuckFlowEntity updated);
}
