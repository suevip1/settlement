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
    ErrorNo SETTLE_STATUS_ERROR = new ErrorNo(10006, "结算单状态错误");
    ErrorNo DETAIL_INFO_EMPTY = new ErrorNo(10007, "结算详情单为空");
    ErrorNo DETAIL_INFO_STATE_ERROR = new ErrorNo(10008, "结算详情单状态错误");
    ErrorNo COMPLETE_CLEAR_DETAILS_IS_NULL = new ErrorNo(10009, "完成清算的详情单为空");
    ErrorNo COMPLETE_CLEAR_SETTLE_STATUS_ERROR = new ErrorNo(10010, "完成清算的结算单状态错误");
    ErrorNo COMPLETE_CLEAR_DETAILS_SIZE_ERROR = new ErrorNo(10011, "完成清算的详情单数量错误，传入详情单数据和已存在的详情单数量不一致");
    ErrorNo USER_TRADE_TYPE_NOT_EXISTS = new ErrorNo(10012, "用户交易类型不存在");
    ErrorNo NOT_TIME_TO_CLEARING = new ErrorNo(10013, "未到结算时间");
    ErrorNo RISK_MANAGE_SETTLE_STATUS_ERROR = new ErrorNo(10014, "风控管理结算单状态错误");
    ErrorNo RISK_MANAGE_WAIT = new ErrorNo(10015, "当前风控拦截，不可进行清算");
}
