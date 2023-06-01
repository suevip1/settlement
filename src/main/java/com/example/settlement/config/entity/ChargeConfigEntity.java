package com.example.settlement.config.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 17:13
 */
@Data
@Builder
public class ChargeConfigEntity implements Serializable {
    private Long id; // 自增主键
    private String countryCode; // 国家
    private String configId; // 手续费配置唯一 id
    private Long userId; // 商户 id
    private Integer userType; // 商户类型
    private Integer userProduct; // 商户产品
    private Integer userTradeType; // 商户交易类型
    private Integer payType; // 商户支付类型
    private Integer cardGroup; // 卡组
    private Integer installmentTiers;
    private Integer feeCode; // 费项编码
    private String currency; // 币种
    private Integer feeBase; // 计费基数
    private String feeRate; // 费率，单位为百分号，精确到小数点后3位
    private Long feeMin; // 最低手续费，单位为分
    private Long feeMax; // 最高手续费，单位为分
    private Date activationTime; // 生效时间，北京时间
    private Date expireTime; // 失效时间，北京时间
    private Integer effective; // 是否是最新的一条
    private Integer archive; // 归档状态（0未删除，1删除）
    private String remark; // 备注
    private Integer version; // 版本号

    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    private static final long serialVersionUID = 1L;
}
