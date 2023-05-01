package com.example.settlement.settle.infra;

import com.example.settlement.common.exceptions.ErrorNo;

/**
 * 通用 0 - 9999
 * 清结算 10000 - 19999
 * @author yangwu_i
 * @date 2023/5/1 10:20
 */
public interface SettlementErrorNo {

    ErrorNo CONFIG_NOT_FOUND = new ErrorNo(10000, "配置未找到");
    ErrorNo SUMMARY_DETAIL_HAS_SETTLED = new ErrorNo(10001, "汇总详情单已经结算");
}
