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

import java.util.Date;
import java.util.List;

/**
 * 定时结算，将汇总详情单汇总到结算单
 * @author yangwu_i
 * @date 2023/5/3 15:46
 */
@Slf4j
@Service
public class SettleProcessHandler {

    @Resource
    private ConfigMaintainService configMaintainService;
    @Resource
    private SettleBillMapper settleBillMapper;
    
    @Resource
    private SettleService settleService;
    @XxlJob("settle_bill_finish")
    public void execute() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        List<Long> userIdList =  configMaintainService.selectActiveUsers();
        log.info("settle_bill_finish, shardIndex:{}, shardTotal:{}, userIdList:{}", shardIndex, shardTotal, userIdList);

        for (Long userId : userIdList) {
            if (userId % shardTotal == shardIndex) {
                List<String> settleIdList = settleBillMapper.selectExpiredSettleBillIds(userId, new Date(), SettleStatusEnum.unSettled());
                log.info("settle_bill_finish, userId:{}, settleIdList:{}", userId, settleIdList);
                
                settleIdList.forEach(settleId -> {
                    ExecutorUtils.SETTLE_BILL_TRY_FINISH.execute(() -> {
                        try {
                            Pair<Boolean, UnexpectedEvent> result = settleService.tryFinishSettleBill(userId, settleId);
                            if (result.getLeft()) {
                                log.info("settle_bill_finish succeed: userId={}, settleId={}", userId, settleId);
                            } else {
                                log.error("settle_bill_finish fail: userId={}, settleId={}, error={}", userId, settleId, result.getRight());
                            }
                        }catch (Throwable e) {
                            log.info("", e);
                            log.error("settle_bill_finish fail: userId={}, settleId={}, error={}", userId, settleId, e);
                        }
                    });
                });
            }
        }
    }
}
