package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.settle.model.domain.BillModel;
import com.example.settlement.settle.model.event.SettleUnSettledTaxFee;
import com.example.settlement.settle.model.event.handler.IHandleable;
import com.example.settlement.settle.model.event.handler.SettleEventHandler;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author yangwu_i
 * @date 2023/5/7 14:30
 */
@Setter
public class SettleProcessTaxFee extends AbstractHandler<BillModel> {
    @Resource
    private SettleEventHandler repo;
    @Override
    public UnexpectedEvent handle(BillModel model) {
        if (model.isWaitSettleUnSettledTaxFee()) {
            Pair<SettleUnSettledTaxFee, UnexpectedEvent> result = model.transferUnSettledTaxFee();
            if (result.getRight() != null) {
                return result.getRight();
            }
            repo.process(result.getLeft());
            model.refresh(result.getLeft());
        }
        return next.handle(model);
    }
}
