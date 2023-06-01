package com.example.settlement.utils.fixture.clear;

import com.example.settlement.binlog.enums.CardGroupEnum;
import com.example.settlement.binlog.enums.PayTypeEnum;
import com.example.settlement.binlog.enums.UserTypeEnum;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.common.enums.UserProductTypeEnum;
import com.example.settlement.common.enums.UserTradeTypeEnum;
import com.example.settlement.config.entity.SettlementConfigEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import javax.xml.crypto.Data;
import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 13:52
 */
public class ClearBillEntityFixture {
    public static ClearingBillEntity pay(SettlementConfigEntity settleConfig) {
        return ClearingBillEntity.builder()
                .tradeId("pay" + RandomStringUtils.randomAlphabetic(15))
                .originTradeId("")
                .orderId("pay" + RandomStringUtils.randomAlphabetic(15))
                .originOrderId("")
                .userId(settleConfig.getUserId())
                .userType(UserTypeEnum.B.getValue())
                .tradeType(UserTradeTypeEnum.PAY.getValue())
                .productType(UserProductTypeEnum.PAY_IN.getValue())
                .payType(PayTypeEnum.CARD_TYPE.getValue())
                .cardGroup(CardGroupEnum.VISA_CREDIT.getValue())
                .installmentTiers(1)
                .countryCode(settleConfig.getCountryCode())
                .currency(settleConfig.getCurrency())
                .tradeAmount(2000L)
                .status(0)
                .tradeCreateTime(new Date())
                .tradeFinishTime(new Date())
                .settleMode(settleConfig.getSettleMode())
                .settlementConfig(settleConfig).build();
    }
}
