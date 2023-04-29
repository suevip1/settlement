package com.example.settlement.clear.model;

import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.db.mapper.IClearingBillMapper;
import com.example.settlement.clear.infra.enums.ClearStatusEnum;
import com.example.settlement.clear.infra.errorno.ClearingErrorNo;
import com.example.settlement.clear.infra.errorno.SettlementErrorNo;
import com.example.settlement.clear.infra.mq.CallbackNotifyService;
import com.example.settlement.clear.model.calculate.AbstractClearCalculateService;
import com.example.settlement.clear.model.calculate.IClearCalculateService;
import com.example.settlement.clear.model.calculate.impl.PayClearCalculateService;
import com.example.settlement.clear.model.calculate.impl.RefundClearCalculateService;
import com.example.settlement.common.constant.CommonConstant;
import com.example.settlement.common.enums.SettleModeEnum;
import com.example.settlement.common.enums.UserTradeTypeEnum;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.stuck.ExecResult;
import com.example.settlement.stuck.StuckFlowAppService;
import groovyjarjarantlr4.runtime.tree.CommonErrorNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Objects;

/**
 * 清分服务
 * @author yangwu_i
 * @date 2023/4/26 21:02
 */
@Slf4j
@Service
public class ClearingService {

    @Resource
    private IClearingBillMapper clearBillMapper;
    @Resource
    private CallbackNotifyService callbackNotifyService;
    @Resource
    private StuckFlowAppService stuckFlowService;
    @Resource
    private PayClearCalculateService payClearCalculateService;

    @Resource
    private RefundClearCalculateService refundClearCalculateService;

    public boolean handle(ClearingBillEntity entity) {
        try {
            // 清分数据落库
            ClearingBillEntity existEntity = clearBillMapper.selectByTradeIdAndTradeType(entity.getTradeId(), entity.getTradeType());
            SettleModeEnum settleModeEnum = entity.getSettleMode() != null ? SettleModeEnum.getByValue(entity.getSettleMode()) : null;
            if (settleModeEnum == null) {
                throw new ErrorNoException(SettlementErrorNo.ASSIGNED_SETTLEMENT_CONFIG_NOT_EXISTS, "");
            }
            if (existEntity == null) {
                Assert.isTrue(clearBillMapper.insertSelective(entity) == 1, ClearingErrorNo.CLEAR_PROCESS_INSERT_ENTITY_ERROR.getErrorMsg());
            } else {
                // 判断清分实体核心信息是否相等，不相等报错
                if (!entity.equals(existEntity)) {
                    log.error("清分实体核心信息不相等，清分实体：{}，已存在的清分实体：{}", entity, existEntity);
                    return false;
                }
                // 使用 DB 实体处理，避免因为 status 不同导致流程重放，空跑
                entity = existEntity;
            }
            return doHandler(entity);

        } catch (Exception e) {
            log.error("清分处理异常：{}，清分实体：{}，", e, entity);
        }
        return false;
    }

    private boolean doHandler(ClearingBillEntity entity) {
        ClearResult result = ClearResult.buildSuccessResult();
        try {
            // while 循环实现状态机
            while (result.isSuccess() && !entity.getStatus().equals(ClearStatusEnum.SUCCESS.getStatus())) {
                result = forward(entity);
            }
            // 实时结算结果通知
            callbackNotifyService.notifyRealTimeSettlementResult(entity);
            if (!result.isSuccess()) {
                entity.setRemark(result.toString());
            } else {
                return true;
            }
        } catch (IllegalArgumentException e) {
            log.error("清分处理参数异常：{}，清分实体：{}，", e, entity);
            entity.setRemark(e.getMessage());
        } catch (Exception e) {
            log.error("清分处理异常：{}，清分实体：{}，", e, entity);
            entity.setRemark(e.getMessage());
        }

        if (result == null || !result.isSuccess()) {
            ExecResult execResult = stuckFlowService.tradeClearingStucked(entity.getUserId(), entity.getTradeId(), entity.getTradeType(), ErrorNo.SERVER_ERROR.getErrorNo(), entity.getRemark());
            if (!execResult.isSuccess()) {
                log.error("清分处理异常，流水：{}，异常信息：{}", entity, execResult);
            }
        }
        // 更新错误信息
        entity.setModifyTime(new Date());
        clearBillMapper.updateRemark(entity);
        return false;

    }

    private ClearResult forward(ClearingBillEntity entity) {
        // 状态流转
        switch (ClearStatusEnum.valueOf(entity.getStatus())) {
            case SUCCESS:
                return ClearResult.buildSuccessResult();
            case INIT:
                // 清分计费（各种费用，手续费、分期费、税费、净额）
                return clear(entity);
            case CLEAR:
                // 交易记账
                return transTally(entity);
            case TRANS_TALLY:
                if (SettleModeEnum.REAL_TIME.getValue() == entity.getSettleMode()) {
                    // 实时结算
                    return realTimeCollectFee(entity);
                } else {
                    // 周期结算，各费项累计
                    return accumulateFeeItems(entity);
                }
            case REALTIME_SETTLE_FEE_TALLY:
                return realTimeSettle(entity);
            case REALTIME_SETTLE_NET_TALLY:
                // 各费项累计，应结净额，已结净额，应收手续费，已收手续费
                return accumulateFeeItems(entity);
            case ACCUMULATE:
            case FEE:
                // 产生分账明细
                return doSeparateAccounting(entity);
            default:
                return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_STATUS_ERROR);
        }
    }

    private ClearResult doSeparateAccounting(ClearingBillEntity entity) {
        return null;
    }

    private ClearResult realTimeSettle(ClearingBillEntity entity) {
        return null;
    }

    private ClearResult accumulateFeeItems(ClearingBillEntity entity) {
        return null;
    }

    private ClearResult realTimeCollectFee(ClearingBillEntity entity) {
        return null;
    }

    private ClearResult transTally(ClearingBillEntity entity) {
        return null;
    }

    private ClearResult clear(ClearingBillEntity entity) {
        ClearResult clearResult = null;
        if (Objects.equals(UserTradeTypeEnum.PAY.getValue(), entity.getTradeType())) {
            clearResult = payClearCalculateService.calculate(entity);
        } else if (Objects.equals(UserTradeTypeEnum.REFUND.getValue(), entity.getTradeType())) {
            clearResult = refundClearCalculateService.calculate(entity);
        }
        // if (Objects.equals(UserTradeTypeEnum.PAY.getValue(), entity.getTradeType())) {
        //     if (ObjectUtils.isEmpty(entity.getFee1Amount()) )
        // }
        return clearResult;
    }
}
