package com.example.settlement.settle.infra.job;

import com.example.settlement.config.ConfigMaintainService;
import com.example.settlement.config.entity.SettlementConfigEntity;
import com.example.settlement.settle.SettleService;
import com.example.settlement.settle.infra.utils.ExecutorUtils;
import com.example.settlement.settle.model.valueobj.SettleId;
import com.example.settlement.settle.model.valueobj.SettleKey;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 生成结算单定时任务：每小时检查，是否需要为商户创建结算单
 * @author yangwu_i
 * @date 2023/5/1 20:24
 */
@Slf4j
@Service
public class SettleBillGenerateHandler {

    @Resource
    private ConfigMaintainService configMaintainService;
    @Resource
    private SettleService settleService;

    @XxlJob("settle_bill_gen")
    public void execute() throws Exception {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        Date now = new Date();

        List<Long> userIds = configMaintainService.selectActiveUsers();
        log.info("settle_bill_gen: shardIndex={}, shardTotal={}, now={}, userIds={}", shardIndex, shardTotal, now, userIds);

        for (Long userId : userIds) {
            if (userId % shardTotal == shardIndex) {
                List<SettlementConfigEntity> configs = configMaintainService.getSettleConfig(userId);
                log.info("settle_bill_gen: userId={}, configs={}", userId, configs);

                configs.forEach(config -> {
                    ExecutorUtils.SETTLE_BILL_GEN.execute(() -> {
                        try {
                            SettleId id = settleService.createSettleId(config.getUserId(), config.getUserProduct(), now);
                            log.info("settle_bill_gen: userId={}, config={}, settleId={}", userId, config, id);
                        } catch (Throwable e) {
                            log.info("", e);
                            log.error("settle_bill_gen: userId={}, config={}", userId, config, e);
                        }
                    });
                });
            }
        }
    }
}
