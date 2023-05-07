package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.settle.model.domain.BillModel;
import com.example.settlement.settle.model.event.handler.IHandleable;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author yangwu_i
 * @date 2023/5/7 17:17
 */
@Slf4j
public class SettleProcessComplete extends AbstractHandler<BillModel> {
    @Override
    public UnexpectedEvent handle(BillModel model) {
        if (model.isSettled()) {
            // 通知触发 提现动作
            log.info("SettleProcessComplete: {}, started to withdraw!", model);
        }
        return null;
    }
}
