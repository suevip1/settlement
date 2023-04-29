package com.example.settlement.stuck.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 14:29
 */
@AllArgsConstructor
@Getter
public enum StuckFlowStateEnum {
    Receiving("异常清算流水，分析 stuck 原因", 0),
    AutoRetring("自动重试中", 10),
    RetryFailed("自动重试失败", 11),
    Completed("处理完成", 20),
    WaitManualProcess("等待人工处理", 30),
    ManualChangeData("数据处理变换完成", 31),
    ManualMarked("人工标记异常忽略", 32),
    ;

    private String desc;
    private int code;
}
