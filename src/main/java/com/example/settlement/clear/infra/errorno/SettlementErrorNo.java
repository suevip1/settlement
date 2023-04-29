package com.example.settlement.clear.infra.errorno;

import com.example.settlement.common.exceptions.ErrorNo;

/**
 *
 * @author yangwu_i
 * @date 2023/4/26 22:24
 */
public interface SettlementErrorNo {
    ErrorNo ASSIGNED_SETTLEMENT_CONFIG_NOT_EXISTS = new ErrorNo(1, "指定的结算配置不存在");
}
