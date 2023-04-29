package com.example.settlement.common.utils.db;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 14:52
 */
public class ShardingStrategy100 {
    public String doSharding(String tableName, String tradeId) {
        return tableName + "_" + Integer.parseInt(tradeId) % 100;
    }
}
