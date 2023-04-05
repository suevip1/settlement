package com.example.settlement.utils.datetime;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据国家码获取时区
 * @author yangwu_i
 * @date 2023/4/5 21:37
 */
public class ZoneIdByCountryCode {
    private static final Map<String, String> COUNTRY_CODE_TO_ZONE_ID = new HashMap<>();

    static {
        // 添加国家码与时区的映射关系，这里只添加了部分国家的示例
        COUNTRY_CODE_TO_ZONE_ID.put("US", "America/New_York");
        COUNTRY_CODE_TO_ZONE_ID.put("CN", "Asia/Shanghai");
        COUNTRY_CODE_TO_ZONE_ID.put("IN", "Asia/Kolkata");
        COUNTRY_CODE_TO_ZONE_ID.put("JP", "Asia/Tokyo");
        COUNTRY_CODE_TO_ZONE_ID.put("GB", "Europe/London");
    }

    public static ZoneId getZoneIdByCountryCode(String countryCode) {
        String zoneIdStr = COUNTRY_CODE_TO_ZONE_ID.getOrDefault(countryCode, "UTC");
        return ZoneId.of(zoneIdStr);
    }
}
