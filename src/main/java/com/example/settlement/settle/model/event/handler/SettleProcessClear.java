package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.settle.SettleConfigRepo;
import com.example.settlement.settle.SettleQueryRepo;
import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import com.example.settlement.settle.model.domain.BillModel;
import com.example.settlement.settle.model.domain.BillSettleModel;
import com.example.settlement.settle.model.event.SettleBindRestart;
import com.example.settlement.settle.model.event.SettleClearCompleted;
import com.example.settlement.settle.model.event.handler.IHandleable;
import com.example.settlement.settle.model.event.handler.SettleEventHandler;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/4 20:50
 */
@Setter
@Slf4j
public class SettleProcessClear extends AbstractHandler<BillModel> {
    @Resource
    private SettleQueryRepo repo;
    @Resource
    private SettleConfigRepo configRepo;
    @Resource
    private SettleEventHandler eventHandler;
    @Override
    public UnexpectedEvent handle(BillModel model) {
        if (model.isClearing()) {
            List<SettleDetailEntity> detailInfoList = repo.getDetailInfos(model.getEntity().getUserId(), model.getEntity().getSettleId());
            Pair<SettleClearCompleted, UnexpectedEvent> result = model.completeClear(detailInfoList);
            if (result.getRight() != null) {
                return result.getRight();
            }
            BillSettleModel settleModel = configRepo.getUserSettleModel(model.getEntity().getUserId());
            SettleBindRestart restart = settleModel.restartIfNeed(result.getLeft());
            if (restart == null) {
                eventHandler.process(result.getLeft());
                model.refresh(result.getLeft());
            } else {
                eventHandler.process(restart);
                model.refresh(restart);

                // mock 发送商户结算失败消息
                log.info("商户结算失败消息: {}", result.getLeft());
            }
        }
        return next.handle(model);
    }
}
