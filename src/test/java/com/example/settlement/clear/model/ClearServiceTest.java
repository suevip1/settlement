package com.example.settlement.clear.model;

import com.example.settlement.SettlementApplication;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.db.mapper.IClearingBillMapper;
import com.example.settlement.clear.infra.enums.ClearStatusEnum;
import com.example.settlement.common.enums.FeeTypeEnum;
import com.example.settlement.common.enums.SettleModeEnum;
import com.example.settlement.common.enums.UserTradeTypeEnum;
import com.example.settlement.config.entity.ChargeConfigEntity;
import com.example.settlement.config.entity.SettlementConfigEntity;
import com.example.settlement.config.mapper.ChargeConfigMapper;
import com.example.settlement.config.mapper.SettleConfigMapper;
import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import com.example.settlement.settle.infra.db.mapper.SettleDetailMapper;
import com.example.settlement.utils.fixture.clear.ChargeConfigEntityFixture;
import com.example.settlement.utils.fixture.clear.ClearBillEntityFixture;
import com.example.settlement.utils.fixture.settle.SettleConfigEntityFixture;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 13:18
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = SettlementApplication.class)
class ClearServiceTest {

    private SettlementConfigEntity settleConfig;
    @Resource
    SettleConfigMapper settleConfigMapper;
    @Resource
    ChargeConfigMapper chargeConfigMapper;
    @Resource
    ClearService clearService;
    @Resource
    IClearingBillMapper clearBillMapper;
    @Resource
    SettleDetailMapper settleDetailMapper;

    private final int installmentTiers = 3;
    private List<Integer> installmentFeeItems = List.of(FeeTypeEnum.PROCESSED_TRADE_FEE.getCode(), FeeTypeEnum.PROCESSED_INSTALLMENT_FEE.getCode());
    private List<Integer> defaultFeeItems = List.of(FeeTypeEnum.PROCESSED_TRADE_FEE.getCode());

    @BeforeEach
    void setUp() {
        // Apollo.autoInit();


    }

    @AfterEach
    void tearDown() {
    }

    // 周期结算-支付（卡支付）
    @Test
    public void testPayCycleSettle() {
        initConfig(false, SettleModeEnum.CYCLE);
        ClearingBillEntity pay = ClearBillEntityFixture.pay(settleConfig);
        Assert.isTrue(clearService.handle(pay), "");
        checkClearResult(pay);
    }

    // 实时结算-支付（卡支付）
    @Test
    public void testPayRealTimeSettle() {
        initConfig(false, SettleModeEnum.REAL_TIME);
        ClearingBillEntity pay = ClearBillEntityFixture.pay(settleConfig);
        Assert.isTrue(clearService.handle(pay), "");
        checkClearResult(pay);
    }

    // 周期结算-支付（分期支付）
    @Test
    public void testInstallmentPayCycleSettle() {
        initConfig(true, SettleModeEnum.CYCLE);
        ClearingBillEntity pay = ClearBillEntityFixture.pay(settleConfig);
        Assert.isTrue(clearService.handle(pay), "");
        checkClearResult(pay);
    }

    // 实时结算-支付（分期支付）
    @Test
    public void testInstallmentPayRealTimeSettle() {
        initConfig(true, SettleModeEnum.REAL_TIME);
        ClearingBillEntity pay = ClearBillEntityFixture.pay(settleConfig);
        Assert.isTrue(clearService.handle(pay), "");
        checkClearResult(pay);
    }

    private void checkClearResult(ClearingBillEntity pay) {
        // 检查清分记录成功状态
        ClearingBillEntity exist = clearBillMapper.selectByTradeIdAndTradeType(pay.getTradeId(), pay.getTradeType());
        Assert.isTrue(exist != null && exist.getStatus() == ClearStatusEnum.SUCCESS.getStatus(), "");

        // 检查存在结算明细并累计正确
        Date summaryTime = Date.from(pay.getTradeFinishTime().toInstant().atZone(ZoneId.of(ZoneId.systemDefault().getId())).truncatedTo(ChronoUnit.DAYS).toInstant());
        SettleDetailEntity detailEntity = settleDetailMapper.selectOne(pay.getUserId(), pay.getProductType(), pay.getTradeType(), summaryTime);
        Assert.notNull(detailEntity, "");
    }

    private void initConfig(boolean isInstallmentPay, SettleModeEnum settleMode) {
        settleConfig = SettleConfigEntityFixture.regular(settleMode.getValue());
        settleConfigMapper.insertSelective(settleConfig);
        if (isInstallmentPay) {
            for (Integer feeCode : installmentFeeItems) {
                ChargeConfigEntity chargeConfig = ChargeConfigEntityFixture.regular(settleConfig, UserTradeTypeEnum.PAY.getValue(), feeCode);
                if (FeeTypeEnum.PROCESSED_TRADE_FEE.getCode() == feeCode) {
                    chargeConfig.setInstallmentTiers(0);
                } else {
                    chargeConfig.setInstallmentTiers(installmentTiers);
                }
            }
        } else {
            for (Integer feeCode : defaultFeeItems) {
                ChargeConfigEntity chargeConfig = ChargeConfigEntityFixture.regular(settleConfig, UserTradeTypeEnum.PAY.getValue(), feeCode);
                chargeConfigMapper.insertSelective(chargeConfig);
            }
        }
    }
}