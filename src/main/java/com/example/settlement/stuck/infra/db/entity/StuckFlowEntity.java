package com.example.settlement.stuck.infra.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 清分流水异常管理表
 * @author yangwu_i
 * @date 2023/4/29 11:08
 */
@Data
public class StuckFlowEntity implements Serializable {
    private Long id; // 自增主键
    private Long userId; // 商户id
    private Integer stuckType; //  导致流程卡住的错误类型
    private Long productType;
    private String tableIndex; // 所在表索引

    private Integer tradeType; // 交易类型
    private String tradeId; // 交易id

    private String tradeContext; // 交易内容
    private Long tradeFinishTime; // 交易创建时间，非0为正常创建，单位毫秒
    private Integer lastCode; // 最近异常码

    private String lastMsg; // 最近异常信息
    private Long retryCount; // 自动重试次数
    private Integer status; // 状态
    private String handler; // 人工处理人员
    private String handlerMsg; // 处理意见
    private String handlerContext; // 处理变换交易内容，json
    private String remark; // 备注
    private Integer archived; // 是否存档
    private Integer version; // 乐观锁版本号
    private Date createTime; // 创建时间
    private Date modifyTime; // 更新时间

    private static final long serialVersionUID = 1L;
}
