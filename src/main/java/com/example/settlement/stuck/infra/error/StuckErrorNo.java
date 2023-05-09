package com.example.settlement.stuck.infra.error;

import com.example.settlement.common.exceptions.ErrorNo;

/**
 *
 * @author yangwu_i
 * @date 2023/5/8 22:06
 */
public interface StuckErrorNo {

    ErrorNo STUCK_NOT_FOUND = new ErrorNo(1001, "stuck not found");
}
