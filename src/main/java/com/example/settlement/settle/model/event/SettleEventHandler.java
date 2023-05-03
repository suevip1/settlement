package com.example.settlement.settle.model.event;

import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.settle.infra.db.entity.SettleBillEntity;
import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import com.example.settlement.settle.infra.db.mapper.SettleBillMapper;
import com.example.settlement.settle.infra.db.mapper.SettleDetailMapper;
import com.example.settlement.settle.infra.enums.SettleStatusEnum;
import com.example.settlement.settle.model.valueobj.DetailInfo;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 15:21
 */
@Service
public class SettleEventHandler {
    @Resource
    private SettleDetailMapper detailMapper;
    @Resource
    private SettleBillMapper billMapper;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void process(SummaryStarted summaryStarted) {
        SettleDetailEntity entity = new SettleDetailEntity();
        BeanUtils.copyProperties(summaryStarted, entity);
        entity.setState(SettleStatusEnum.BINDING.getValue());
        Assert.isTrue(detailMapper.insertSelective(entity) == 1, ErrorNo.DB_INSERT_ERROR.toString());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void process(SettleBillInited event) {
        SettleBillEntity entity = SettleBillEntity.builder()
                .countryCode(event.getCountryCode())
                .userId(event.getUserId())
                .userType(event.getUserType())
                .configId(event.getConfigId())
                .settleId(event.getSettleId())
                .settleMode(event.getSettleMode())
                .settleType(event.getSettleType())
                .settleCycle(event.getSettleCycle())
                .settleTime(event.getSettleTime())
                .settleStartTime(event.getSettleStartTime())
                .settleEndTime(event.getSettleEndTime())
                .liquidStartTime(event.getLiquidStartTime())
                .liquidEndTime(event.getLiquidEndTime())
                .settleStatus(SettleStatusEnum.INIT.getValue())
                .currency(event.getCurrency())
                .remark(event.getRemark())
                .version(0).build();
        Assert.isTrue(billMapper.insertSelective(entity) == 1, ErrorNo.DB_INSERT_ERROR.toString());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void process(SettleBindStarted bindStarted) {
        SettleBillEntity billUpdate = SettleBillEntity.builder()
                .settleStatus(SettleStatusEnum.BINDING.getValue())
                .version(bindStarted.version() + 1)
                .build();
        Assert.isTrue(billMapper.updateSelective(billUpdate, bindStarted.userId(),
                bindStarted.settleId(), bindStarted.version()) == 1, ErrorNo.DB_UPDATE_ERROR.toString());

        List<String> detailIds = bindStarted.detailInfos().stream().map(DetailInfo::getDetailId).toList();
        if (CollectionUtils.isNotEmpty(detailIds)) {
            Assert.isTrue(detailMapper.updateSettleId(bindStarted.userId(),
                            bindStarted.settleId(), detailIds) == detailIds.size(), ErrorNo.DB_UPDATE_ERROR.toString() + bindStarted);
        }

    }
}
