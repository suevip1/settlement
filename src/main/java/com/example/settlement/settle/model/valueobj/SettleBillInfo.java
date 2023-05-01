package com.example.settlement.settle.model.valueobj;

import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 10:39
 */
public record SettleBillInfo(
    String settleId,
    String configId,
    Integer settleMode,
    Integer settleCycle,
    Date settleTime,
    Date liquidStartAt, // 清算开始时间
    Date liquidEndAt, // 清算结束时间
    Integer settleStatus // 清算状态
){
}
