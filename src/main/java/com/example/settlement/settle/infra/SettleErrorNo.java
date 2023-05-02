package com.example.settlement.settle.infra;

import com.example.settlement.common.exceptions.ErrorNo;

/**
 * 通用 0 - 9999
 * 清结算 10000 - 19999
 * @author yangwu_i
 * @date 2023/5/1 10:20
 */
public interface SettleErrorNo {

    ErrorNo CONFIG_NOT_FOUND = new ErrorNo(10000, "配置未找到");
    ErrorNo SUMMARY_DETAIL_HAS_SETTLED = new ErrorNo(10001, "汇总详情单已经结算");
    ErrorNo SETTLE_UNSUPPORTED_CYCLE = new ErrorNo(10002, "不支持的结算周期");
    ErrorNo SETTLE_UNSUPPORTED_DELAY_MODE = new ErrorNo(10003, "不支持的延迟结算模式");
    ErrorNo SETTLE_HOLIDAY_ERROR = new ErrorNo(10004, "节假日配置错误");
    ErrorNo LAST_BILL_IS_BEING_AUDITED = new ErrorNo(10005, "上一个结算单正在审核中");
}
