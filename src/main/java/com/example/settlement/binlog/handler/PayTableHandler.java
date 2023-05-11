package com.example.settlement.binlog.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.settlement.binlog.Binlog;
import com.example.settlement.binlog.enums.CardGroupEnum;
import com.example.settlement.binlog.enums.PayTypeEnum;
import com.example.settlement.binlog.enums.TradePayStatusEnum;
import com.example.settlement.binlog.enums.UserTypeEnum;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.enums.ClearStatusEnum;
import com.example.settlement.common.enums.UserProductTypeEnum;
import com.example.settlement.common.enums.UserTradeTypeEnum;
import com.example.settlement.config.entity.SettlementConfigEntity;
import com.example.settlement.config.mapper.SettleConfigMapper;
import com.google.common.collect.Table;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 00:39
 */
@Slf4j
@Component
public class PayTableHandler implements TradeTableHandler {
    private final String TABLE_NAME = "pay";
    @Resource
    private SettleConfigMapper settleConfigMapper;

    @Override
    public boolean matchThis(Binlog binlog) {
        return StringUtils.equalsIgnoreCase(TABLE_NAME, binlog.getOriginTableName());
    }

    @Override
    public boolean needClearThis(Binlog binlog) {
        try {
            int status = Integer.parseInt(binlog.getValue("status"));
            if (status != TradePayStatusEnum.PAY_SUCCESS.getValue()) {
                return false;
            }
        } catch (Exception e) {
            log.error("PayTableHandler.needClearThis error: {}", e);
            return false;
        }
        return true;
    }

    @Override
    public ClearingBillEntity build(Binlog binlog) {
        ClearingBillEntity entity = new ClearingBillEntity();
        try {
            // 参数校验
            preParamsCheck(binlog);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            entity.setTradeId(binlog.getValue("out_trade_id")); // 交易单号
            entity.setOriginOrderId(null);
            entity.setOrderId(binlog.getValue("order_id")); // 商户订单号
            entity.setOriginOrderId(null);
            entity.setUserId(Long.parseLong(binlog.getValue("product_id"))); // 用户ID
            entity.setUserType(UserTypeEnum.B.getValue()); // 用户类型
            entity.setTradeType(UserTradeTypeEnum.PAY.getValue());
            entity.setOriginTradeType(null);
            entity.setProductType(UserProductTypeEnum.PAY_IN.getValue());
            entity.setCountryCode(binlog.getValue("country_code"));

            // 处理支付明细
            handlePayDetail(binlog, entity);

            entity.setCurrency(binlog.getValue("currency"));
            entity.setTradeAmount(Long.parseLong(binlog.getValue("amount")));
            entity.setStatus(ClearStatusEnum.INIT.getStatus());
            entity.setTradeCreateTime(dateFormat.parse(binlog.getValue("create_time")));
            entity.setTradeFinishTime(dateFormat.parse(binlog.getValue("pay_time")));
            entity.setCreateTime(date);
            entity.setModifyTime(date);

            // 分期信息
            handleInstallmentInfo(binlog, entity);
            // 结算信息
            handleSettleConfig(binlog, entity);
            // 实体校验
            afterParamsCheck(entity);
        } catch (Exception e) {
            log.error("PayTableHandler.build error: {}", e);
            return null;
        }

        return null;
    }

    private void handleSettleConfig(Binlog binlog, ClearingBillEntity entity) {
        SettlementConfigEntity config = settleConfigMapper.selectByUserIdAndProductType(entity.getUserId(), entity.getProductType());
        Assert.notNull(config, "");
        entity.setSettleMode(config.getSettleMode());
        entity.setSettleConfig(config);
    }

    private void handleInstallmentInfo(Binlog binlog, ClearingBillEntity entity) {
        if (StringUtils.isBlank(binlog.getValue("installment_info"))) {
            entity.setInstallmentTiers(0);
            return;
        }
        String installment = JSON.parseObject(binlog.getValue("installment_info")).getString("installment");
        if (StringUtils.isNotBlank(installment)) {
            Boolean isInstallment = JSON.parseObject(installment).getBoolean("is_installment");
            int installTiers = isInstallment != null && isInstallment ? JSON.parseObject(installment).getIntValue("number") : 0;
            entity.setInstallmentTiers(installTiers);
        } else {
            entity.setInstallmentTiers(0);
        }
    }

