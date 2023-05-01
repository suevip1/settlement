package com.example.settlement.clear.model.calculate.impl;

import com.example.settlement.charge.domain.ChargeCalculatorService;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.db.mapper.IClearingBillMapper;
import com.example.settlement.clear.infra.enums.ClearStatusEnum;
import com.example.settlement.clear.infra.errorno.ClearingErrorNo;
import com.example.settlement.clear.model.ClearResult;
import com.example.settlement.clear.model.calculate.AbstractClearCalculateService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 16:08
 */
@Service
public class RefundClearCalculateService extends AbstractClearCalculateService<ClearingBillEntity> {
    @Resource
    private IClearingBillMapper clearBillMapper;
    @Resource
    private ChargeCalculatorService calculatorService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    protected ClearResult doCalculate(ClearingBillEntity entity) {
        // 查原支付订单
        ClearingBillEntity originBill = clearBillMapper.selectByTradeIdAndTradeType(entity.getTradeId(), entity.getTradeType());
        if (originBill == null) {
            return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_NOT_FOUND_ORIGIN_BILL);
        }
        ClearResult clearResult = calculatorService.calculateRefundFee(entity);
        if (!clearResult.isSuccess()) {
            return clearResult;
        }
        entity.setStatus(ClearStatusEnum.CLEAR.getStatus());
        entity.setModifyTime(new Date());
        Assert.isTrue(clearBillMapper.updateFee(entity) == 1, ClearingErrorNo.CLEAR_PROCESS_PAYIN_CALC_DB_UPDATE_FAIL.toString());
        return ClearResult.buildSuccessResult();
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
