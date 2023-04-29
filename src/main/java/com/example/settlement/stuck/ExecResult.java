package com.example.settlement.stuck;

import com.example.settlement.common.event.ErrorNoUnexpectedEvent;
import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.common.exceptions.ErrorNo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 10:49
 */
@Data
@AllArgsConstructor
public class ExecResult {
    private static final ExecResult SUCCESS = new ExecResult(ErrorNo.SUCCESS, "");

    private ErrorNo errorNo;
    private String debugMessage;

    public static ExecResult success() {
        return SUCCESS;
    }

    public boolean isSuccess() {
        return getErrorNo().isSuccess();
    }


    public static ExecResult error(ErrorNo errorNo, String debugMessage) {
        return new ExecResult(errorNo, debugMessage);
    }

    public static ExecResult error(UnexpectedEvent event) {
        if (event instanceof ErrorNoUnexpectedEvent) {
            ErrorNoUnexpectedEvent unexpected = (ErrorNoUnexpectedEvent) event;
            return new ExecResult(unexpected.errorNo(),unexpected.debugMsg());
        } else {
            return new ExecResult(ErrorNo.SERVER_ERROR, event.toString());
        }
    }
}
