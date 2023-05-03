package com.example.settlement.settle.model.domain;

import com.alibaba.fastjson.JSONObject;
import com.example.settlement.clear.infra.enums.SummaryStateEnum;
import com.example.settlement.common.enums.FundDirectEnum;
import com.example.settlement.common.enums.UserTradeTypeEnum;
import com.example.settlement.common.event.ErrorNoUnexpectedEvent;
import com.example.settlement.common.event.ExpectedEvent;
import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.settle.infra.SettleErrorNo;
import com.example.settlement.settle.infra.db.entity.SettleBillEntity;
import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import com.example.settlement.settle.infra.enums.SettleStatusEnum;
import com.example.settlement.settle.model.event.SettleBillBound;
import com.example.settlement.settle.model.event.SettleBindStarted;
import com.example.settlement.settle.model.event.SettleClearCompleted;
import com.example.settlement.settle.model.event.SettleRiskManaged;
import com.example.settlement.settle.model.valueobj.DetailInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 结算绑定模型，绑定详情单（将结算id写到详情单中）
 * @author yangwu_i
 * @date 2023/5/3 12:10
 */
@Slf4j
@Getter
public class BillModel {
    private SettleBillEntity entity;
    private List<DetailInfo> details; // 已绑定的汇总详情单

    public BillModel(SettleBillEntity entity, List<DetailInfo> details) {
        this.entity = entity;
        this.details = details == null ? List.of() : details;
    }

