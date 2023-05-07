package com.example.settlement.settle.model.event.handler;

import com.example.settlement.common.event.UnexpectedEvent;

/**
 *
 * @author yangwu_i
 * @date 2023/5/7 17:42
 */
public abstract class AbstractHandler<T> implements IHandleable<T> {
    protected IHandleable<T> next;
    public abstract UnexpectedEvent handle(T obj);
    public IHandleable<T> setNext(IHandleable<T> next) {
        this.next = next;
        return this;
    }
}
