package com.example.settlement.binlog;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 00:21
 */
@Data
@Slf4j
public class Binlog {
    private String xid;
    private String binlog;
    private String db;
    private String table;
    private String event;
    private long time;
    private long canalTime;
    private ArrayList<BinlogItem> columns;
    private int processedTimes = 0;

    // 为分表前的表名（去除后缀）
    private String originTableName;
    @JsonIgnore
    public Map<String, BinlogItem> binlogItemMap;

    public static Binlog parse(String message) {
        Binlog binlog = JSON.to(Binlog.class, message);
        log.info("Binlog.parse binlog: {}", binlog);
        binlog.binlogItemMap = initBinlogItemMap(binlog.getColumns());
        binlog.originTableName = getOriginTableName(binlog.table);
        return binlog;
    }

    private static String getOriginTableName(String tableName) {
        tableName = tableName.trim();
        int index = tableName.lastIndexOf("_");
        if (index == -1) {
            return tableName;
        }
        return tableName.substring(0, index);
    }

    private static Map<String, BinlogItem> initBinlogItemMap(ArrayList<BinlogItem> columns) {
        Map<String, BinlogItem> map = new HashMap<>();
        for (BinlogItem column : columns) {
            map.put(column.getKey(), column);
        }
        return map;
    }

    public String getValue(String key) {
       BinlogItem item = binlogItemMap.get(key);
         if (item == null) {
             log.warn("Binlog.getValue item is null, key: {}", key);
              return "";
         }
         return StringUtils.defaultString(item.getValue(), "");
    }
}