    private void handlePayDetail(Binlog binlog, ClearingBillEntity entity) {
        JSONObject payDetailJson = JSON.parseObject(binlog.getValue("pay_detail"));
        JSONArray payDetailList = payDetailJson.getJSONArray("payDetailList");
        Assert.notNull(payDetailList, "");
        Assert.notEmpty(payDetailList.toArray(), "");
        boolean found = false;
        for (Object obj : payDetailList) {
            JSONObject payDetail = (JSONObject) obj;
            // 卡支付获取卡组、卡类型
            PayTypeEnum payType = PayTypeEnum.valueOf(payDetail.getIntValue("channleID"));
            if (payType == PayTypeEnum.CARD_TYPE) {
                found = true;
                entity.setPayType(PayTypeEnum.CARD_TYPE.getValue());
                Assert.hasLength(payDetail.getString("cardGroup"), "");
                CardGroupEnum cardGroup = CardGroupEnum.valueOf(payDetail.getString("cardGroup"));
                entity.setCardGroup(cardGroup.getValue());
            } else if (payType != null && payType != PayTypeEnum.NONE) {
                // 非卡支付
                found = true;
                entity.setPayType(payType.getValue());
                entity.setCardGroup(CardGroupEnum.UNKNOWN.getValue());
            }
        }
        if (!found) {
            // 余额支付
            entity.setPayType(PayTypeEnum.BALANCE.getValue());
            entity.setCardGroup(CardGroupEnum.UNKNOWN.getValue());
        }

    }

    private void preParamsCheck(Binlog binlog) {
        Assert.hasLength(binlog.getValue("out_trade_id"), "");
        Assert.hasLength(binlog.getValue("order_id"), "");
        Assert.hasLength(binlog.getValue("product_id"), "");
        Assert.hasLength(binlog.getValue("pay_detail"), "");
        Assert.hasLength(binlog.getValue("country_code"), "");
        Assert.hasLength(binlog.getValue("ext_kv"), "");
        Assert.hasLength(binlog.getValue("currency"), "");
        Assert.hasLength(binlog.getValue("total_fee"), "");
        Assert.hasLength(binlog.getValue("buyer_id"), "");
        Assert.hasLength(binlog.getValue("create_time"), "");
        Assert.hasLength(binlog.getValue("payment_time"), "");
    }

    private void afterParamsCheck(ClearingBillEntity entity) {
        Assert.notNull(entity.getCardGroup(), "");
        Assert.notNull(entity.getPayType(), "");
        Assert.isTrue(entity.getTradeAmount() > 0, "");
    }

    @Override
    public Map<String, String> buildExtra(Binlog binlog) {
        Map<String, String> map = new HashMap<>();
        map.put("tradeId", binlog.getValue("out_trade_id"));
        map.put("orderId", binlog.getValue("order_id"));
        map.put("userId", binlog.getValue("product_id"));
        map.put("userType", String.valueOf(UserTypeEnum.B.getValue()));
        map.put("tradeType", String.valueOf(UserTradeTypeEnum.PAY.getValue()));
        map.put("productType", String.valueOf(UserProductTypeEnum.PAY_IN.getValue()));
        map.put("countryCode", binlog.getValue("country_code"));
        map.put("currency", binlog.getValue("currency"));
        map.put("tradeAmount", binlog.getValue("amount"));
        map.put("status", String.valueOf(ClearStatusEnum.INIT.getStatus()));
        map.put("tradeCreateTime", binlog.getValue("create_time"));
        map.put("tradeFinishTime", binlog.getValue("pay_time"));

        ClearingBillEntity tmp = new ClearingBillEntity();
        try {
            handlePayDetail(binlog, tmp);
            map.put("payType", String.valueOf(tmp.getPayType()));
            map.put("cardGroup", String.valueOf(tmp.getCardGroup()));

            map.put("settleMode", "");
            SettlementConfigEntity config = settleConfigMapper.selectByUserIdAndProductType(Long.parseLong(binlog.getValue("product_id")), UserProductTypeEnum.PAY_IN.getValue());
            if (config != null) {
                map.put("settleMode", String.valueOf(config.getSettleMode()));
            }
        } catch (Exception e) {
            log.error("PayTableHandler.buildExtra error: {}", e);
        }
        return map;
    }
}
