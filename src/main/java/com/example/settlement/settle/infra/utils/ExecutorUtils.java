package com.example.settlement.settle.infra.utils;

import com.example.settlement.settle.infra.job.SettleBillBindHandler;
import com.example.settlement.settle.infra.job.SettleProcessHandler;
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

    int WC_RATE_BILL_BIND = 10 / 3;
    ExecutorService SETTLE_BILL_BIND_DETAIL = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT * (1 + WC_RATE_BILL_BIND), 10L,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(10240), new ThreadFactoryBuilder().setNameFormat("settle_bill_bind_detail_pool_%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

    int WC_RATE_BILL_TRY_FINISH = 10 / 3;
    ExecutorService SETTLE_BILL_TRY_FINISH = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT * (1 + WC_RATE_BILL_TRY_FINISH), 10L,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(10240), new ThreadFactoryBuilder().setNameFormat("settle_bill_try_finish_pool_%d").build(),
            new ThreadPoolExecutor.AbortPolicy());
}
