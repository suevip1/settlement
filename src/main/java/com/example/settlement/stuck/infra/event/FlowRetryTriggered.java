package com.example.settlement.stuck.infra.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author yangwu_i
 * @date 2023/5/11 23:49
 */
@Data
@AllArgsConstructor
public class FlowRetryTriggered implements ExpectedEvent {
    private String tradeId;
    private Integer tradeType;
    private Integer retryTimes;
    private Integer status;
    private Integer version;
}
