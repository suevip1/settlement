package com.example.settlement.stuck.infra.handler;

import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.db.mapper.IClearingBillMapper;
import com.example.settlement.clear.model.ClearService;
import com.example.settlement.common.enums.UserTradeTypeEnum;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.stuck.ExecResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


/**
 *
 * @author yangwu_i
 * @date 2023/5/9 21:49
 */
@Slf4j
@Service
public class ClearBillGatewayImpl implements ClearBillGateway {
    @Resource
    private ClearBillGateway clearBillGateway;
    @Resource
    private IClearingBillMapper clearBillMapper;

    @Resource
    private ClearService clearService;
    @Override
    public ExecResult continueClear(String tradeId, Integer tradeType, String tradeContext, String handleContext) {
        ClearingBillEntity bill = clearBillMapper.selectByTradeIdAndTradeType(tradeId, tradeType);
        try {
            if (bill == null) {
                bill = doCreateNewClearBill(tradeContext, handleContext);
            } else {
                bill = doModifyClearBill(bill, tradeContext, handleContext);
            }
            switch (UserTradeTypeEnum.valueOf(bill.getTradeType())) {
                case PAY:
                    doValidTradeTypeOfPay(bill);
                case REFUND:
                default: //其他交易类型不需检查
            }
            return doTriggerClear(bill);
        } catch (ErrorNoException e) {
            log.error("continueClear error", e);
            return ExecResult.error(e.getErrorNo(), e.getMessage());
        } catch (Exception f) {
            log.error("continueClear error", f);
            return ExecResult.error(ErrorNo.PARAM_ERROR, f.getMessage());
        }
    }

    private ExecResult doTriggerClear(ClearingBillEntity bill) {
        boolean result = clearService.handleWithoutCoverage(bill);
        return result ? ExecResult.success() : ExecResult.error(ErrorNo.SERVER_ERROR, "clearService.handleWithoutCoverage error");
    }

    private void doValidTradeTypeOfPay(ClearingBillEntity bill) {
    }

    private ClearingBillEntity doModifyClearBill(ClearingBillEntity bill, String tradeContext, String handleContext) {
        ClearingBillEntity tmp = doCreateNewClearBill(tradeContext, handleContext);
        if (tmp.getTradeId() != null) {
            bill.setTradeId(tmp.getTradeId());
        }
        if (tmp.getOriginTradeId() != null) {
            bill.setOriginTradeId(tmp.getOriginTradeId());
        }
        if (tmp.getOrderId() != null) {
            bill.setOrderId(tmp.getOrderId());
        }
        if (tmp.getOriginOrderId() != null) {
            bill.setOriginOrderId(tmp.getOriginOrderId());
        }
        if (tmp.getUserId() != null) {
            bill.setUserId(tmp.getUserId());
        }
        if (tmp.getUserType() != null) {
            bill.setUserType(tmp.getUserType());
        }
        if (tmp.getTradeType() != null) {
            bill.setTradeType(tmp.getTradeType());
        }
        if (tmp.getOriginTradeType() != null) {
            bill.setOriginTradeType(tmp.getOriginTradeType());
        }
        if (tmp.getProductType() != null) {
            bill.setProductType(tmp.getProductType());
        }
        if (tmp.getPayType() != null) {
            bill.setPayType(tmp.getPayType());
        }
        if (tmp.getCardGroup() != null) {
            bill.setCardGroup(tmp.getCardGroup());
        }
        if (tmp.getCountryCode() != null) {
            bill.setCountryCode(tmp.getCountryCode());
        }
        if (tmp.getCurrency() != null) {
            bill.setCurrency(tmp.getCurrency());
        }
        if (tmp.getCurrency() != null) {
            bill.setCurrency(tmp.getCurrency());
        }
        if (tmp.getTradeAmount() != null) {
            bill.setTradeAmount(tmp.getTradeAmount());
        }
        if (tmp.getOriginTradeAmount() != null) {
            bill.setOriginTradeAmount(tmp.getOriginTradeAmount());
        }
        if (tmp.getStatus() != null) {
            bill.setStatus(tmp.getStatus());
        }
        if (tmp.getTradeCreateTime() != null) {
            bill.setTradeCreateTime(tmp.getTradeCreateTime());
        }
        if (tmp.getTradeFinishTime() != null) {
            bill.setTradeFinishTime(tmp.getTradeFinishTime());
        }
        if (tmp.getSettleMode() != null) {
            bill.setSettleMode(tmp.getSettleMode());
        }
        return bill;
    }

    private ClearingBillEntity doCreateNewClearBill(String tradeContext, String handleContext) {
        // 解析 tradeContext
        Map<String, String> tmp = JsonMapper.builder().build().convertValue(tradeContext, Map.class);
        tmp.putAll(JsonMapper.builder().build().convertValue(handleContext, Map.class));

        ClearingBillEntity entity = ClearingBillEntity.builder()
                .settleMode(toInt(tmp, "settleMode"))
                .tradeId(toString(tmp, "tradeId"))
                .originTradeId(toString(tmp, "originTradeId"))
                .orderId(toString(tmp, "orderId"))
                .originOrderId(toString(tmp, "originTradeId"))
                .userId(toLong(tmp, "userId"))
                .userType(toInt(tmp, "userType"))
                .originTradeType(toInt(tmp, "originTradeType"))
                .productType(toInt(tmp, "productType"))
                .payType(toInt(tmp, "payType"))
                .cardGroup(toInt(tmp, "cardGroup"))
                .countryCode(toString(tmp, "countryCode"))
                .currency(toString(tmp, "currency"))
                .tradeAmount(toLong(tmp, "tradeAmount"))
                .originTradeAmount(toLong(tmp, "originTradeAmount"))
                .status(toInt(tmp, "status"))
                .tradeCreateTime(toDate(tmp, "tradeCreateTime"))
                .tradeFinishTime(toDate(tmp, "tradeFinishTime"))
                .createTime(new Date())
                .modifyTime(new Date())
                .build();
        return entity;
    }

    private Date toDate(Map<String, String> binlog, String field) {
        if (binlog.get(field) == null) {
            return null;
        }
        try {
            return new Date(Long.parseLong(binlog.get(field)));
        } catch (Exception e) {
            log.error("toDate error", e);
            return null;
        }
    }

    private Long toLong(Map<String, String> binlog, String field) {
        if (binlog.get(field) == null) {
            return null;
        }
        try {
            return Long.valueOf(binlog.get(field));
        } catch (Exception e) {
            log.error("toLong error", e);
            return null;
        }
    }

    private String toString(Map<String, String> binlog, String field) {
        return binlog.get(field);
    }

    private Integer toInt(Map<String, String> binlog, String field) {
        if (binlog.get(field) == null) {
            return null;
        }
        try {
            return Integer.valueOf(binlog.get(field));
        } catch (Exception e) {
            log.error("toInt error", e);
            return null;
        }
    }
}
