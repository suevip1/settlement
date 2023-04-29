package com.example.settlement.clear.infra.mq;

import com.alibaba.fastjson2.JSON;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.enums.ClearStatusEnum;
import com.example.settlement.clear.infra.mq.producer.CommonCarreraProducer;
import com.example.settlement.common.enums.SettleModeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.settlement.clear.infra.constant.Constant.REALTIME_SETTLEMENT_CALLBACK_TOPIC;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 09:17
 */
@Slf4j
@Service
public class CallbackNotifyService {
    @Resource
    private CommonCarreraProducer commonCarreraProducer;
    private Set<Integer> realtimeSettleSuccessStatusList = Set.of(
            ClearStatusEnum.REALTIME_SETTLE_NET_TALLY.getStatus(),
            ClearStatusEnum.ACCUMULATE.getStatus(),
            ClearStatusEnum.SUCCESS.getStatus()
    );

    public void notifyRealTimeSettlementResult(ClearingBillEntity entity) {
        if (SettleModeEnum.REAL_TIME.getValue() == entity.getSettleMode() &&
            realtimeSettleSuccessStatusList.contains(entity.getStatus())) {
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("merchant_id", entity.getUserId());
            dataMap.put("order_id", entity.getOrderId());
            dataMap.put("out_trade_id", entity.getTradeId());
            dataMap.put("trade_type", entity.getTradeType());
            dataMap.put("country_code", entity.getCountryCode());
            dataMap.put("settle_mode", entity.getSettleMode());
            dataMap.put("amount", entity.getTradeAmount());
            dataMap.put("currency", entity.getCurrency());
            dataMap.put("settle_status", "SUCCESS");
            dataMap.put("settle_finished_time", entity.getModifyTime());
            commonCarreraProducer.sendMessage(REALTIME_SETTLEMENT_CALLBACK_TOPIC, "", JSON.toJSONString(dataMap));
        }
    }

    public static void main(String[] args) {
        Map<String, Object> data = new HashMap<>();
        data.put("a", "aaa");
        data.put("b", 11);
        data.put("c", List.of(1, 2, 3));
        Map<String, Object> innerData = new HashMap<>();
        innerData.put("name", "yagnwu");
        innerData.put("age", 12);
        data.put("d", innerData);
        String s = JSON.toJSONString(data);
        System.out.println(s);
    }
}
