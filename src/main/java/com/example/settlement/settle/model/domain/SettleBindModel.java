package com.example.settlement.settle.model.domain;

import com.alibaba.fastjson.JSONObject;
import com.example.settlement.clear.infra.enums.SummaryStateEnum;
import com.example.settlement.common.event.ErrorNoUnexpectedEvent;
import com.example.settlement.common.event.ExpectedEvent;
import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.settle.infra.SettleErrorNo;
import com.example.settlement.settle.infra.db.entity.SettleBillEntity;
import com.example.settlement.settle.infra.enums.SettleStatusEnum;
import com.example.settlement.settle.model.event.SettleBindStarted;
import com.example.settlement.settle.model.valueobj.DetailInfo;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 结算绑定模型，绑定详情单（将结算id写到详情单中）
 * @author yangwu_i
 * @date 2023/5/3 12:10
 */
@Slf4j
@Getter
public class SettleBindModel {
    private SettleBillEntity entity;
    private List<DetailInfo> details; // 已绑定的汇总详情单

    public SettleBindModel(SettleBillEntity entity, List<DetailInfo> details) {
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
        }
    }

    private void handle(SettleBindStarted event) {
        entity.setSettleStatus(SettleStatusEnum.BINDING.getValue());
        entity.setVersion(event.version() + 1);
        details = event.detailInfos().stream().peek(e -> e.setState(SummaryStateEnum.BINDING.getValue())).toList();
    }
}
