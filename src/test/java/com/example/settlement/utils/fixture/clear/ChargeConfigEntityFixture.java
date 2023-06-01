package com.example.settlement.utils.fixture.clear;

import com.example.settlement.binlog.enums.CardGroupEnum;
import com.example.settlement.binlog.enums.PayTypeEnum;
import com.example.settlement.binlog.enums.UserTypeEnum;
import com.example.settlement.common.enums.UserProductTypeEnum;
import com.example.settlement.common.enums.UserTradeTypeEnum;
import com.example.settlement.config.entity.ChargeConfigEntity;
import com.example.settlement.config.entity.SettlementConfigEntity;
import org.apache.commons.lang3.RandomUtils;

import javax.xml.crypto.Data;
import java.time.Instant;
import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 13:38
 */
public class ChargeConfigEntityFixture {

    public static ChargeConfigEntity regular(SettlementConfigEntity settleConfig, int userTradeType, Integer feeCode) {
        ChargeConfigEntity configEntity = template(feeCode);
        configEntity.setUserId(settleConfig.getUserId());
        configEntity.setUserProduct(settleConfig.getUserProduct());
        configEntity.setUserTradeType(userTradeType);
        configEntity.setCountryCode(configEntity.getCountryCode());
        configEntity.setCurrency(configEntity.getCurrency());
        return configEntity;
    }

    private static ChargeConfigEntity template(Integer feeCode) {
        return ChargeConfigEntity.builder()
                .countryCode("CN")
                .configId(String.valueOf(RandomUtils.nextLong()))
                .userId(RandomUtils.nextLong())
                .userType(UserTypeEnum.B.getValue())
                .userProduct(UserProductTypeEnum.PAY_OUT.getValue())
                .userTradeType(UserTradeTypeEnum.PAY.getValue())
                .payType(PayTypeEnum.CARD_TYPE.getValue())
                .cardGroup(CardGroupEnum.VISA_CREDIT.getValue())
                .feeCode(feeCode)
                .currency("CNY")
                .feeRate("16")
                .feeBase(10)
                .feeMax((long)Integer.MAX_VALUE)
                .feeMin(0L)
                .activationTime(Date.from(Instant.parse("2000-01-01T08:00:00:00Z")))
                .expireTime(Date.from(Instant.parse("2099-01-01T08:00:00:00Z")))
                .installmentTiers(0)
                .effective(1)
                .archive(0)
                .remark("test")
                .version(0)
                .createTime(new Date())
                .updateTime(new Date())
                .build();

    }
}
