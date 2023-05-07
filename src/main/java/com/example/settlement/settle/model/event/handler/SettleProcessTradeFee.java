package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.settle.model.domain.BillModel;
import com.example.settlement.settle.model.domain.BillSettleModel;
import com.example.settlement.settle.model.event.SettleUnSettledTradeFee;
import com.example.settlement.settle.model.event.handler.IHandleable;
import com.example.settlement.settle.model.event.handler.SettleEventHandler;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author yangwu_i
 * @date 2023/5/4 22:07
 */
@Setter
public class SettleProcessTradeFee extends AbstractHandler<BillModel> {
    @Resource
    private SettleEventHandler repo;
    @Override
    public UnexpectedEvent handle(BillModel model) {
        if (model.isWaitSettleUnSettledTradeFee()) {
            Pair<SettleUnSettledTradeFee, UnexpectedEvent> result = model.transferUnSettledTradeFee();
            if (result.getRight() != null) {
                return result.getRight();
            }
            SettleUnSettledTradeFee event = result.getLeft();
            repo.process(event);
            model.refresh(event);
        }
        return next.handle(model);
    }
}
