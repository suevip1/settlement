package com.example.settlement.stuck.infra.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 00:05
 */
@Data
@AllArgsConstructor
public class FlowManuallyIgnored implements ExpectedEvent {
    private String tradeId;
    private Integer tradeType;
    private String handler;
    private String handleMessage;
    private int status;
    private int version;
}
