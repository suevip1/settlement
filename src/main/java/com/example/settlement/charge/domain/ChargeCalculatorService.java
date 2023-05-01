package com.example.settlement.charge.domain;

import com.alibaba.fastjson2.JSON;
import com.example.settlement.charge.domain.feecharge.ChargeCalculator;
import com.example.settlement.charge.domain.feecharge.FeeCalItem;
import com.example.settlement.charge.domain.feecharge.TradeInfo;
import com.example.settlement.charge.domain.feecomputer.feeitem.FeeConfig;
import com.example.settlement.charge.infra.enums.TaxTypeEnum;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.db.entity.ClearingBillPayoutEntity;
import com.example.settlement.clear.infra.db.mapper.IClearingBillMapper;
import com.example.settlement.clear.infra.db.mapper.IClearingBillPayoutMapper;
import com.example.settlement.clear.infra.errorno.ClearingErrorNo;
import com.example.settlement.clear.model.ClearResult;
import com.example.settlement.common.enums.FeeTypeEnum;
import com.example.settlement.common.enums.FundDirectEnum;
import com.example.settlement.common.enums.RefundModeEnum;
import com.example.settlement.common.enums.UserTradeTypeEnum;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.config.apollo.CalculateTaxFeeSwitchConfig;
import com.example.settlement.config.entity.SettlementConfigEntity;
import com.example.settlement.config.entity.TaxRuleConfig;
import com.example.settlement.settle.SettlementQueryRepo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 16:57
 */
@Slf4j
@Service
public class ChargeCalculatorService {

    @Resource
    private ChargeCalculatorRepo repo;

    @Resource
    private SettlementQueryRepo settlementQueryRepo;
    @Resource
    private IClearingBillMapper clearBillMapper;

    @Resource
    private IClearingBillPayoutMapper clearBillPayoutMapper;
    /**
     * 计算各种费项，包括手续费、分期费、税费和净额
     * @param info
     * @return
     */
    public List<FeeCalItem> calculateChargeFee(TradeInfo info) {
        ChargeCalculator calculator =  repo.fetch(info.userId());
        List<FeeCalItem> feeList = calculator.calculate(info);
        // 根据费率配置计算交易手续费，代表平台收取的费用
        long feeSum = feeList.stream().mapToLong(FeeCalItem::feeAmount).sum();
        // 计算税费
        long taxFee = 0;
        Date startTime = CalculateTaxFeeSwitchConfig.getStartTime();
        if (new Date().after(startTime)) {
            List<TaxRuleConfig> taxRules = settlementQueryRepo.fetchTaxRule(info.userId());
            if (!CollectionUtils.isEmpty(taxRules)) {
                for (TaxRuleConfig taxRule : taxRules) {
                    switch (TaxTypeEnum.valueOf(taxRule.getTaxType())) {
                        case VAT:
                        if (feeSum != 0) {
                            taxFee = new BigDecimal(taxRule.getTaxRate()).multiply(new BigDecimal(feeSum)).setScale(0, RoundingMode.HALF_UP).longValue();
                        }
                        continue;
                        default:
                            log.error("不支持的税费类型", JSON.toJSON(info));
                    }
                }
            }
        }
        // 添加税费项
        if (taxFee != 0) {
            FeeConfig tax = new FeeConfig(FeeTypeEnum.PROCESSED_TAX_FEE.getCode(), "system_default_id", info.tradeCurrency());
            feeList.add(new FeeCalItem(tax, taxFee, info.tradeCurrency()));
            feeSum += taxFee;
        }

        // 计算净额
        long netAmount = 0;
        UserTradeTypeEnum userTradeType = UserTradeTypeEnum.valueOf(info.userTradeType());
        if (userTradeType == null || userTradeType.getDirect() == null) {
            throw new ErrorNoException(ErrorNo.PARAM_ERROR, "userTradeType illegal, calculate netAmount fail");
        }
        if (userTradeType.getDirect() == FundDirectEnum.DIRECT_PAYMENT) {
            netAmount = info.tradeAmount() - feeSum;
        } else {
            // todo ?
            netAmount = -info.tradeAmount() - feeSum;
        }
        // 添加净额项
        FeeConfig net = new FeeConfig(FeeTypeEnum.SETTLED_NET_AMOUNT.getCode(), "system_default_id", info.tradeCurrency());
        feeList.add(new FeeCalItem(net, netAmount, info.tradeCurrency()));

        log.info("calculate request: {}, response: {}", JSON.toJSON(info), JSON.toJSON(feeList));
        return feeList;
    }

