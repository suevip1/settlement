package com.example.settlement.clear.infra.jobs;

import com.alibaba.fastjson.JSONObject;
import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import com.example.settlement.clear.infra.db.mapper.IClearingBillShardingMapper;
import com.example.settlement.clear.model.ClearService;
import com.example.settlement.clear.infra.enums.ClearStatusEnum;
import com.example.settlement.common.constant.CommonConstant;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 清分清算重试任务
 * @author yangwu_i
 * @date 2023/4/26 19:19
 */
@Slf4j
@Service
public class ClearingJob {

    private final static int MAX_TABLE_SHARDING = 999;
    private static final int PAGE_SIZE = 10;
    private static final int ONE_DAY = 1; // 默认扫描昨天的数据
    public static final String PARAM_DAY = "day"; // 任务参数名称
    private static final String TABLE_NAME = "clearing_bill";

    @Resource
    private IClearingBillShardingMapper clearingBillShardingMapper;
    @Resource
    private ClearService clearService;

    @XxlJob("clearingJobHandler")
    public void clearingJobHandler() {
        log.info("清分清算重试任务开始执行");

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        // 循环处理每张表
        for (int i = 0; i < MAX_TABLE_SHARDING; i++) {
            try {
                // 分片处理，每个机器处理自己的任务
                if (i % shardTotal == shardIndex) {
                    log.info("清分清算重试任务开始执行，当前处理的表为：{}，机器编号为：{}， 机器总数为：{}", i, shardIndex, shardTotal);

                    int pageNum = 1; // 页码
                    int startIndex = 0; // 分页起始条目
                    AtomicInteger successCount = new AtomicInteger(); // 表成功数
                    Date tradeFinishTime = getClearJobStartDate();
                    Long maxId = clearingBillShardingMapper.getMaxId(TABLE_NAME + CommonConstant.UNDERLINE + i);
                    if (maxId == null || maxId == 0) {
                        log.info("清分清算重试任务执行完成，当前处理的表为：{}，机器编号为：{}， 成功数为：{}", i, shardIndex, successCount);
                        continue;
                    }
                    // 分页循环处理
                    while (true) {
                        AtomicLong pageSucCount = new AtomicLong();
                        // 计算分页查询起始条目数
                        if (pageNum > 1) {
                            startIndex = (pageNum - 1) * PAGE_SIZE - 1;
                        }

                        // 分页查询未清算成功的清算数据
                        // todo: 分页查询 limit 原理，优化
                        List<ClearingBillEntity> list = clearingBillShardingMapper.selectShardingPageByStatus(TABLE_NAME + CommonConstant.UNDERLINE + i, ClearStatusEnum.SUCCESS.getStatus(), tradeFinishTime, maxId + 1, startIndex, PAGE_SIZE);
                        Date now = new Date();
                        list.stream().filter((e) -> e.getTradeFinishTime().compareTo(now) < 0).forEach(e -> {
                            boolean result = clearService.handle(e);
                            if (result) {
                                pageSucCount.getAndIncrement();
                                successCount.getAndIncrement();
                            }
                        });
                        if (list.isEmpty() || list.size() < PAGE_SIZE) {
                            // 最后一页跳出循环
                            break;
                        } else {
                            // todo: why?
                            // 当前页没有成功的数据则执行下一页，如果有成功数据由于更新数据导致分页数据变更，继续查询当前页
                            if (pageSucCount.get() == 0) {
                                pageNum++;
                            }
                        }
                    }
                    log.info("清分清算重试任务执行完成，当前处理的表为：{}，机器编号为：{}， 成功数为：{}", i, shardIndex, successCount);
                }
            } catch (Exception e) {
                log.error("清分清算重试任务执行异常: {}, table:{}", e, i);
            }
        }

    }

    // 获取任务处理数据的开始时间（扫描几天前的数据，默认扫描昨天的数据）
    private Date getClearJobStartDate() {
        return Date.from(ZonedDateTime.now().minusDays(getDay()).toInstant());
    }

    private int getDay() {
        try {
            String param = XxlJobHelper.getJobParam();
            if (!StringUtils.isEmpty(param)) {
                JSONObject json = JSONObject.parseObject(param);
                if (json.containsKey(PARAM_DAY) && json.getIntValue(PARAM_DAY) > 0) {
                    return json.getIntValue(PARAM_DAY);
                }
            }
            return ONE_DAY;
        } catch (Exception e) {
            return ONE_DAY;
        }
    }
}
