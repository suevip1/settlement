package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import com.example.settlement.settle.SettleService;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/7 14:32
 */
@Getter
@AllArgsConstructor
public class SettleUnSettledTaxFee implements ExpectedEvent {
    private String settleId;
    private Long userId;
    private int status;
    private Long totalUnprocessedTaxFee;
    private Long totalProcessedTaxFee;
    private int version;
}
