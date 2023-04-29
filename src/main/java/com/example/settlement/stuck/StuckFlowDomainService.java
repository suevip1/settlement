package com.example.settlement.stuck;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.common.utils.db.ShardingUtil;
import com.example.settlement.stuck.infra.enums.StuckFlowStateEnum;
import com.example.settlement.stuck.infra.enums.StuckTypeEnum;
import com.example.settlement.stuck.infra.event.FlowStuckCreated;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.tuple.Pair;
import java.rmi.UnexpectedException;
import java.time.Instant;


/**
 *
 * @author yangwu_i
 * @date 2023/4/29 12:28
 */
public class StuckFlowDomainService {
    @Resource
    private StuckFlowRepo stuckFlowRepo;


    /**
     * 交易清算 stuck，需要进入异常管理
     * @param userId
     * @param tradeId
     * @param tradeType
     * @param errorNo
     * @param errorMsg
     * @return
     */
    public Pair<FlowStuckCreated, UnexpectedEvent> tradeClearingStucked(Long userId, String tradeId, Integer tradeType, int errorNo, String errorMsg) {
        FlowStuckCreated.FlowStuckCreatedBuilder builder = FlowStuckCreated.builder();
        builder.userId(userId)
                .stuckType(StuckTypeEnum.Clearing.getCode())
                .tradeId(tradeId)
                .tradeType(tradeType)
                .retryCount(0)
                .status(StuckFlowStateEnum.AutoRetring.getCode())
                .errorNo(errorNo)
                .errorMsg(errorMsg)
                .tableIndex(ShardingUtil.shardingTableBy1000("clearing_bill", tradeId))
                .tradeFinishTime(Instant.now().toEpochMilli())
                .remark(tradeId);
        return Pair.of(builder.build(), null);
    }
}
