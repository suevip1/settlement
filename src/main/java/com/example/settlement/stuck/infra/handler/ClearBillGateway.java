package com.example.settlement.stuck.infra.handler;

import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.stuck.ExecResult;

/**
 *
 * @author yangwu_i
 * @date 2023/5/9 21:47
 */
public interface ClearBillGateway {
    ExecResult continueClear(String tradeId, Integer tradeType, String tradeContext, String handleContext);

}
