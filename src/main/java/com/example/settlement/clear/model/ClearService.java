package com.example.settlement.clear.model;

import com.example.settlement.clear.infra.db.entity.ClearIndexEntity;
import com.example.settlement.clear.infra.db.entity.ClearPayoutIndexEntity;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.db.mapper.IClearIndexMapper;
import com.example.settlement.clear.infra.db.mapper.IClearPayoutIndexMapper;
import com.example.settlement.clear.infra.db.mapper.IClearingBillMapper;
import com.example.settlement.clear.infra.enums.ClearStatusEnum;
import com.example.settlement.clear.infra.enums.SummaryStateEnum;
import com.example.settlement.clear.infra.errorno.ClearingErrorNo;
import com.example.settlement.clear.infra.errorno.SettlementErrorNo;
import com.example.settlement.clear.infra.mq.CallbackNotifyService;
import com.example.settlement.clear.model.calculate.impl.PayClearCalculateService;
import com.example.settlement.clear.model.calculate.impl.RefundClearCalculateService;
import com.example.settlement.common.enums.FeeTypeEnum;
import com.example.settlement.common.enums.SettleModeEnum;
import com.example.settlement.common.enums.UserTradeTypeEnum;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.settle.SettleService;
import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import com.example.settlement.settle.infra.db.mapper.SettleDetailMapper;
import com.example.settlement.settle.model.valueobj.DetailId;
import com.example.settlement.stuck.ExecResult;
import com.example.settlement.stuck.StuckFlowAppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Objects;

/**
 * 清分服务
 * @author yangwu_i
 * @date 2023/4/26 21:02
 */
@Slf4j
@Service
public class ClearService {

    @Resource
    private IClearingBillMapper clearBillMapper;
    @Resource
    private CallbackNotifyService callbackNotifyService;
    @Resource
    private SettleService settleService;
    @Resource
    private StuckFlowAppService stuckFlowService;
    @Resource
    private PayClearCalculateService payClearCalculateService;

    @Resource
    private RefundClearCalculateService refundClearCalculateService;

    @Resource
    private IClearIndexMapper clearIndexMapper;

