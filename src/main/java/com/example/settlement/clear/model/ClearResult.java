package com.example.settlement.clear.model;

import com.example.settlement.common.exceptions.ErrorNo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author yangwu_i
 * @date 2023/4/26 23:00
 */
@Data
@AllArgsConstructor
public class ClearResult {
    private int code;
    private String msg;

    public boolean isSuccess() {
        return code == ErrorNo.SUCCESS.getErrorNo();
    }

    public static ClearResult buildSuccessResult() {
        return new ClearResult(ErrorNo.SUCCESS.getErrorNo(), ErrorNo.SUCCESS.getErrorMsg());
    }

    public static ClearResult buildFailResult(ErrorNo errorNo) {
        return new ClearResult(errorNo.getErrorNo(), errorNo.getErrorMsg());
    }
}
