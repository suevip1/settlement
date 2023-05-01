package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 14:59
 */
@Value
public class SummaryStarted implements ExpectedEvent {
    String countryCode;
    Long userId;
    Integer userType;
    Integer userProduct;
    Integer userTradeType;
    String configId; // 结算配置id
    String detailId; // 详情单id
    Integer settleMode;
    Date summaryTime;
    int summaryCycle;
    String currency;
    String remark;
}
