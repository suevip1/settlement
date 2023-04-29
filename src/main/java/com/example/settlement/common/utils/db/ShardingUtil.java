package com.example.settlement.common.utils.db;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 14:33
 */
public class ShardingUtil {
    private static final ShardingStrategy1000 shardingStrategy1000 = new ShardingStrategy1000();
    private static final ShardingStrategy100 shardingStrategy100 = new ShardingStrategy100();

    public static String shardingTableBy1000(String tableName, String tradeId) {
        if (StringUtils.isBlank(tradeId)) {
           return tableName + "_void";
        }
        try {
            return shardingStrategy1000.doSharding(tableName, tradeId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getShardingStrategy100(String tableName, String tradeId) {
        if (StringUtils.isBlank(tradeId)) {
            return tableName + "_void";
        }
        try {
            return shardingStrategy100.doSharding(tableName, tradeId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
