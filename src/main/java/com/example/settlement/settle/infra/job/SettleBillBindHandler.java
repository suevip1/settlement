package com.example.settlement.settle.infra.job;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.config.ConfigMaintainService;
import com.example.settlement.settle.SettleService;
import com.example.settlement.settle.infra.db.mapper.SettleBillMapper;
import com.example.settlement.settle.infra.enums.SettleStatusEnum;
import com.example.settlement.settle.infra.utils.ExecutorUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 将详情单绑定到汇总单
 * 每小时检查当前结算单，1.直接进行结算主流程，2.不满足结算条件，进行汇总结算单的处理
 * @author yangwu_i
 * @date 2023/5/2 12:43
 */
@Slf4j
@Service
public class SettleBillBindHandler {

    @Resource
    private ConfigMaintainService configMaintainService;
    @Resource
    private SettleBillMapper settleBillMapper;
    @Resource
    private SettleService settleService;

    @XxlJob("settle_bill_attach")
    public void execute() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        List<Long> userIds = configMaintainService.selectActiveUsers();
        log.info("settle_bill_attach: shardIndex={}, shardTotal={}, userIds={}", shardIndex, shardTotal, userIds);

        for (Long userId : userIds) {
            if (userId % shardTotal == shardIndex) {
                List<String> settleIds = settleBillMapper.selectAllSettleId(userId, SettleStatusEnum.unSettled());
                log.info("settle_bill_attach: userId={}, settleIds={}", userId, settleIds);

                settleIds.forEach( settleId -> {
                    ExecutorUtils.SETTLE_BILL_GEN.execute(() -> {
                        try {
                            Pair<Boolean, UnexpectedEvent> result = settleService.bindSummaryDetail(userId, settleId);
                            if (result.getLeft()) {
                                log.info("settle_bill_attach succeed: userId={}, settleId={}", userId, settleId);
                            } else {
                                log.error("settle_bill_attach failed: userId={}, settleId={}, error={}", userId, settleId, result.getRight());
                            }
                        } catch (Throwable e) {
                            log.info("", e);
                            log.error("settle_bill_attach fail: userId={}, settleId={}, error={}", userId, settleId, e);
                        }
                    });
                });
            }
        }
    }
}
