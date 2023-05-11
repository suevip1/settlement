package com.example.settlement.stuck.infra.error;

import com.example.settlement.common.exceptions.ErrorNo;

/**
 *
 * @author yangwu_i
 * @date 2023/5/8 22:06
 */
public interface StuckErrorNo {

    ErrorNo STUCK_NOT_FOUND = new ErrorNo(1001, "stuck not found");
    ErrorNo NOT_ILLEGAL_STATE = new ErrorNo(1002, "not illegal state");
    ErrorNo NOT_SUPPORTED_TYPE = new ErrorNo(1003, "not supported type");
}
