package com.example.settlement.settle.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 21:13
 */
@AllArgsConstructor
@Getter
public enum DelayCalcMode {
    NATUAL_DAY(1, "按自然日延迟计算"),
    WORKDAY(2, "按工作日延迟计算"),
    ;

    private int value;
    private String desc;
}
