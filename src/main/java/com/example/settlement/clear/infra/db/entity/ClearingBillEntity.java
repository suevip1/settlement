package com.example.settlement.clear.infra.db.entity;

import com.example.settlement.config.entity.SettlementConfigEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 交易（支付、退款、拒付等）清分表实体
 * @author yangwu_i
 * @date 2023/4/26 19:57
 */
@Data
@AllArgsConstructor
public class ClearingBillEntity implements Serializable {

    private Integer productType;
    private String tradeId;
    private String originTradeId;
    private Integer tradeType;
    private Integer originTradeType;
    private Date tradeFinishTime;

    private Integer payType;

    private Integer cardGroup;
    private String orderId; // 商户交易单号

    private Integer settleMode;
    private Long userId;
    private Integer userType;
    private String countryCode;
    private String currency;
    private Long tradeAmount; // 交易（支付、退款、拒付等）总额，分
    private Long originTradeAmount; // 原交易总额，分

    private Long netAmount; // 净额，分
    private Long tradeFeeAmount; // 交易手续费， 分
    private Long taxFeeAmount; // 税费，分

    private Long installmentFeeAmount; // 分期手续费
    private SettlementConfigEntity SettleConfig; // 结算配置，binlog 解析就设置，todo: ?
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
