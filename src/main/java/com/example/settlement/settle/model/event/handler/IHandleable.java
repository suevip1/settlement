package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.event.UnexpectedEvent;

/**
 * 结算处理器
 * @author yangwu_i
 * @date 2023/5/3 16:33
 */
public interface IHandleable<T> {
    UnexpectedEvent handle(T obj);
    IHandleable<T> setNext(IHandleable<T> next);
}
