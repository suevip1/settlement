package com.example.settlement.binlog.enums;

import com.example.settlement.binlog.Binlog;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 01:03
 */
@AllArgsConstructor
@Getter
public enum CardGroupEnum {
    UNKNOWN(0, "UNKNOWN"),
    VISA_CREDIT(1, "VISA_CREDIT"),
    VISA_DEBIT(2, "VISA_DEBIT"),
    MASTER_CREDIT(3, "MASTER_CREDIT"),
    MASTER_DEBIT(4, "MASTER_DEBIT"),
    AMERICAN_EXPRESS_CREDIT(5, "AMERICAN_EXPRESS_CREDIT"),
    AMERICAN_EXPRESS_DEBIT(6, "AMERICAN_EXPRESS_DEBIT");
    private final int value;
    private final String desc;
}