    @Resource
    private IClearPayoutIndexMapper clearPayoutIndexMapper;
    @Resource
    private SettleDetailMapper detailMapper;

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
                return ((ClearService) AopContext.currentProxy()).clear(entity);
            case CLEAR:
                // 交易记账
                return ((ClearService) AopContext.currentProxy()).transTally(entity);
            case TRANS_TALLY:
                if (SettleModeEnum.REAL_TIME.getValue() == entity.getSettleMode()) {
                    // 实时结算
                    return ((ClearService) AopContext.currentProxy()).realTimeCollectFee(entity);
                } else {
                    // 周期结算，各费项累计
                    return ((ClearService) AopContext.currentProxy()).accumulateFeeItems(entity);
                }
            case REALTIME_SETTLE_FEE_TALLY:
                // 实时结算，结算净额
                return ((ClearService) AopContext.currentProxy()).realTimeSettle(entity);
            case REALTIME_SETTLE_NET_TALLY:
                // 各费项累计，应结净额，已结净额，应收手续费，已收手续费
                return ((ClearService) AopContext.currentProxy()).accumulateFeeItems(entity);
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

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    protected ClearResult realTimeSettle(ClearingBillEntity entity) {
        if (entity.getStatus() == ClearStatusEnum.REALTIME_SETTLE_FEE_TALLY.getStatus()) {
            // mock 实时结算结算净额 rpc
            if (entity.getNetAmount() != null && entity.getNetAmount() != 0) {
                log.info("实时结算结算净额成功");
            }
            entity.setStatus(ClearStatusEnum.REALTIME_SETTLE_NET_TALLY.getStatus());
            entity.setModifyTime(new Date());
            Assert.isTrue(clearBillMapper.updateStatus(entity, entity.getTradeId(), ClearStatusEnum.REALTIME_SETTLE_FEE_TALLY.getStatus()) == 1, ClearingErrorNo.CLEAR_PROCESS_UPDATE_STATUS_ERROR.toString());
            return ClearResult.buildSuccessResult();
        } else {
            return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_STATUS_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    protected ClearResult accumulateFeeItems(ClearingBillEntity entity) {
        if (entity.getStatus() == ClearStatusEnum.TRANS_TALLY.getStatus() ||
                entity.getStatus() == ClearStatusEnum.REALTIME_SETTLE_NET_TALLY.getStatus()) {
            // 获取结算单号 （同类型多笔交易汇总到一个单子）
            DetailId detailId = settleService.createDetailId(entity.getUserId(), entity.getProductType(), entity.getTradeType(), entity.getTradeFinishTime());
            if (detailId == null || detailId.detailId() == null) {
                log.error("获取汇总单号失败，清分实体：{}", entity);
                return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_GET_DETAIL_ID_ERROR);
            }
            // 查清分索引表
            ClearIndexEntity idx = ClearIndexEntity.builder().
                    tradeId(entity.getTradeId()).
                    tradeType(entity.getTradeType()).
                    build();
            if (CollectionUtils.isEmpty(clearIndexMapper.selectBy(idx.getTradeId(), idx.getTradeType()))) {
                ((ClearService) AopContext.currentProxy()).accumulate(entity, detailId);
            }
            return null;
        } else {
            return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_STATUS_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    protected void accumulate(ClearingBillEntity entity, DetailId detailId) {
        // 累计结算明细
        SettleDetailEntity settleDetailEntity = new SettleDetailEntity();
        settleDetailEntity.setUserId(entity.getUserId());
        settleDetailEntity.setDetailId(detailId.detailId()); // matter
        settleDetailEntity.setUserTradeType(entity.getTradeType());
        settleDetailEntity.setCurrency(entity.getCurrency());
        settleDetailEntity.setTotalAmount(entity.getTradeAmount());
        settleDetailEntity.setTotalCount(1L);
        if (SettleModeEnum.REAL_TIME.getValue() == entity.getSettleMode()) {
            // 实时结算，累计已处理手续费、分期费、税费、净额
            if (entity.getTradeFeeAmount() != null && entity.getTradeFeeAmount() != 0) {
                settleDetailEntity.setTotalProcessedTradeFee(entity.getTradeFeeAmount());
            }
            if (entity.getInstallmentFeeAmount() != null && entity.getInstallmentFeeAmount() != 0) {
                settleDetailEntity.setTotalProcessedInstallmentFee(entity.getInstallmentFeeAmount());
            }
            if (entity.getTaxFeeAmount() != null && entity.getTaxFeeAmount() != 0) {
                settleDetailEntity.setTotalProcessedTaxFee(entity.getTaxFeeAmount());
            }
            if (entity.getNetAmount() != null && entity.getNetAmount() != 0) {
                settleDetailEntity.setTotalSettledNetAmount(entity.getNetAmount());
            }
        } else {
            // 周期结算，累计未处理手续费、分期费、税费、净额
            if (entity.getTradeFeeAmount() != null && entity.getTradeFeeAmount() != 0) {
                settleDetailEntity.setTotalUnProcessedTradeFee(entity.getTradeFeeAmount());
            }
            if (entity.getInstallmentFeeAmount() != null && entity.getInstallmentFeeAmount() != 0) {
                settleDetailEntity.setTotalUnProcessedInstallmentFee(entity.getInstallmentFeeAmount());
            }
            if (entity.getTaxFeeAmount() != null && entity.getTaxFeeAmount() != 0) {
                settleDetailEntity.setTotalUnProcessedTaxFee(entity.getTaxFeeAmount());
            }
            if (entity.getNetAmount() != null && entity.getNetAmount() != 0) {
                settleDetailEntity.setTotalUnSettledNetAmount(entity.getNetAmount());
            }
        }
        settleDetailEntity.setUpdateTime(new Date());
        // 累加同类型的多笔交易（支付、退款）
        Assert.isTrue(detailMapper.updateFee(settleDetailEntity, SummaryStateEnum.BINDING.getValue()) == 1, ClearingErrorNo.CLEAR_PROCESS_DB_UPDATE_FAIL.toString());
        // 存储该笔交易的各项费用索引，为后续交易重试做幂等校验
        createTradeFeeClearIndex(entity, detailId);
        createInstallmentFeeClearIndex(entity, detailId);
        createTaxFeeClearIndex(entity, detailId);
        createNetClearIndex(entity, detailId);
    }

    private void createTradeFeeClearIndex(ClearingBillEntity entity, DetailId detailId) {
        int feeType = SettleModeEnum.REAL_TIME.getValue() == entity.getSettleMode() ? FeeTypeEnum.SETTLED_NET_AMOUNT.getCode() : FeeTypeEnum.UNSETTLED_NET_AMOUNT.getCode();
        // 添加该笔交易的交易手续费索引
        insertFeeTypeIndex(entity, detailId, feeType);
        // 添加出款交易手续费索引
        insertPayoutFeeTypeIndex(entity, feeType);
    }
    private void createInstallmentFeeClearIndex(ClearingBillEntity entity, DetailId detailId) {
        int feeType = SettleModeEnum.REAL_TIME.getValue() == entity.getSettleMode() ? FeeTypeEnum.PROCESSED_INSTALLMENT_FEE.getCode() : FeeTypeEnum.UNPROCESSED_INSTALLMENT_FEE.getCode();
        // 添加该笔交易的分期费索引
        insertFeeTypeIndex(entity, detailId, feeType);
        // 添加出款分期费索引
        insertPayoutFeeTypeIndex(entity, feeType);
    }
    private void createTaxFeeClearIndex(ClearingBillEntity entity, DetailId detailId) {
        int feeType = SettleModeEnum.REAL_TIME.getValue() == entity.getSettleMode() ? FeeTypeEnum.PROCESSED_TAX_FEE.getCode() : FeeTypeEnum.UNPROCESSED_TAX_FEE.getCode();
        // 添加该笔交易的税费索引
        insertFeeTypeIndex(entity, detailId, feeType);
        // 添加出款税费索引
        insertPayoutFeeTypeIndex(entity, feeType);
    }
    private void createNetClearIndex(ClearingBillEntity entity, DetailId detailId) {
        int feeType = SettleModeEnum.REAL_TIME.getValue() == entity.getSettleMode() ? FeeTypeEnum.SETTLED_NET_AMOUNT.getCode() : FeeTypeEnum.UNSETTLED_NET_AMOUNT.getCode();
        // 添加该笔交易的净额索引
        insertFeeTypeIndex(entity, detailId, feeType);
    }
    private void insertFeeTypeIndex(ClearingBillEntity entity, DetailId detailId, int feeType) {
        Date date = new Date();
        ClearIndexEntity idx = ClearIndexEntity.builder()
                .tradeId(entity.getTradeId())
                .tradeType(entity.getTradeType())
                .feeType(feeType)
                .userId(entity.getUserId())
                .userType(entity.getUserType())
                .settleId("")
                .detailId(detailId.detailId())
                .createTime(date)
                .updateTime(date)
                .build();
        Assert.isTrue(clearIndexMapper.insert(idx) == 1, ClearingErrorNo.CLEAR_PROCESS_DB_INSERT_FAIL.toString());
    }
    private void insertPayoutFeeTypeIndex(ClearingBillEntity entity, int feeType) {
        if (UserTradeTypeEnum.REFUND.getValue() == entity.getTradeType()) {
            Date date = new Date();
            ClearPayoutIndexEntity payoutIndex = ClearPayoutIndexEntity.builder()
                    .tradeId(entity.getTradeId())
                    .originTradeId(entity.getOriginTradeId())
                    .tradeType(entity.getTradeType())
                    .feeType(feeType)
                    .originTradeType(entity.getOriginTradeType())
                    .userId(entity.getUserId())
                    .userType(entity.getUserType())
                    .amount(entity.getTradeAmount())
                    .originAmount(entity.getOriginTradeAmount())
                    .feeAmount(entity.getTradeFeeAmount())
                    .createTime(date)
                    .updateTime(date).build();
            Assert.isTrue(clearPayoutIndexMapper.insert(payoutIndex) == 1, ClearingErrorNo.CLEAR_PROCESS_DB_INSERT_FAIL.toString());
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    protected ClearResult realTimeCollectFee(ClearingBillEntity entity) {
        if (entity.getStatus() == ClearStatusEnum.TRANS_TALLY.getStatus()) {
            // mock 实时结算收手续费 rpc
            if (entity.getTradeFeeAmount() != null && entity.getTradeFeeAmount() != 0) {
                log.info("实时结算收手续费成功");
            }
            if (entity.getInstallmentFeeAmount() != null && entity.getInstallmentFeeAmount() != 0) {
                log.info("实时结算收分期费成功");
            }
            if (entity.getTradeAmount() != null && entity.getTradeAmount() != 0) {
                log.info("实时结算收税费成功");
            }
            // 更新状态
            entity.setStatus(ClearStatusEnum.REALTIME_SETTLE_FEE_TALLY.getStatus());
            entity.setModifyTime(new Date());
            Assert.isTrue(clearBillMapper.updateStatus(entity, entity.getTradeId(), entity.getStatus()) == 1, ClearingErrorNo.CLEAR_PROCESS_UPDATE_STATUS_ERROR.toString());
            return ClearResult.buildSuccessResult();
        } else {
            return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_STATUS_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    protected ClearResult transTally(ClearingBillEntity entity) {
        if (entity.getStatus() == ClearStatusEnum.CLEAR.getStatus()) {
            // mock 交易记账 rpc
            log.info("交易记账成功");
            // 更新状态
            entity.setStatus(ClearStatusEnum.TRANS_TALLY.getStatus());
            entity.setModifyTime(new Date());
            Assert.isTrue(clearBillMapper.updateStatus(entity, entity.getTradeId(), ClearStatusEnum.CLEAR.getStatus()) == 1, ClearingErrorNo.CLEAR_PROCESS_UPDATE_STATUS_ERROR.getErrorMsg());
            return ClearResult.buildSuccessResult();
        } else {
            return ClearResult.buildFailResult(ClearingErrorNo.CLEAR_PROCESS_STATUS_ERROR);
        }
    }

    private ClearResult clear(ClearingBillEntity entity) {
        ClearResult clearResult = null;
        if (Objects.equals(UserTradeTypeEnum.PAY.getValue(), entity.getTradeType())) {
            clearResult = payClearCalculateService.calculate(entity);
        } else if (Objects.equals(UserTradeTypeEnum.REFUND.getValue(), entity.getTradeType())) {
            clearResult = refundClearCalculateService.calculate(entity);
        }
        return clearResult;
    }

    public boolean handleWithoutCoverage(ClearingBillEntity bill) {
        return false;
    }
}
