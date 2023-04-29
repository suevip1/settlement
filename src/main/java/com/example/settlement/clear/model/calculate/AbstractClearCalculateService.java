package com.example.settlement.clear.model.calculate;

import com.example.settlement.clear.model.ClearResult;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 16:01
 */
public abstract class AbstractClearCalculateService<E> implements IClearCalculateService<E> {

    @Override
    public ClearResult calculate(E entity) {
        ClearResult result = preCheck(entity);
        if (!result.isSuccess()) {
            return result;
        }
        return doCalculate(entity);
    }

    protected abstract ClearResult doCalculate(E entity);
    protected abstract ClearResult preCheck(E entity);
}
