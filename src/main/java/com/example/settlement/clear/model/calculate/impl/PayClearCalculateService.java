package com.example.settlement.clear.model.calculate.impl;

import com.example.settlement.charge.domain.ChargeCalculatorService;
import com.example.settlement.charge.domain.feecharge.FeeCalItem;
import com.example.settlement.charge.domain.feecharge.TradeInfo;
import com.example.settlement.charge.domain.feecomputer.feeitem.FeeConfig;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.db.mapper.IClearingBillMapper;
import com.example.settlement.clear.infra.enums.ClearStatusEnum;
import com.example.settlement.clear.infra.errorno.ClearingErrorNo;
import com.example.settlement.clear.model.ClearResult;
import com.example.settlement.clear.model.calculate.AbstractClearCalculateService;
import com.example.settlement.common.enums.FeeTypeEnum;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 16:05
 */
@Slf4j
@Service
public class PayClearCalculateService extends AbstractClearCalculateService<ClearingBillEntity> {

    @Resource
    private ChargeCalculatorService chargeCalculatorService;

    @Resource
    private IClearingBillMapper clearBillMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    protected ClearResult doCalculate(ClearingBillEntity entity) {
        // 组装计费参数
        TradeInfo tradeInfo = new TradeInfo(
                entity.getUserId(),
                entity.getUserType(),
                entity.getProductType(),
                entity.getTradeType(),
                entity.getPayType(),
                entity.getCountryCode(),
                entity.getCardGroup(),
                entity.getTradeFinishTime(),
                entity.getTradeAmount(),
                entity.getTradeAmount(),
                entity.getCurrency()
        );

        // 调用计费服务
        List<FeeCalItem> feeList = chargeCalculatorService.calculateChargeFee(tradeInfo);
        if (CollectionUtils.isEmpty(feeList)) {
            log.error("计费结果为空，计费参数：{}", tradeInfo);
            return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_NOT_FEE_ITEMS);
        }
        boolean hasNet = false, hasFee = false, hasTax = false;
        // 将各个费项 set 到 entity 中
        for (FeeCalItem fee : feeList) {
            FeeConfig config = fee.config();
            switch (FeeTypeEnum.valueOf(config.feeCode())) {
                case PROCESSED_TRADE_FEE:
                    entity.setTradeFeeAmount(fee.feeAmount());
                    hasFee = true;
                    continue;
                case PROCESSED_INSTALLMENT_FEE:
                    entity.setInstallmentFeeAmount(fee.feeAmount());
                    hasFee = true;
                    continue;
                case PROCESSED_TAX_FEE:
                    entity.setTaxFeeAmount(fee.feeAmount());
                    hasTax = true;
                case SETTLED_NET_AMOUNT:
                    entity.setNetAmount(fee.feeAmount());
                    hasNet = true;
                    continue;
                default:
                    throw new ErrorNoException(ErrorNo.PARAM_ERROR, "费用类型错误");
            }
        }
        if (!hasFee || !hasNet) {
            log.error("计费结果异常，缺少费项，计费结果：{}", feeList);
            return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_MISS_FEE_ITEMS);
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
