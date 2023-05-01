package com.example.settlement.settle.model.valueobj;

import java.util.Date;

/**
 * 汇总唯一键：根据什么进行汇总
 * (产品，交易类型，天）-> 一个汇总单
 * @author yangwu_i
 * @date 2023/5/1 09:53
 */
public record SummaryId(
        Integer userProduct,
        Integer userTradeType,
        Date summaryTime) {
}
