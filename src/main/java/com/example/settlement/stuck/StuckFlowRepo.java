package com.example.settlement.stuck;

import com.example.settlement.common.event.ExpectedEvent;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.stuck.infra.db.mapper.StuckFlowMapper;
import com.example.settlement.stuck.infra.db.entity.StuckFlowEntity;
import com.example.settlement.stuck.infra.event.*;
import com.example.settlement.stuck.model.StuckFlowModel;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 11:04
 */
public class StuckFlowRepo {
    @Resource
    StuckFlowMapper stuckFlowMapper;

    public StuckFlowModel findStuckModel(Integer stuckType, String tradeId, Integer tradeType) {
        StuckFlowEntity bill = stuckFlowMapper.selectByStuckTypeAndTradeIdAndTradeType(stuckType, tradeId, tradeType);
        if (bill == null) {
            return null;
        }
        return new StuckFlowModel(bill);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void handle(ExpectedEvent event) {
        int result = -1;
        if (event instanceof FlowStuckCreated) {
            result = doHandle((FlowStuckCreated) event);
        } else if (event instanceof FlowRetried) {
            result = doHandle((FlowRetried) event);
        } else if (event instanceof FlowDataModified) {
            result = doHandle((FlowDataModified) event);
        } else if (event instanceof FlowManuallyIgnored) {
            result = doHandle((FlowManuallyIgnored) event);
        } else if (event instanceof FlowRetryTriggered) {
            result = doHandle((FlowRetryTriggered) event);
        }
        if (result == -1) {
            throw new ErrorNoException(ErrorNo.SERVER_ERROR, "Unsupported Event!");
        }
        if (result != 1) {
            throw new ErrorNoException(ErrorNo.DB_UPDATE_ERROR, "");
        }
    }

    private int doHandle(FlowRetryTriggered event) {
        StuckFlowEntity updated = new StuckFlowEntity();
        updated.setTradeId(event.getTradeId());
        updated.setTradeType(event.getTradeType());
        updated.setRetryTimes(event.getRetryTimes());
        updated.setStatus(event.getStatus());
        return stuckFlowMapper.updateByTradeIdAndTradeType(updated);
    }

    private int doHandle(FlowManuallyIgnored event) {
        StuckFlowEntity updated = new StuckFlowEntity();
        updated.setTradeId(event.getTradeId());
        updated.setTradeType(event.getTradeType());
        updated.setHandler(event.getHandler());
        updated.setHandlerMsg(event.getHandleMessage());
        updated.setStatus(event.getStatus());
        return stuckFlowMapper.updateByTradeIdAndTradeType(updated);
    }

    private int doHandle(FlowDataModified event) {
        StuckFlowEntity updated = new StuckFlowEntity();
        updated.setTradeId(event.getTradeId());
        updated.setTradeType(event.getTradeType());
        updated.setHandler(event.getHandler());
        updated.setHandleContext(event.getHandleContext());
        updated.setHandlerMsg(event.getHandleMessage());
        updated.setStatus(event.getStatus());
        return stuckFlowMapper.updateByTradeIdAndTradeType(updated);
    }

    private int doHandle(FlowStuckCreated event) {
        StuckFlowEntity entity = new StuckFlowEntity();
        BeanUtils.copyProperties(event, entity);
        entity.setLastCode(event.getErrorNo());
        entity.setLastMsg(event.getErrorMsg());
        return stuckFlowMapper.insertSelective(entity);
    }

    private int doHandle(FlowRetried event) {
        StuckFlowEntity updated = StuckFlowEntity.builder()
                .tradeId(event.getTradeId())
                .tradeType(event.getTradeType())
                .status(event.getStatus())
                .retryTimes(event.getRetryTimes())
                .lastCode(event.getErrorNo())
                .lastMsg(event.getErrorMsg()).build();
        return stuckFlowMapper.updateByTradeIdAndTradeType(updated);
    }
}
