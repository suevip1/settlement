package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.settle.infra.job.SettleProcessHandler;
import com.example.settlement.settle.model.domain.BillModel;
import com.example.settlement.settle.model.event.SettleClearCompleted;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author yangwu_i
 * @date 2023/5/3 16:52
 */
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettleProcessInit implements IHandleable<BillModel> {

    @Resource
    private SettleEventHandler repo;
    private IHandleable<BillModel> next;
    @Override
    public UnexpectedEvent handle(BillModel model) {
        if (model.isInit()) {
            // init 状态转为 clear 结束（肯定没有单子，直接到清算结束）
            Pair<SettleClearCompleted, UnexpectedEvent> result = model.completeClear(Lists.newLinkedList());
            if (result.getLeft() == null) {
                return result.getRight();
            }
            repo.process(result.getLeft());
            model.refresh(result.getLeft());
        }
        return next.handle(model);
    }
}
