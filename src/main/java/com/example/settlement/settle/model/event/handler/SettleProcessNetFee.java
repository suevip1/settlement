package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.event.ErrorNoUnexpectedEvent;
import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.settle.SettleConfigRepo;
import com.example.settlement.settle.infra.SettleErrorNo;
import com.example.settlement.settle.infra.enums.NetSettleStrategy;
import com.example.settlement.settle.model.domain.BillModel;
import com.example.settlement.settle.model.event.SettleUnSettledNet;
import com.example.settlement.settle.model.event.handler.IHandleable;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author yangwu_i
 * @date 2023/5/7 14:39
 */
@Setter
public class SettleProcessNetFee extends AbstractHandler<BillModel> {
    @Resource
    private SettleEventHandler repo;
    @Resource
    private SettleConfigRepo configRepo;


    private boolean forced; // 不考虑时间，强制结算

    public SettleProcessNetFee(boolean forced) {
        this.forced = forced;
    }

    @Override
    public UnexpectedEvent handle(BillModel model) {
        if (model.isWaitSettleUnSettledNetFee()) {
            if (!forced && model.canRegularlyClearing()) {
                return new ErrorNoUnexpectedEvent(SettleErrorNo.NOT_TIME_TO_SETTLE, "未到结算时间");
            }
            NetSettleStrategy strategy =  configRepo.getSettleStrategy(model.getEntity().getConfigId());
            Pair<SettleUnSettledNet, UnexpectedEvent> result = model.settleUnSettledNetFee(strategy);
            if (result.getLeft() == null) {
                return result.getRight();
            }
            repo.process(result.getLeft());
            model.refresh(result.getLeft());
        }
        return next.handle(model);
    }
}
