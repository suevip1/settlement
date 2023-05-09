package com.example.settlement.stuck.model;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.stuck.infra.db.entity.StuckFlowEntity;
import com.example.settlement.stuck.infra.event.FlowRetried;
import com.example.settlement.stuck.infra.handler.ClearBillGateway;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

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
        return null;
    }
}
