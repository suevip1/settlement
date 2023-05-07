package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/7 14:44
 */
@Getter
@AllArgsConstructor
public class SettleUnSettledNet implements ExpectedEvent {
    private String settleId;
    private Long userId;
    private int status;
    private Long totalUnsettledNet;
    private Long totalSettledNet;
    private int withdrawStatus;
    private int version;

}
