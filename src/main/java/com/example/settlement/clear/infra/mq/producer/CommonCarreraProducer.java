package com.example.settlement.clear.infra.mq.producer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 清结算 binlog 延迟消息 producer
 * @author yangwu_i
 * @date 2023/4/29 10:18
 */
@Slf4j
@Service
public class CommonCarreraProducer implements ICarreraProducer {

    @Resource
    // public RocketMQProducer rocketMQProducer;
    @Override
    public boolean sendMessage(String topic, String key, String body) {
        try {
            // return rocketMQProducer.send(topic, key, body);
            return true;
        } catch (Exception e) {
            log.error("发送消息异常：{}，topic：{}，key：{}，body：{}", e, topic, key, body);
            return false;
        }
    }

    @Override
    public boolean sendDelayMessage(String topic, String body, long fireTime) {
        return false;
    }

    @Override
    public boolean sendFixedIntervalCyclicMessage(String topic, String body, long fireTime, long interval, long times, long validityPeriod) {
        return false;
    }
}
