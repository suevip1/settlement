package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.enums.UserProductTypeEnum;
import com.example.settlement.common.event.ErrorNoUnexpectedEvent;
import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.config.entity.SettlementConfigEntity;
import com.example.settlement.config.mapper.SettleConfigMapper;
import com.example.settlement.settle.infra.SettleErrorNo;
import com.example.settlement.settle.infra.enums.SettleRiskStrategyEnum;
import com.example.settlement.settle.model.domain.BillModel;
import com.example.settlement.settle.model.event.SettleBillBound;
import com.example.settlement.settle.model.event.SettleRiskManaged;
import com.example.settlement.settle.model.event.handler.IHandleable;
import com.example.settlement.settle.model.event.handler.SettleEventHandler;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author yangwu_i
 * @date 2023/5/3 21:14
 */
@Setter
public class SettleProcessBinding extends AbstractHandler<BillModel> {
    @Resource
    private SettleConfigMapper settleConfigMapper;
    @Resource
    private SettleEventHandler repo;
    private boolean forced; // 不考虑时间，强制结算
    public SettleProcessBinding(boolean forced) {
        this.forced = forced;
    }
    @Override
    public UnexpectedEvent handle(BillModel model) {
        if (model.isBinding()) {
           // 非强制结算，并且未到结算时间
            if (!forced && !model.canRegularlyClearing()) {
                return new ErrorNoUnexpectedEvent(SettleErrorNo.NOT_TIME_TO_CLEARING, model.getEntity().getSettleId() + " notReachSettleTime");
            }
            // 若有风险交易明细汇总，需要先风险拦截
            if (needRiskManage(model)) {
                Pair<SettleRiskManaged, UnexpectedEvent> result = model.riskManage();
                if (result.getLeft() == null) {
                    return result.getRight();
                }
                repo.process(result.getLeft());
                model.refresh(result.getLeft());
            } else {
                // 没有风控拦截，直接到清算结束
                Pair<SettleBillBound, UnexpectedEvent> result = model.clear();
                if (result.getLeft() == null) {
                    return result.getRight();
                }
                repo.process(result.getLeft());
                model.refresh(result.getLeft());
            }
        }
        return next.handle(model);
    }

    private boolean needRiskManage(BillModel model) {
       // 如果 detail 中有风控拦截，走风控拦截流程
        boolean containRiskDetail = model.isContainRiskDetail();
        return !forced && needRiskManage(model.getEntity().getUserId()) && containRiskDetail;
    }

    private boolean needRiskManage(Long userId) {
        SettlementConfigEntity config = settleConfigMapper.selectConfig(userId, UserProductTypeEnum.PAY_IN.getValue());
        SettleRiskStrategyEnum settleRiskStrategy;
        if (config == null) {
            settleRiskStrategy = SettleRiskStrategyEnum.NORMAL;
        } else {
            settleRiskStrategy = SettleRiskStrategyEnum.valueOf(config.getSettleRiskStrategy());
        }

        return settleRiskStrategy != SettleRiskStrategyEnum.MERCHANT_WHITELIST;
    }


}
