package com.example.settlement.settle.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 10:48
 */
@AllArgsConstructor
@Getter
public enum SettleStatusEnum {

    INIT(0, "初始化", false),
    ACCUMULATING(1, "累计中", false),
    RISKMANAGEMENT(2, "风控中", false),
    CLEARING(3, "清算中", false), // 小清算，只做轧差，完成后操作账户
    WAITING_SETTLE_TRADE_FEE(4, "等待结算交易手续费", true),
    WAITING_SETTLE_INSTALLMENT_FEE(5, "等待结算分期手续费", true),
    WAITING_SETTLE_TAX_FEE(6, "等待结算税费", true),
    WAITING_SETTLE_NET(7, "等待结算净额", true),
    SETTLED(8, "已结算", true);
    private int value;
    private String desc;
    // 结算单两大流程：1. 清算流程 2. 结算流程（进入此流程户，下一周期结算单可以开始结算）
    private boolean fundSetting;


    public static List<Integer> unSettled() {
        return List.of(INIT.value, ACCUMULATING.value, RISKMANAGEMENT.value, CLEARING.value, WAITING_SETTLE_TRADE_FEE.value, WAITING_SETTLE_INSTALLMENT_FEE.value, WAITING_SETTLE_TAX_FEE.value, WAITING_SETTLE_NET.value);
    }

    public static SettleStatusEnum valueOf(int value) {
        for (SettleStatusEnum statusEnum : SettleStatusEnum.values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        return null;
    }

}
