package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.settle.model.domain.BillModel;
import com.example.settlement.settle.model.event.SettleBillBound;
import com.example.settlement.settle.model.event.handler.IHandleable;
import com.example.settlement.settle.model.event.handler.SettleEventHandler;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 结算单风控拦截
 * @author yangwu_i
 * @date 2023/5/3 22:09
 */
@Setter
public class SettleProcessRiskManage extends AbstractHandler<BillModel> {
    @Resource
    private SettleEventHandler repo;
    @Override
    public UnexpectedEvent handle(BillModel model) {
        if (model.isRisk()) {
            Pair<SettleBillBound, UnexpectedEvent> result = model.riskBeforeClear();
            if (result.getLeft() == null) {
                return result.getRight();
            }
            repo.process(result.getLeft());
            model.refresh(result.getLeft());
        }
        return next.handle(model);
    }
}
