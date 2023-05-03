package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import com.example.settlement.settle.model.valueobj.DetailInfo;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/3 13:36
 */
public record SettleBindStarted(
        Long userId,
        Integer userType,
        String settleId,
        List<DetailInfo> detailInfos,
        Integer version
) implements ExpectedEvent {
}
