package com.example.settlement.stuck.model;

import com.example.settlement.stuck.infra.db.entity.StuckFlowEntity;
import lombok.AllArgsConstructor;

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



}
