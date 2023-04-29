package com.example.settlement.stuck;

import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.stuck.infra.db.mapper.StuckFlowMapper;
import com.example.settlement.stuck.infra.db.entity.StuckFlowEntity;
import com.example.settlement.stuck.infra.event.FlowStuckCreated;
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
    public void handle(FlowStuckCreated event) {
        int result = -1;
        if (event instanceof FlowStuckCreated) {
            result = doHandle((FlowStuckCreated) event);
        }
        if (result == -1) {
            throw new ErrorNoException(ErrorNo.SERVER_ERROR, "Unsupported Event!");
        }
        if (result != 1) {
            throw new ErrorNoException(ErrorNo.DB_UPDATE_ERROR, "");
        }
    }

    private int doHandle(FlowStuckCreated event) {
        StuckFlowEntity entity = new StuckFlowEntity();
        BeanUtils.copyProperties(event, entity);
        entity.setLastCode(event.getErrorNo());
        entity.setLastMsg(event.getErrorMsg());
        return stuckFlowMapper.insertSelective(entity);
    }
}
