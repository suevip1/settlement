package com.example.settlement.config.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 20:28
 */
@Data
public class SettlementConfigEntity implements Serializable {
    private Long id;
    private String countryCode;
    private Long userId; // 商户编号
    private Integer userType; // 商户类型
    private Integer userProduct; // 商户产品类型
    private String configId; // 结算配置ID
    private Integer settleRiskStrategy; // 结算风控策略，0正常，1白名单
    private Integer refundMode; // 退款模式（交易手续费）
    private Integer installmentFeeRefundMode; // 退款模式（分期手续费）
    private Integer installmentSettleMode; // 分期结算模式
    private Integer settleMode; // 结算模式：周期、实时
    private Integer settleCycle; // 结算周期 实时 T+？天/周/月 N+?天
    private Long minSettleAmount; // 最小结算金额
    private String currency; // 结算币种
    private Integer disabled; // 结算状态
    private Date activationTime; // 激活时间，北京时间
    private Date expirationTime; // 失效时间，北京时间
    private Integer archive; // 归档状态 0未删除，1删除
    private String remark; // 备注
    private Integer version; // 版本号
    private Date createTime; // 创建时间
    private Date modifyTime; // 修改时间
    private static final long serialVersionUID = 1L;
}
