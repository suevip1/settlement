package com.example.settlement.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 带有错误码的异常
 * @author yangwu_i
 * @date 2023/4/26 22:18
 */
@Data
public class ErrorNoException extends RuntimeException {
    private ErrorNo errorNo;
    private String debugMsg;
    private Object data;

    public ErrorNoException(ErrorNo errorNo, String debugMsg) {
        super(errorNo.getErrorMsg() + ", debugMsg: " + debugMsg);
        this.errorNo = errorNo;
        this.debugMsg = debugMsg;
    }

    public ErrorNoException(ErrorNo errorNo, String debugMsg, Object data) {
        super(errorNo.getErrorMsg() + ", debugMsg: " + debugMsg);
        this.errorNo = errorNo;
        this.debugMsg = debugMsg;
        this.data = data;
    }
}
