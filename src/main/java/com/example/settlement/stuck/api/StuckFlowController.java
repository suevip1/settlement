package com.example.settlement.stuck.api;

import com.alibaba.fastjson2.JSON;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.stuck.ExecResult;
import com.example.settlement.stuck.StuckFlowAppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author yangwu_i
 * @date 2023/5/11 23:24
 */
@Slf4j
@RequestMapping("/stuckflow")
@RestController
public class StuckFlowController {
    @Resource
    private StuckFlowAppService stuckFlowAppService;

    @PostMapping("/modify")
    public ExecResult modifyFlowData(@RequestParam String tradeId, @RequestParam Integer tradeType, @RequestParam String handler, @RequestParam String handleMessage, @RequestParam String handleContext) {
        if (StringUtils.isBlank(tradeId) || tradeType <= -1 || StringUtils.isBlank(handler) || StringUtils.isBlank(handleMessage) || StringUtils.isBlank(handleContext)) {
            return ExecResult.error(ErrorNo.PARAM_ERROR, "参数错误");
        }
        if (JSON.toJSONString(handleContext).isBlank()) {
            return ExecResult.error(ErrorNo.PARAM_ERROR, "handleContext参数错误");
        }
        return stuckFlowAppService.modifyFlowData(tradeId, tradeType, handler, handleMessage, handleContext);
    }

    @PostMapping("/trigger")
    public ExecResult trigger(@RequestParam String tradeId, @RequestParam Integer tradeType) {
        if (StringUtils.isBlank(tradeId) || tradeType <= -1) {
            return ExecResult.error(ErrorNo.PARAM_ERROR, "参数错误");
        }
        return stuckFlowAppService.triggerRetry(tradeId, tradeType);
    }

    @PostMapping("/mrigger")
    public ExecResult modifyAndTrigger(@RequestParam String tradeId, @RequestParam Integer tradeType, @RequestParam String handler, @RequestParam String handleMessage, @RequestParam String handleContext) {
        if (StringUtils.isBlank(tradeId) || tradeType <= -1 || StringUtils.isBlank(handler) || StringUtils.isBlank(handleMessage) || StringUtils.isBlank(handleContext)) {
            return ExecResult.error(ErrorNo.PARAM_ERROR, "参数错误");
        }
        if (JSON.toJSONString(handleContext).isBlank()) {
            return ExecResult.error(ErrorNo.PARAM_ERROR, "handleContext参数错误");
        }

        ExecResult result = stuckFlowAppService.modifyFlowData(tradeId, tradeType, handler, handleMessage, handleContext);
        if (!result.isSuccess()) {
            return result;
        }
        return stuckFlowAppService.triggerRetry(tradeId, tradeType);
    }

    @PostMapping("/ignore")
    public ExecResult ignoreFlow(@RequestParam String tradeId, @RequestParam Integer tradeType, @RequestParam String handler, @RequestParam String handleMessage) {
        if (StringUtils.isBlank(tradeId) || tradeType <= -1 || StringUtils.isBlank(handler) || StringUtils.isBlank(handleMessage)) {
            return ExecResult.error(ErrorNo.PARAM_ERROR, "参数错误");
        }
        return stuckFlowAppService.maskAsIgnored(tradeId, tradeType, handler, handleMessage);
    }
}
