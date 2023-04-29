package com.example.settlement.clear.infra.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 清分表实体
 * @author yangwu_i
 * @date 2023/4/26 19:57
 */
@Data
@AllArgsConstructor
public class ClearingBillEntity implements Serializable {

    private String tradeId;
    private Integer tradeType;
    private Date tradeFinishTime;

    private String orderId; // 商户交易单号

    private Integer settleMode;
    private Long userId;
    private Integer userType;
    private String countryCode;
    private String currency;
    private Long tradeAmount; // 交易总额，分

    private Long netAmount; // 净额，分
    private Long tradeFeeAmount; // 交易手续费， 分

    private String remark; // 描述

    private Integer status; // 清分状态机状态

    private Date modifyTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClearingBillEntity that = (ClearingBillEntity) o;
        return tradeId.equals(that.tradeId) && userId.equals(that.userId) && userType.equals(that.userType) && tradeType.equals(that.tradeType) && countryCode.equals(that.countryCode) && currency.equals(that.currency) && tradeAmount.equals(that.tradeAmount);
    }
}
