package com.example.settlement.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 11:45
 */
@AllArgsConstructor
@Getter
public enum UserProductTypeEnum {
    PAY_IN(1, "主动支付"),
    PAY_OUT(2, "代付"),
    ;
    private int value;
    private String desc;
}
