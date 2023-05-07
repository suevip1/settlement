package com.example.settlement.settle.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/7 15:15
 */
@AllArgsConstructor
@Getter
public enum WithdrawStatusEnum {
    INIT(0, "初始化"),
    SUCCESS_TO_CASH_ACCT(1, "成功结算到现金账户");
    private int value;
    private String desc;

}
