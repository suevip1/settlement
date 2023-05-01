package com.example.settlement.clear.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 16:26
 */
@AllArgsConstructor
@Getter
public enum SummaryStateEnum {
    ACCUMULATING(0, "累加流程中"),
    CLEARING(1, "清算流程中");
    private int value;
    private String desc;
}
