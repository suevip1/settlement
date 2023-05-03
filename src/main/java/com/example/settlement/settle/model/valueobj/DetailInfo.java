package com.example.settlement.settle.model.valueobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.Date;

/**
 * 结算详情信息
 * @author yangwu_i
 * @date 2023/5/3 12:16
 */
@Data
@AllArgsConstructor
public class DetailInfo {
        private String detailId;
        private Date summaryTime;
        private Integer state;
        private Integer transType;
        private Long riskCount; // 风控结果总数
        private Long totalTradeCount;
}

