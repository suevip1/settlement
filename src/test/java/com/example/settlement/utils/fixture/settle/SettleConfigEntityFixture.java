package com.example.settlement.utils.fixture.settle;

import com.example.settlement.binlog.enums.UserTypeEnum;
import com.example.settlement.common.enums.RefundModeEnum;
import com.example.settlement.common.enums.SettleModeEnum;
import com.example.settlement.common.enums.UserProductTypeEnum;
import com.example.settlement.config.entity.SettlementConfigEntity;
import com.example.settlement.settle.infra.enums.SettleCycleEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 13:24
 */
public class SettleConfigEntityFixture {

    public static SettlementConfigEntity regular(int settleMode) {
        return templatePayIn(settleMode);
    }

    private static SettlementConfigEntity templatePayIn(int settleMode) {
        return SettlementConfigEntity.builder()
                .countryCode("CN")
                .userId(RandomUtils.nextLong())
                .userType(UserTypeEnum.B.getValue())
                .userProduct(UserProductTypeEnum.PAY_IN.getValue())
                .configId(String.valueOf(RandomUtils.nextLong()))
                .settleMode(settleMode)
                .settleCycle(settleMode == SettleModeEnum.CYCLE.getValue() ? SettleCycleEnum.DAY_AFTER_1_DAY.getValue() : null)
                .refundMode(RefundModeEnum.TRADE_FEE_REFUND.getValue())
                .minSettleAmount(0L)
                .currency("CNY")
                .disabled(0)
                .activationTime(new Date())
                .expirationTime(Date.from(Instant.parse("2099-12-31T23:59:59.999Z")))
                .archive(0)
                .createTime(new Date())
                .modifyTime(new Date())
                .build();
    }
}
