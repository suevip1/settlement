package com.example.settlement.clear.infra.db.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 清分出款交易明细实体
 * @author yangwu_i
 * @date 2023/5/1 17:03
 */
@Data
@Builder
public class ClearPayoutIndexEntity implements Serializable {
    private Long id;
    private String tradeId;
    private String originTradeId; // 原交易单号
    private Integer tradeType;
    private Integer originTradeType; // 原交易类型
    private Long userId;
    private Integer userType;
    private Long amount; // 交易金额
    private Long originAmount; // 原交易金额
    private Integer feeType; // 费用类型（交易手续费、分期费、税费）
    private Long feeAmount; //  费用金额
    private Date createTime;
    private Date updateTime;
}
