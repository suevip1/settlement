package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/4 22:18
 */
@Getter
@AllArgsConstructor
public class SettleUnSettledTradeFee implements ExpectedEvent {
    private String settleId;
    private Long userId;
    private int status;
    private Long totalProcessedTradeFee;
    private Long totalUnProcessedTradeFee;
    private int version;
}
