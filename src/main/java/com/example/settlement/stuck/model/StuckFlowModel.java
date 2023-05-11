package com.example.settlement.stuck.model;

import com.alibaba.fastjson2.JSON;
import com.example.settlement.common.event.ErrorNoUnexpectedEvent;
import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.stuck.ExecResult;
import com.example.settlement.stuck.infra.db.entity.StuckFlowEntity;
import com.example.settlement.stuck.infra.enums.StuckTypeEnum;
import com.example.settlement.stuck.infra.error.StuckErrorNo;
import com.example.settlement.stuck.infra.event.FlowDataModified;
import com.example.settlement.stuck.infra.event.FlowManuallyIgnored;
import com.example.settlement.stuck.infra.event.FlowRetried;
import com.example.settlement.stuck.infra.event.FlowRetryTriggered;
import com.example.settlement.stuck.infra.handler.ClearBillGateway;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static com.example.settlement.stuck.infra.enums.StuckFlowStateEnum.*;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 11:03
 */
public class StuckFlowModel {
    int MAX_RETRY_COUNT = 5;

    private StuckFlowEntity that;

    public StuckFlowModel(StuckFlowEntity that) {
        this.that = that;
    }


    public Pair<FlowRetried, UnexpectedEvent> retry(ClearBillGateway clearingBIllGateWay) {
        if (that.getStatus() != RetryFailed.getCode() && that.getStatus() != AutoRetring.getCode()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(StuckErrorNo.NOT_ILLEGAL_STATE, "not illegal state"));
        }

        FlowRetried retried = new FlowRetried();
        retried.setRetryTimes(that.getRetryTimes() + 1);
        retried.setTradeId(that.getTradeId());
        retried.setTradeType(that.getTradeType());
        retried.setVersion(that.getVersion());

        ExecResult result;
        switch (StuckTypeEnum.valueOf(that.getStatus())) {
            case Clearing:
                result = clearingBIllGateWay.continueClear(that.getTradeId(), that.getTradeType(), that.getTradeContext(), that.getHandleContext());
                break;
            case Settlement:
            default:
                result = ExecResult.error(StuckErrorNo.NOT_SUPPORTED_TYPE, "not supported type");
        }
        retried.setErrorNo(result.getErrorNo().getErrorNo());
        retried.setErrorMsg(result.getDebugMessage());
        if (retried.getErrorNo() == ErrorNo.SUCCESS.getErrorNo()) {
            retried.setStatus(Completed.getCode());
        } else {
            if (retried.getRetryTimes() >= MAX_RETRY_COUNT) {
                retried.setStatus(WaitManualProcess.getCode());
            } else {
                retried.setStatus(RetryFailed.getCode());
            }
        }
        return Pair.of(retried, null);
    }

    public Pair<FlowDataModified, UnexpectedEvent> modifyFlowData(String handler, String handleMessage, String handleContext) {
        if (that.getStatus() != WaitManualProcess.getCode()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(StuckErrorNo.NOT_ILLEGAL_STATE, "not illegal state"));
        }
        Map<String, Object> handleContextMap = JSON.to(Map.class, handleContext);
        if (CollectionUtils.isEmpty(handleContextMap)) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(ErrorNo.PARAM_ERROR, "handleContext参数错误"));
        }
        return Pair.of(new FlowDataModified(that.getTradeId(), that.getTradeType(), handler, JSON.toJSONString(handleContextMap), handleMessage, ManualChangeData.getCode(), that.getVersion()), null);
    }

    public Pair<FlowRetryTriggered, UnexpectedEvent> triggerRetry() {
        if (that.getStatus() != ManualChangeData.getCode()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(StuckErrorNo.NOT_ILLEGAL_STATE, "not illegal state"));
        }
        return Pair.of(new FlowRetryTriggered(that.getTradeId(), that.getTradeType(), 0, AutoRetring.getCode(), that.getVersion()), null);
    }

    public Pair<FlowManuallyIgnored, UnexpectedEvent> maskAsIgnored(String handler, String handleMessage) {
        if (that.getStatus() != WaitManualProcess.getCode()) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(StuckErrorNo.NOT_ILLEGAL_STATE, "not illegal state"));
        }
        if (StringUtils.isBlank(handler) || handler.length() > 50) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(ErrorNo.PARAM_ERROR, "handler参数长度 > 50"));
        }
        if (StringUtils.isBlank(handleMessage) || handleMessage.length() > 100) {
            return Pair.of(null, new ErrorNoUnexpectedEvent(ErrorNo.PARAM_ERROR, "handleMessage参数长度 > 100"));
        }
        return Pair.of(new FlowManuallyIgnored(that.getTradeId(), that.getTradeType(), handler, handleMessage, Completed.getCode(), that.getVersion()), null);
    }
}
