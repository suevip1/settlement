package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/4 21:18
 */
@Getter
@Builder
public class SettleBindRestart implements ExpectedEvent {
    private Long userId;
    private String settleId;
    private List<String> detailIds;
    private Integer version;
    private Date updatedSettleTime;
    private Date updatedLiquidEndTime;
}
