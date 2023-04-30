package com.example.settlement.charge.domain;

import com.example.settlement.charge.domain.feecharge.ChargeCalculator;
import com.example.settlement.charge.domain.feecomputer.SubitemComputer;
import com.example.settlement.charge.domain.feecomputer.feecomputer.IFeeComputer;
import com.example.settlement.charge.domain.feecomputer.feecomputer.LinearComputer;
import com.example.settlement.charge.domain.feecomputer.feeitem.FeeConfig;
import com.example.settlement.charge.domain.feecomputer.match.TradeMatcher;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.config.ConfigQueryService;
import com.example.settlement.config.entity.ChargeConfigEntity;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.example.settlement.clear.infra.errorno.ClearingErrorNo.CACHE_LOAD_FAILED;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 17:00
 */
@Slf4j
@Service
public class ChargeCalculatorRepo {
    @Resource
    private ConfigQueryService configQueryService;
    private final Cache<Long, ChargeCalculator> configCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

    public ChargeCalculator fetch(Long merchantId) {
        try {
            return configCache.get(merchantId, () -> {
                ChargeCalculator calculator = new ChargeCalculator();
                configQueryService.selectActiveConfigsByUserId(merchantId).forEach(entity -> {
                    IFeeComputer computer = parseToComputer(entity);
                    FeeConfig config = parseToFeeItemConfig(entity);
                    TradeMatcher matcher = parseToTradeMatcher(entity);
                    calculator.addFeeComputer(new SubitemComputer(config, matcher, computer));
                });
                return calculator;
            });
        } catch (Exception e) {
            log.info("fetch charge calculator error", e);
            throw new ErrorNoException(CACHE_LOAD_FAILED, "");
        }
    }

    private TradeMatcher parseToTradeMatcher(ChargeConfigEntity entity) {
        return new TradeMatcher(entity.getUserProduct(), entity.getUserTradeType(), entity.getPayType(), entity.getCurrency(), entity.getActivationTime(), entity.getExpireTime());
    }

    private FeeConfig parseToFeeItemConfig(ChargeConfigEntity entity) {
        return new FeeConfig(entity.getFeeCode(), entity.getConfigId(), entity.getCurrency());
    }



    private IFeeComputer parseToComputer(ChargeConfigEntity entity) {
       return new LinearComputer(entity.getFeeBase(), NumberUtils.toFloat(entity.getFeeRate(), 0f),  entity.getFeeMin(), entity.getFeeMax());
    }
}
