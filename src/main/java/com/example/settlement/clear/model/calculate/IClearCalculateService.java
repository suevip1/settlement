package com.example.settlement.clear.model.calculate;

import com.example.settlement.clear.model.ClearResult;

/**
 * 清分接口
 * @author yangwu_i
 * @date 2023/4/29 16:00
 */
public interface IClearCalculateService<E> {
    ClearResult calculate(E entity);
}
