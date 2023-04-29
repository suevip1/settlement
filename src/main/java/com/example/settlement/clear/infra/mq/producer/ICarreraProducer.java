package com.example.settlement.clear.infra.mq.producer;

/**
 * binlog 延迟消息 producer
 * @author yangwu_i
 * @date 2023/4/29 10:13
 */
public interface ICarreraProducer {
    // 实时消息
    boolean sendMessage(String topic, String key, String body);

    /**
     * 一次性延迟消息
     * @param topic
     * @param body
     * @param fireTime 毫秒
     * @return
     */
    boolean sendDelayMessage(String topic, String body, long fireTime);

    /**
     * 固定间隔循环消息，自定义策略
     * @param topic
     * @param body
     * @param fireTime 首次触发时间
     * @param interval 触发间隔
     * @param times    循环次数
     * @param validityPeriod  有效时期
     * @return
     */
    boolean sendFixedIntervalCyclicMessage(String topic, String body, long fireTime, long interval, long times, long validityPeriod);
}
