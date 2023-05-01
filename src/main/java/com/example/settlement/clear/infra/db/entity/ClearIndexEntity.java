package com.example.settlement.clear.infra.db.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 清分索引表
 * @author yangwu_i
 * @date 2023/5/1 15:46
 */
@Builder
@Data
public class ClearIndexEntity implements Serializable {
    private Long id;
    private String tradeId;
    private Integer tradeType;
    private Integer feeType; // 费用类型
    private Long userId;
    private Integer userType;
    private String settleId; // 结算单号
    private String detailId; // 结算明细单号
    private Date createTime;
    private Date updateTime;
}
