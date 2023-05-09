package com.example.settlement.stuck.infra.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 12:29
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FlowStuckCreated implements ExpectedEvent {
    private Long userId;
    private Integer stuckType;
    private Integer productType;
    private String tradeId;
    private Integer tradeType;
    private String tradeContext;
    private int errorNo;
    private String errorMsg;
    private int retryTimes;
    private int status;
    private String tableIndex;
    private Long tradeFinishTime;
    private String remark;
}
