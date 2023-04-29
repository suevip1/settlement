package com.example.settlement.common.event;

import com.example.settlement.common.exceptions.ErrorNo;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 10:57
 */
public record ErrorNoUnexpectedEvent(ErrorNo errorNo, String debugMsg) implements UnexpectedEvent {
}
