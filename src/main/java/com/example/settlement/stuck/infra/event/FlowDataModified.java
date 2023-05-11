package com.example.settlement.stuck.infra.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author yangwu_i
 * @date 2023/5/11 23:34
 */
@Data
@AllArgsConstructor
public class FlowDataModified implements ExpectedEvent {
    private String tradeId;
    private Integer tradeType;
    private String handler;
    private String handleMessage;
    private String handleContext;
    private int status;
    private int version;
}
