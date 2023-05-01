package com.example.settlement.settle.model.event;

import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.settle.infra.db.entity.SettleBillEntity;
import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import com.example.settlement.settle.infra.db.mapper.SettleDetailMapper;
import com.example.settlement.settle.infra.enums.SettleStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 15:21
 */
@Service
public class SettleEventHandler {
    @Resource
    private SettleDetailMapper detailMapper;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void process(SummaryStarted summaryStarted) {
        SettleDetailEntity entity = new SettleDetailEntity();
        BeanUtils.copyProperties(summaryStarted, entity);
        entity.setState(SettleStatusEnum.ACCUMULATING.getValue());
        Assert.isTrue(detailMapper.insertSelective(entity) == 1, ErrorNo.DB_INSERT_ERROR.toString());
    }
}