    public ClearResult calculateRefundFee(ClearingBillEntity entity) {
        ClearingBillEntity originEntity = clearBillMapper.selectByTradeIdAndTradeType(entity.getOriginTradeId(), entity.getOriginTradeType());
        if (originEntity == null) {
            log.error("originTradeId not exist, entity: {}, {}", entity, ClearingErrorNo.BINLOG_PROCESS_ORIGIN_TRADE_NOT_EXIST);
            throw new ErrorNoException(ClearingErrorNo.BINLOG_PROCESS_ORIGIN_TRADE_NOT_EXIST, "originTradeId not exist");
        }

        SettlementConfigEntity settleConfig = entity.getSettleConfig();
        long tradeFee = 0; // 交易手续费
        long refundedTradeFee = 0; // 已退手续费
        if (RefundModeEnum.TRADE_FEE_REFUND.getValue() == settleConfig.getRefundMode()) {
            long refundedTradeAmount = 0; // 已退交易金额
            List<Integer> feeTypes = List.of(FeeTypeEnum.PROCESSED_TRADE_FEE.getCode(), FeeTypeEnum.UNPROCESSED_TRADE_FEE.getCode());
            List<ClearingBillPayoutEntity> billPayoutEntities = clearBillPayoutMapper.selectByOriginTradeIdAndFeeCodeList(entity.getUserId(), entity.getTradeId(), entity.getTradeType(), feeTypes);
            for (ClearingBillPayoutEntity billPayoutEntity : billPayoutEntities) {
                refundedTradeAmount += billPayoutEntity.getAmount();
                refundedTradeFee += billPayoutEntity.getFeeAmount();
            }
            tradeFee = doCalculateRefundFee(originEntity.getTradeAmount(), originEntity.getTradeFeeAmount(), refundedTradeAmount, refundedTradeFee, entity.getTradeAmount());
        }
        entity.setTradeFeeAmount(tradeFee);

        // 退分期手续费
        long installmentFee = 0; // 分期手续费
        long refundedInstallmentFee = 0; // 已退分期手续费
        if (originEntity.getInstallmentFeeAmount() > 1 &&
                RefundModeEnum.INSTALLMENT_FEE_REFUND.getValue() == settleConfig.getInstallmentFeeRefundMode()) {
            long refundedInstallmentAmount = 0; // 已退分期金额
            List<Integer> feeTypes = List.of(FeeTypeEnum.PROCESSED_INSTALLMENT_FEE.getCode(), FeeTypeEnum.UNPROCESSED_INSTALLMENT_FEE.getCode());
            List<ClearingBillPayoutEntity> billPayoutEntities = clearBillPayoutMapper.selectByOriginTradeIdAndFeeCodeList(entity.getUserId(), entity.getTradeId(), entity.getTradeType(), feeTypes);
            for (ClearingBillPayoutEntity billPayoutEntity : billPayoutEntities) {
                refundedInstallmentAmount += billPayoutEntity.getAmount();
                refundedInstallmentFee += billPayoutEntity.getFeeAmount();
            }
            installmentFee = doCalculateRefundFee(originEntity.getTradeAmount(), originEntity.getInstallmentFeeAmount(), refundedInstallmentAmount, refundedInstallmentFee, entity.getTradeAmount());
        }
        entity.setInstallmentFeeAmount(installmentFee);

        // 税费计算，根据已退税费计算
        long chargeFee = -tradeFee - installmentFee; // 本次需退手续费
        long originTotalChargeFee = originEntity.getTradeFeeAmount() + originEntity.getInstallmentFeeAmount(); // 原交易总手续费
        long originTotalTaxFee = originEntity.getTaxFeeAmount();
        long refundedChargeFee = -refundedTradeFee - refundedInstallmentFee; // 已退手续费
        long taxFee = 0; // 本次需退税费
        if (chargeFee != 0) {
            long refundedTaxFee = 0; // 已退税费
            List<Integer> feeTypes = List.of(FeeTypeEnum.PROCESSED_TAX_FEE.getCode(), FeeTypeEnum.UNPROCESSED_TAX_FEE.getCode());
            List<ClearingBillPayoutEntity> billPayoutEntities = clearBillPayoutMapper.selectByOriginTradeIdAndFeeCodeList(entity.getUserId(), entity.getTradeId(), entity.getTradeType(), feeTypes);
            for (ClearingBillPayoutEntity billPayoutEntity : billPayoutEntities) {
                refundedTaxFee += billPayoutEntity.getFeeAmount();
            }
            taxFee = doCalculateRefundFee(originTotalChargeFee, originTotalTaxFee, refundedChargeFee, refundedTaxFee, chargeFee);
        }
        entity.setTaxFeeAmount(taxFee);

        // 设置净额 todo: 为什么为负值
        entity.setNetAmount(-entity.getTradeAmount() - entity.getTradeFeeAmount() - entity.getInstallmentFeeAmount() - entity.getTaxFeeAmount());
        return ClearResult.buildSuccessResult();
    }

    /**
     * 计算退款手续费，返回负值
     * @param originTradeAmount 原交易总金额
     * @param originTradeFeeAmount 原交易手续费
     * @param refundedTradeAmount 已退交易总金额
     * @param refundedTradeFee 已退交易总手续费（负值）
     * @param refundAmount 本次退款交易金额
     * @return
     */
    private long doCalculateRefundFee(Long originTradeAmount, Long originTradeFeeAmount, long refundedTradeAmount, long refundedTradeFee, Long refundAmount) {
        long feeAmount = 0;
        if (originTradeAmount > refundedTradeAmount + refundAmount) { // 非最后一笔退款，银行家算法
            // feeAmount 算出来为正值
            feeAmount = new BigDecimal(originTradeFeeAmount).multiply(new BigDecimal(refundAmount)).divide(new BigDecimal(originTradeAmount), 0, RoundingMode.HALF_EVEN).longValue();
            // 剩余可退手续费小于 feeAmount
            if (originTradeFeeAmount + refundedTradeFee < feeAmount) {
                feeAmount = originTradeFeeAmount + refundedTradeFee;
            }
        } else if (originTradeAmount == refundedTradeAmount + refundAmount) { // 最后一笔退款
            feeAmount = originTradeFeeAmount + refundedTradeFee;
        } else {
            log.error("refundAmount illegal, originTradeAmount: {}, refundedTradeAmount: {}, refundAmount: {}", originTradeAmount, refundedTradeAmount, refundAmount);
            throw new ErrorNoException(ErrorNo.PARAM_ERROR, "refundAmount illegal");
        }
        return -feeAmount;
    }
}
