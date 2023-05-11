package com.example.settlement.binlog;

import com.alibaba.fastjson.JSON;
import com.example.settlement.binlog.handler.TradeTableHandler;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.model.ClearService;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.stuck.ExecResult;
import com.example.settlement.stuck.StuckFlowAppService;
import io.netty.channel.ChannelOutboundBuffer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 清结算 binlog 消息处理器
 * @author yangwu_i
 * @date 2023/5/12 00:19
 */
@Slf4j
@Component
// @Gconsumer("binlogConsumer")
public class BinlogParser {

    @Resource
    private Map<String, TradeTableHandler> tableHandlerMap;
    @Resource
    private ClearService clearService;
    @Resource
    private StuckFlowAppService stuckFlowAppService;

    public boolean process(Object message, Object context) {
        log.info("BinlogParser.process message: {}, context: {}", message, context);
        Binlog binlog = parse(message);
        if (binlog == null) {
            return true; // 不重试
        }

        boolean result = processBinlog(binlog);
        if (result) {
            // 处理成功，取消延时消息
            return true; // 不重试
        } else {
            // binlog 消息不重试，发送延迟队列
            return false; // 重试
        }
    }

    private boolean processBinlog(Binlog binlog) {
        TradeTableHandler handler = null;
        try {
            // 获取对应表 Handler
            for (Map.Entry<String, TradeTableHandler> entry : tableHandlerMap.entrySet()) {
                if (entry.getValue().matchThis(binlog)) {
                    handler = entry.getValue();
                }
            }

            if (handler == null) {
                log.error("BinlogParser.processBinlog error: handler is null");
                return true;
            }

            if (!handler.needClearThis(binlog)) {
                log.info("BinlogParser.processBinlog needClearThis is false");
                return true;
            }

            ClearingBillEntity entity = handler.build(binlog);
            if (entity == null) {
                log.error("BinlogParser.processBinlog error: entity is null");
                // 异常流程
                doManageStuckClearFlow(null, handler.buildExtra(binlog));
                return true;
            }

            // 清算
            log.info("BinlogParser.processBinlog entity: {}", entity);
            return clearService.handle(entity);
        } catch (Exception e) {
            log.error("BinlogParser.processBinlog error: {}", e);
            if (handler != null) {
                doManageStuckClearFlow(e, handler.buildExtra(binlog));
            }
            return false;
        }
    }

    private void doManageStuckClearFlow(Exception e, Map<String, String> map) {
        long userId = NumberUtils.toLong(map.get("userId"), 0L);
        String tradeId = map.get("tradeId");
        int tradeType = NumberUtils.toInt(map.get("tradeType"), -1);
        String tradeContxt = JSON.toJSONString(map);
        int code;
        String msg;
        if (e instanceof ErrorNoException) {
            code = ((ErrorNoException) e).getErrorNo().getErrorNo();
            msg = ((ErrorNoException) e).getErrorNo().getErrorMsg();
        } else {
            code = ErrorNo.SERVER_ERROR.getErrorNo();
            msg = e == null ? "param parse error!" : e.getMessage();
        }
        ExecResult result = stuckFlowAppService.binlogParseStucked(userId, tradeId, tradeType, tradeContxt, code, msg);
        if (!result.isSuccess()) {
            log.error("BinlogParser.doManageStuckClearFlow error: {}", result);
        }
    }

    private Binlog parse(Object message) {
        try {
            return Binlog.parse(String.valueOf(message));
        } catch (Exception e) {
            log.error("BinlogParser.parse error: {}", e);
            return null;
        }
    }
}
