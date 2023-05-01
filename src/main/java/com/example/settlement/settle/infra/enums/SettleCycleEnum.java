package com.example.settlement.settle.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.example.settlement.settle.infra.enums.DelayCalcMode.*;
import static com.example.settlement.settle.infra.enums.SummaryMode.DAY;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 21:07
 */
@AllArgsConstructor
@Getter
public enum SettleCycleEnum {
    // 按自然日汇总，按自然日延期计算
    DAY_AFTER_1_DAY(1, DAY, NATUAL_DAY, 1),

    // 按自然日汇总，按工作日延期计算
    DAY_AFTER_1_WORKDAY(2, DAY, WORKDAY, 1),
    ;
    private int value;
    private SummaryMode summaryMode; // 汇总模式：天、周、月、季、年
    private DelayCalcMode delayCalcMode; // 延期计算模式：自然日、工作日
    private int count; // 延迟天数

    public static SettleCycleEnum valueOf(int value) {
        for (SettleCycleEnum e : SettleCycleEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }
}
