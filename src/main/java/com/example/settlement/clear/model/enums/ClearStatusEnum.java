package com.example.settlement.clear.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/4/26 20:26
 */
@AllArgsConstructor
@Getter
public enum ClearStatusEnum {
    INIT(0, "初始化"),

    SUCCESS(1, "清算成功"),
    CLEAR(2, "清分成功"),
    TRANS_TALLY(3, "交易记账成功"),
    FEE_TALLY(4, "手续费记账成功"),
    REALTIME_SETTLE_FEE_TALLY(5, "实时结算，收手续费成功"),
    REALTIME_SETTLE_NET_TALLY(6, "实时结算，收净额成功"),
    NET(7, "净额清算成功"),
    FEE(8, "手续费清算成功"),
    ACCUMULATE(9, "费用累计成功"),

    ;
    private int status;
    private String desc;
}
