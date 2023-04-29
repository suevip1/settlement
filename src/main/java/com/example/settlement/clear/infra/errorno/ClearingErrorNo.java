package com.example.settlement.clear.infra.errorno;

import com.example.settlement.common.exceptions.ErrorNo;

/**
 *
 * @author yangwu_i
 * @date 2023/4/26 22:31
 */
public interface ClearingErrorNo {
    ErrorNo CLEAR_PROCESS_INSERT_ENTITY_ERROR = new ErrorNo(1, "清分处理插入实体错误");
    ErrorNo CLEAR_PROCESS_STATUS_ERROR = new ErrorNo(2, "清分处理状态错误");
}
