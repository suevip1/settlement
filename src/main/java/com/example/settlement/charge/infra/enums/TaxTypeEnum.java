package com.example.settlement.charge.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 13:22
 */
@Getter
@AllArgsConstructor
public enum TaxTypeEnum {

    VAT(1, "增值税");
    private int code;
    private String desc;

    public static TaxTypeEnum valueOf(int code) {
        for (TaxTypeEnum taxTypeEnum : TaxTypeEnum.values()) {
            if (taxTypeEnum.code == code) {
                return taxTypeEnum;
            }
        }
        return null;
    }
}
