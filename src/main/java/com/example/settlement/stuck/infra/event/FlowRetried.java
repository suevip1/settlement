package com.example.settlement.stuck.infra.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.Data;

/**
 *
 * @author yangwu_i
 * @date 2023/5/9 21:17
 */
@Data
public class FlowRetried implements ExpectedEvent {
    private String tradeId;
    private Integer tradeType;
    private int errorNo;
    private String errorMsg;
    private int retryTimes;
    private int status;
    private int version;
}
