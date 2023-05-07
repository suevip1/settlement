package com.example.settlement.clear.model;

import lombok.Data;

import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 09:46
 */
@Data
public class ClearingSetting {
        private String countryCode;
        private Long userId;
        Integer userType;
        Integer userProduct;
        String configId; // 采用的结算配置Id
        Integer settleMode;
        private Long minSettleAmount;
        String currency;
        Long cityId;
        ZoneId zoneId;
        Date activationTime; // 正式生效时间（判断交易时间）北京时间
        Date expirationTime; // 失效时间（判断交易时间）北京时间
}
