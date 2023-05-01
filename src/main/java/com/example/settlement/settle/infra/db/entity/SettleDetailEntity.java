package com.example.settlement.settle.infra.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 汇总详情（结算详情）表
 * @author yangwu_i
 * @date 2023/5/1 11:26
 */
@Data
public class SettleDetailEntity implements Serializable {
    private Long id;
    private String countryCode;
    private String configId; // 汇总详情单生成时依赖的配置 id
    private Long userId;
    private Integer userType;
    private String settleId; // 结算单号
    private Integer settleMode; // 结算模式：实时 or 周期
    private Integer userProduct;
    private Integer userTradeType;
    private String detailId; // 结算详情单号
    private Date summaryTime; // 汇总时间点
    private Long totalCount; // 总交易笔数
    private Long totalAmount; // 总交易金额

    // 下面费用类型对应 FeeTypeEnum 中的 8 个枚举值
    private Long totalProcessedFee; // 总已处理交易手续费
    private Long totalUnProcessedFee; // 总未处理交易手续费
    private Long totalProcessedInstallmentFee; // 总已处理分期手续费
    private Long totalUnProcessedInstallmentFee; // 总未处理分期手续费
    private Long totalProcessedTaxFee; // 总已处理税费
    private Long totalUnProcessedTaxFee; // 总未处理税费
    private Long totalSettledNetAmount; // 总已结算净交易净额
    private Long totalUnSettledNetAmount; // 总未结算净交易净额

    private String currency;
    private Integer state; // 汇总状态
    private Integer version; // 乐观锁版本号
    private String remark; // 备注
    private Date createTime;
    private Date updateTime;
    private static final long serialVersionUID = 1L;
}
