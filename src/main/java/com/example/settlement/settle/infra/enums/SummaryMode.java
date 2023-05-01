package com.example.settlement.settle.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交易流水汇总模式
 * @author yangwu_i
 * @date 2023/5/1 21:12
 */
@AllArgsConstructor
@Getter
public enum SummaryMode {
    DAY(1, "天"),
    WEEK(2, "周"),
    BIWEEK(3, "双周"),
    MONTH(4, "月"),
    ;

    private int value;
    private String desc;

}
