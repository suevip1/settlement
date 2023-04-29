package com.example.settlement.clear.model.calculate.impl;

import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.enums.ClearStatusEnum;
import com.example.settlement.clear.infra.errorno.ClearingErrorNo;
import com.example.settlement.clear.model.ClearResult;
import com.example.settlement.clear.model.calculate.AbstractClearCalculateService;
import org.springframework.stereotype.Service;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 16:05
 */
@Service
public class PayClearCalculateService extends AbstractClearCalculateService<ClearingBillEntity> {
    @Override
    protected ClearResult doCalculate(ClearingBillEntity entity) {
        return null;
    }

    @Override
    protected ClearResult preCheck(ClearingBillEntity entity) {
        // 初始状态才能清分
        if (entity.getStatus() == ClearStatusEnum.INIT.getStatus()) {
            return ClearResult.buildSuccessResult();
        } else {
            return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_STATUS_ERROR);
        }
    }
}
