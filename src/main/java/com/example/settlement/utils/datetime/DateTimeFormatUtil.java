package com.example.settlement.utils.datetime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 日期格式化工具类
 * @author yangwu_i
 * @date 2023/4/5 20:52
 */
public class DateTimeFormatUtil {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String format(String countryCode, long timestamp, String pattern) {
        ZoneId zoneId = ZoneIdByCountryCode.getZoneIdByCountryCode(countryCode);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
        return formatter.format(zonedDateTime);
    }
}
