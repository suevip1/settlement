package com.example.settlement.binlog.handler;

import com.example.settlement.binlog.Binlog;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;

import java.util.Map;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 00:38
 */
public interface TradeTableHandler {
    boolean matchThis(Binlog binlog);

    boolean needClearThis(Binlog binlog);

    ClearingBillEntity build(Binlog binlog);
    // 取出关键信息
    Map<String, String> buildExtra(Binlog binlog);
}
