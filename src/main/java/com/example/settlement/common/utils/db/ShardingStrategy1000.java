package com.example.settlement.common.utils.db;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 14:50
 */
public class ShardingStrategy1000 {
    public String doSharding(String tableName, String tradeId) {
        return tableName + "_" + Integer.parseInt(tradeId) % 1000;
    }
}
