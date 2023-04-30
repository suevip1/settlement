package com.example.settlement.clear.infra.errorno;

import com.example.settlement.common.exceptions.ErrorNo;

/**
 *
 * @author yangwu_i
 * @date 2023/4/26 22:31
 */
public interface ClearingErrorNo {
    ErrorNo CLEAR_PROCESS_INSERT_ENTITY_ERROR = new ErrorNo(1, "清分处理插入实体错误");
    ErrorNo CLEAR_PROCESS_STATUS_ERROR = new ErrorNo(2, "清分处理状态错误");
    ErrorNo CACHE_LOAD_FAILED = new ErrorNo(3, "缓存加载失败");
    ErrorNo CLEAR_PROCESS_MISS_FEE_ITEMS = new ErrorNo(4, "清分处理缺少费用项");
    ErrorNo CLEAR_PROCESS_PAYIN_CALC_DB_UPDATE_FAIL = new ErrorNo(5, "清分处理入账计算更新数据库失败");
    ErrorNo CLEAR_PROCESS_NOT_FEE_ITEMS = new ErrorNo(6, "清分处理没有费用项");

    ErrorNo BINLOG_PROCESS_ORIGIN_TRADE_NOT_EXIST = new ErrorNo(7, "binlog处理原交易不存在");
    ErrorNo CLEAR_PROCESS_NOT_FOUND_ORIGIN_BILL = new ErrorNo(8, "清分处理没有找到原订单");
}
