package com.example.settlement.settle.infra.db.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 结算表
 * @author yangwu_i
 * @date 2023/5/1 15:24
 */
@Data
@Builder
public class SettleBillEntity {
    private Long id;
    private String countryCode;
    private Long userId;
    private Integer userType;
    private Integer userTradeType;
    private String configId; // 结算配置id
    private String settleId;
    private Integer settleMode;
    private Integer settleCycle;
    private Date settleTime;
    private Long totalCount; // 总交易笔数
    private Long totalNetAmount; // 总交易净额
    private Long totalNetCount; // 总净得交易笔数
    private Long totalFeeAmount; // 总交易手续费
    private Long totalFeeCount; // 总手续费笔数
    private String currency;
    private Date liquidStartTime;
    private Date liquidEndTime;
    private Date settleStartTime;
    private Date settleEndTime;
    private Integer settleStatus;
    private String remark;
    private Integer version; // 乐观锁
    private static final long serialVersionUID = 1L;
}
