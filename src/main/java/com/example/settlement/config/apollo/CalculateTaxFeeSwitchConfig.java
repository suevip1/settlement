package com.example.settlement.config.apollo;

import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.common.utils.datetime.DateTimeFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 计税开关配置
 * @author yangwu_i
 * @date 2023/4/30 13:00
 */
@Slf4j
// @ApolloConfig(
//         namespace = "settlement",
//         configName = "clear_calculate_tax_fee_switch",
// ) todo: 接入 apollo
public class CalculateTaxFeeSwitchConfig {

    // @ApolloField(name = "startTime")
    private static String startTime; // 2023-05-01 13:23:43

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.systemDefault();

    public static Date getStartTime() {
        if (StringUtils.isBlank(startTime)) {
            throw new ErrorNoException(ErrorNo.PARAM_ERROR, "计税开关配置错误");
        }
        LocalDateTime localDateTime = LocalDateTime.parse(startTime, formatter);
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

}