    public Pair<SettleBindStarted, ? extends UnexpectedEvent> startBindDetails(List<DetailInfo> detailInfoList) {
        if (entity.getSettleStatus() != SettleStatusEnum.INIT.getValue() &&
            entity.getSettleStatus() != SettleStatusEnum.BINDING.getValue()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.SETTLE_STATUS_ERROR, entity.getSettleId() + ", " + entity.getSettleStatus()));
        }
        if (CollectionUtils.isEmpty(detailInfoList)) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.DETAIL_INFO_EMPTY, entity.getSettleId() + ", " + detailInfoList.size()));
        }
        if (detailInfoList.stream().anyMatch(detail -> detail.getState() == SummaryStateEnum.CLEARING.getValue())) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.DETAIL_INFO_STATE_ERROR, entity.getSettleId() + ", " + JSONObject.toJSONString(detailInfoList.stream().filter(detail -> detail.getState() == SummaryStateEnum.CLEARING.getValue()))));
        }

        SettleBindStarted settleBindStarted = new SettleBindStarted(entity.getUserId(), entity.getUserType(), entity.getSettleId(), detailInfoList, entity.getVersion());
        return Pair.of(settleBindStarted, null);
    }

    public void refresh(ExpectedEvent event) {
        if (event instanceof SettleBindStarted) {
            handle((SettleBindStarted) event);
            return ;
        }
        if (event instanceof SettleRiskManaged) {
            handle((SettleRiskManaged) event);
            return ;
        }
        if (event instanceof SettleBillBound) {
            handle((SettleBillBound) event);
            return ;
        }
        if (event instanceof SettleClearCompleted) {
            handle((SettleClearCompleted) event);
            return ;
        }
    }

    // 风控拦截 风控拦截交易继续累计不必拿
    private void handle(SettleRiskManaged event) {
        entity.setSettleStatus(SettleStatusEnum.RISKMANAGEMENT.getValue());
        entity.setVersion(event.getVersion() + 1);
    }

    private void handle(SettleBillBound event) {
        entity.setSettleStatus(SettleStatusEnum.CLEARING.getValue());
        entity.setVersion(event.getVersion() + 1);
        details.forEach(e -> e.setState(SummaryStateEnum.CLEARING.getValue()));
    }

    private void handle(SettleClearCompleted event) {
        entity.setSettleStatus(SettleStatusEnum.WAITING_SETTLE_TRADE_FEE.getValue());
        entity.setTotalAmount(event.getTotalTradeAmount());
        entity.setTotalCount(event.getTotalTradeCount());
        entity.setTotalNetAmount(event.getTotalTradeNetAmount());
        entity.setTotalNetCount(event.getTotalTradeNetCount());
        entity.setTotalFeeAmount(event.getTotalFeeAmount());
        entity.setTotalFeeCount(event.getTotalFeeCount());
        entity.setTotalProcessedTradeFeeAmount(event.getTotalProcessedTradeFeeAmount());
        entity.setTotalUnProcessedTradeFeeAmount(event.getTotalUnProcessedTradeFeeAmount());
        entity.setTotalProcessedInstallmentFeeAmount(event.getTotalProcessedInstallmentFeeAmount());
        entity.setTotalUnProcessedInstallmentFeeAmount(event.getTotalUnProcessedInstallmentFeeAmount());
        entity.setTotalProcessedTaxFeeAmount(event.getTotalProcessedTaxFeeAmount());
        entity.setTotalUnProcessedTaxFeeAmount(event.getTotalUnProcessedTaxFeeAmount());
        entity.setTotalSettledNetAmount(event.getTotalSettledNetAmount());
        entity.setTotalUnSettledNetAmount(event.getTotalUnSettledNetAmount());
        entity.setVersion(event.getVersion() + 1);
    }

    private void handle(SettleBindStarted event) {
        entity.setSettleStatus(SettleStatusEnum.BINDING.getValue());
        entity.setVersion(event.version() + 1);
        details = event.detailInfos().stream().peek(e -> e.setState(SummaryStateEnum.BINDING.getValue())).toList();
    }

    public boolean isInit() {
       return entity.getSettleStatus() == SettleStatusEnum.INIT.getValue();
    }

    /**
     * 将详情单各个费项累加到结算单上
     * @param details
     * @return
     */
    public Pair<SettleClearCompleted, UnexpectedEvent> completeClear(List<SettleDetailEntity> details) {
        if (details == null) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.COMPLETE_CLEAR_DETAILS_IS_NULL, entity.getSettleId() + ", " + entity.getSettleStatus()));
        }
        if (!isClearing() && !isInit()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.COMPLETE_CLEAR_SETTLE_STATUS_ERROR, entity.getSettleId() + ", " + entity.getSettleStatus()));
        }
        if (details.size() != this.details.size()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.COMPLETE_CLEAR_DETAILS_SIZE_ERROR, entity.getSettleId() + ", " + details.size() + ", " + this.details.size()));
        }

        List<String> retainedDetailIds = details.stream().map(SettleDetailEntity::getDetailId).collect(Collectors.toList());
        retainedDetailIds.retainAll(this.details.stream().map(DetailInfo::getDetailId).collect(Collectors.toList()));
        if (retainedDetailIds.size() != this.details.size()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.COMPLETE_CLEAR_DETAILS_SIZE_ERROR, entity.getSettleId() + ", " + details.size() + ", " + this.details.size()));
        }

        SettleClearCompleted event = new SettleClearCompleted();
        addAndSetAllFeeAmount(details, event);

        event.setUserId(entity.getUserId());
        event.setConfigId(entity.getConfigId());
        event.setSettleId(entity.getSettleId());
        event.setDetailIds(this.details.stream().map(DetailInfo::getDetailId).collect(Collectors.toList()));
        event.setSettleTime(entity.getSettleTime());
        event.setLiquidEntTime(entity.getLiquidEndTime());
        event.setDetailCount(details.size());
        event.setCurrency(entity.getCurrency());
        event.setVersion(entity.getVersion());
        return Pair.of(event, null);
    }

    private void addAndSetAllFeeAmount(List<SettleDetailEntity> details, SettleClearCompleted event) {
        long totalProcessedTradeFee = details.stream().mapToLong(SettleDetailEntity::getTotalProcessedTradeFee).sum();
        long totalUnProcessedTradeFee = details.stream().mapToLong(SettleDetailEntity::getTotalUnProcessedTradeFee).sum();

        long totalProcessedInstallmentFee = details.stream().mapToLong(SettleDetailEntity::getTotalProcessedInstallmentFee).sum();
        long totalUnProcessedInstallmentFee = details.stream().mapToLong(SettleDetailEntity::getTotalProcessedInstallmentFee).sum();

        long totalProcessedTaxFee = details.stream().mapToLong(SettleDetailEntity::getTotalProcessedTaxFee).sum();
        long totalUnProcessedTaxFee = details.stream().mapToLong(SettleDetailEntity::getTotalUnProcessedTaxFee).sum();

        long totalSettledNet = details.stream().mapToLong(SettleDetailEntity::getTotalSettledNetAmount).sum();
        long totalUnSettledNet = details.stream().mapToLong(SettleDetailEntity::getTotalUnSettledNetAmount).sum();

        event.setTotalProcessedTradeFeeAmount(totalProcessedTradeFee);
        event.setTotalUnProcessedTradeFeeAmount(totalUnProcessedTradeFee);

        event.setTotalProcessedInstallmentFeeAmount(totalProcessedInstallmentFee);
        event.setTotalUnProcessedInstallmentFeeAmount(totalUnProcessedInstallmentFee);

        event.setTotalProcessedTaxFeeAmount(totalProcessedTaxFee);
        event.setTotalUnProcessedTaxFeeAmount(totalUnProcessedTaxFee);

        event.setTotalSettledNetAmount(totalSettledNet);
        event.setTotalUnSettledNetAmount(totalUnSettledNet);

        long totalAmount =  details.stream().mapToLong(detail -> {
                UserTradeTypeEnum type = UserTradeTypeEnum.valueOf(detail.getUserTradeType());
                if (type == null) {
                    throw new ErrorNoException(SettleErrorNo.USER_TRADE_TYPE_NOT_EXISTS, detail.getUserTradeType().toString());
                }
                if (type.getDirect() == FundDirectEnum.DIRECT_PAYMENT) {
                    return detail.getTotalAmount();
                } else {
                    return -detail.getTotalAmount();
            }}).sum();
        long totalCount = details.stream().mapToLong(SettleDetailEntity::getTotalCount).sum();
        event.setTotalTradeAmount(totalAmount);
        event.setTotalTradeCount(totalCount);
        event.setTotalFeeCount(totalCount);
        event.setTotalTradeNetCount(totalCount);
    }

    private boolean isClearing() {
       return entity.getSettleStatus() == SettleStatusEnum.CLEARING.getValue();
    }

    public boolean isBinding() {
       return entity.getSettleStatus() == SettleStatusEnum.BINDING.getValue();
    }

    // 能否进入清算环节（（init/binding/risk_manage) -> clear -> wait_settle）流程
    // 3 小时延迟，防止交易信息堆积
    public boolean canRegularlyClearing() {
        return Instant.now().isAfter(entity.getLiquidEndTime().toInstant().plus(3, ChronoUnit.HOURS));
    }

    // 是否包含风险汇总单
    public boolean isContainRiskDetail() {
        return details.stream().anyMatch(e -> e.getTransType() == UserTradeTypeEnum.PAY.getValue());
    }

    public Pair<SettleRiskManaged, UnexpectedEvent> riskManage() {
        if (!isBinding()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.RISK_MANAGE_SETTLE_STATUS_ERROR, entity.getSettleId() + ", " + entity.getSettleStatus()));
        }
        return Pair.of(new SettleRiskManaged(entity.getUserId(), entity.getSettleId(), details, entity.getVersion()), null);
    }

    // 锁定 不可再增加绑定关系
    public Pair<SettleBillBound, UnexpectedEvent> clear() {
         if (!isBinding()) {
             return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.SETTLE_STATUS_ERROR, entity.getSettleId() + ", " + entity.getSettleStatus()));
         }
         return Pair.of(new SettleBillBound(entity.getUserId(), entity.getSettleId(), details.stream().map(DetailInfo::getDetailId).toList(), entity.getVersion()), null);
    }

    public boolean isRisk() {
        return entity.getSettleStatus() == SettleStatusEnum.RISKMANAGEMENT.getValue();
    }

    // 风险拦截，是否开始清算流程
    public Pair<SettleBillBound, UnexpectedEvent> riskBeforeClear() {
        if (!isRisk()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.RISK_MANAGE_SETTLE_STATUS_ERROR, entity.getSettleId() + ", " + entity.getSettleStatus()));
        }
        List<DetailInfo> payDetails = details.stream().filter(e -> e.getTransType() == UserTradeTypeEnum.PAY.getValue()).toList();
        List<DetailInfo> riskDetails = details.stream().filter(e -> e.getTransType() == UserTradeTypeEnum.RISK_DEDUCT.getValue()).toList();
        boolean isRiskManagement = false;
        long riskCount = riskDetails.stream().mapToLong(DetailInfo::getRiskCount).sum();
        long payCount = payDetails.stream().mapToLong(DetailInfo::getTotalTradeCount).sum();
        // 继续等待，直到支付单全部过风控
        if (riskCount < payCount) {
            isRiskManagement = true;
        }
        boolean isRiskOverTime = riskOverTime();
        if (!isRiskOverTime && isRiskManagement) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(SettleErrorNo.RISK_MANAGE_WAIT, entity.getSettleId() + ", " + entity.getSettleStatus()));
        }
        return Pair.of(new SettleBillBound(entity.getUserId(), entity.getSettleId(), details.stream().map(DetailInfo::getDetailId).toList(), entity.getVersion()), null);
    }

    // 风控拦截是否超时 13 小时
    private boolean riskOverTime() {
        return Instant.now().isAfter(entity.getLiquidEndTime().toInstant().plus(13, ChronoUnit.HOURS));
    }
}
