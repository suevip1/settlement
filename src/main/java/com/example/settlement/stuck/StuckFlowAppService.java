package com.example.settlement.stuck;


import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.stuck.infra.error.StuckErrorNo;
import com.example.settlement.stuck.infra.event.*;
import com.example.settlement.stuck.infra.handler.ClearBillGateway;
import com.example.settlement.stuck.model.StuckFlowModel;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.UnexpectedException;

import static com.example.settlement.stuck.infra.enums.StuckTypeEnum.Clearing;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 10:42
 */
public class StuckFlowAppService {
    @Resource
    StuckFlowRepo stuckFlowRepository;
    @Resource
    private StuckFlowRepo stuckFlowRepo;
    @Resource
    private StuckFlowDomainService stuckFlowDomainService;
    @Resource
    private ClearBillGateway clearingBIllGateWay;
    public ExecResult tradeClearingStucked(Long userId, String tradeId, Integer tradeType, int errorNo, String errorMsg) {
        StuckFlowModel model = stuckFlowRepository.findStuckModel(Clearing.getCode(), tradeId, tradeType);
        if (model != null) {
            return ExecResult.success();
        }

        Pair<FlowStuckCreated, UnexpectedEvent> result = stuckFlowDomainService.tradeClearStucked(userId, tradeId, tradeType, errorNo, errorMsg);
        if (result.getLeft() != null) {
            stuckFlowRepository.handle(result.getLeft());
            return ExecResult.success();
        } else {
            return ExecResult.error(result.getRight());
        }
    }

    public ExecResult retry(String tradeId, Integer tradeType) {
        StuckFlowModel stuckModel = stuckFlowRepository.findStuckModel(Clearing.getCode(), tradeId, tradeType);
        if (stuckModel == null) {
            return ExecResult.error(StuckErrorNo.STUCK_NOT_FOUND, tradeId + " stuck not found");
        }
        Pair<FlowRetried, UnexpectedEvent> retried = stuckModel.retry(clearingBIllGateWay);
        if (retried.getLeft() != null) {
            stuckFlowRepo.handle(retried.getLeft());
            return ExecResult.success();
        } else {
            return ExecResult.error(retried.getRight());
        }
    }

    public ExecResult modifyFlowData(String tradeId, Integer tradeType, String handler, String handleMessage, String handleContext) {
        StuckFlowModel stuckModel = stuckFlowRepository.findStuckModel(Clearing.getCode(), tradeId, tradeType);
        if (stuckModel == null) {
            return ExecResult.error(StuckErrorNo.STUCK_NOT_FOUND, tradeId + " stuck not found");
        }
        Pair<FlowDataModified, UnexpectedEvent> result = stuckModel.modifyFlowData(handler, handleMessage, handleContext);
        if (result.getLeft() != null) {
            stuckFlowRepo.handle(result.getLeft());
            return ExecResult.success();
        } else {
            return ExecResult.error(result.getRight());
        }
    }

    public ExecResult triggerRetry(String tradeId, Integer tradeType) {
        StuckFlowModel stuckModel = stuckFlowRepository.findStuckModel(Clearing.getCode(), tradeId, tradeType);
        if (stuckModel == null) {
            return ExecResult.error(StuckErrorNo.STUCK_NOT_FOUND, tradeId + " stuck not found");
        }
        Pair<FlowRetryTriggered, UnexpectedEvent> result = stuckModel.triggerRetry();
        if (result.getLeft() != null) {
            stuckFlowRepo.handle(result.getLeft());
            return ExecResult.success();
        } else {
            return ExecResult.error(result.getRight());
        }
    }

    public ExecResult maskAsIgnored(String tradeId, Integer tradeType, String handler, String handleMessage) {
        StuckFlowModel stuckModel = stuckFlowRepository.findStuckModel(Clearing.getCode(), tradeId, tradeType);
        if (stuckModel == null) {
            return ExecResult.error(StuckErrorNo.STUCK_NOT_FOUND, tradeId + " stuck not found");
        }
        Pair<FlowManuallyIgnored, UnexpectedEvent> result = stuckModel.maskAsIgnored(handler, handleMessage);
        if (result.getLeft() != null) {
            stuckFlowRepo.handle(result.getLeft());
            return ExecResult.success();
        } else {
            return ExecResult.error(result.getRight());
        }
    }

    public ExecResult binlogParseStucked(long userId, String tradeId, int tradeType, String tradeContext, int errorCode, String errorMsg) {
        StuckFlowModel model = stuckFlowRepository.findStuckModel(Clearing.getCode(), tradeId, tradeType);
        if (model != null) {
            return ExecResult.success();
        }
        Pair<FlowStuckCreated, UnexpectedEvent> result = stuckFlowDomainService.binlogParseStucked(userId, tradeId, tradeType, tradeContext, errorCode, errorMsg);
        if (result.getLeft() != null) {
            stuckFlowRepository.handle(result.getLeft());
            return ExecResult.success();
        } else {
            return ExecResult.error(result.getRight());
        }
    }
}
