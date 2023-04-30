package com.example.settlement.clear.infra.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 出款清分表，如退款，拒付，提现等
 * @author yangwu_i
 * @date 2023/4/30 20:57
 */
@Data
@AllArgsConstructor
public class ClearingBillPayoutEntity implements Serializable {
    private Long id;
    private String tradeId;
    private String originTradeId;
    private String tradeType;
    private String originTradeType;
    private Long userId;
    private Integer userType;
    private Long amount; // 交易金额
    private Long originAmount; // 原交易金额
    private Integer feeCode; // 费用类型
    private Long feeAmount; // 费用金额
    private Date createTime;
    private Date updateTime;
}
