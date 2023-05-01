package com.example.settlement.settle.infra.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 20:34
 */
public interface ExecutorUtils {
    int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    // cpu 空闲时间与忙碌时间比
    int WC_RATE_BILL_GEN = 10 / 3;
    ExecutorService SETTLE_BILL_GEN = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT * (1 + WC_RATE_BILL_GEN), 10L,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(10240), new ThreadFactoryBuilder().setNameFormat("settle_bill_gen_pool_%d").build(),
            new ThreadPoolExecutor.AbortPolicy());
}
