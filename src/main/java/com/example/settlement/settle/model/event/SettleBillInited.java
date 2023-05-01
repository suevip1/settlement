package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 结算单初始化事件
 * @author yangwu_i
 * @date 2023/5/1 23:42
 */
@Data
@Builder
public class SettleBillInited implements ExpectedEvent {
    private String countryCode;
    private Long userId;
    private Integer userType;
    private String configId; // 结算配置id
    private String settleId;
    private Integer settleMode;
    private Integer settleCycle;
    private Date settleTime; // 结算日期
    private String currency;
    private Date liquidStartTime; // 清算开始时间
    private Date liquidEndTime; // 清算结束时间
    private Date settleStartTime; // 结算开始时间
    private Date settleEndTime; // 结算结束时间
    private String remark;
}
