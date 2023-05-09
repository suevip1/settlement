package com.example.settlement.settle.infra.job;

import com.example.settlement.stuck.ExecResult;
import com.example.settlement.stuck.StuckFlowAppService;
import com.example.settlement.stuck.infra.db.entity.StuckFlowEntity;
import com.example.settlement.stuck.infra.db.mapper.StuckFlowMapper;
import com.example.settlement.stuck.infra.enums.StuckFlowStateEnum;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/8 21:45
 */
@Slf4j
public class StuckFlowRetryJob {
    @Resource
    private StuckFlowMapper stuckFlowMapper;
    @Resource
    private StuckFlowAppService stuckFlowAppService;

    @XxlJob("stuckFlowRetryJobHandler")
    public void execute() throws Exception {
        int batchSize = 200;
        String currentTradeId = "";
        int currentSize = 0;
        do {
            List<StuckFlowEntity> stuckFlowEntities = stuckFlowMapper.selectByStatusAndMaxTradeId(List.of(StuckFlowStateEnum.AutoRetring.getCode(), StuckFlowStateEnum.RetryFailed.getCode()), currentTradeId, batchSize);
            if ((currentSize = stuckFlowEntities.size()) <= 0) {
                break;
            }
            for (StuckFlowEntity entity : stuckFlowEntities) {
                currentTradeId = entity.getTradeId();
                try {
                    ExecResult execResult = stuckFlowAppService.retry(entity.getTradeId(), entity.getTradeType());
                    if (execResult.isSuccess()) {
                        log.info("StuckFlowRetryJob success: {}, tradeId: {}, tradeType: {}", execResult, entity.getTradeId(), entity.getTradeType());
                    } else {
                        log.error("StuckFlowRetryJob failed: {}, tradeId: {}, tradeType: {}", execResult, entity.getTradeId(), entity.getTradeType());
                    }
                } catch (Exception e) {
                    log.error("StuckFlowRetryJob error: {}, tradeId: {}", e, entity.getTradeId());
                }
            }
        } while (currentSize >= batchSize);
    }
}
