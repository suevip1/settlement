package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/7 13:30
 */
@Getter
@AllArgsConstructor
public class SettleUnSettledInstallmentFee implements ExpectedEvent {
    private String settleId;
    private Long userId;
    private int status;
    private Long totalUnProcessedInstallmentFee;
    private Long totalProcessedInstallmentFee;
    private int version;
}
