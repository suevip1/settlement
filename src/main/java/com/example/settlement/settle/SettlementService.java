package com.example.settlement.settle;

import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.config.ConfigMaintainService;
import com.example.settlement.settle.infra.SettlementErrorNo;
import com.example.settlement.settle.model.event.SettleEventHandler;
import com.example.settlement.settle.model.event.SummaryStarted;
import com.example.settlement.settle.model.valueobj.DetailId;
import com.example.settlement.settle.model.valueobj.SettleModel;
import com.example.settlement.settle.model.valueobj.SummaryInfo;
import com.example.settlement.settle.model.valueobj.SummaryModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 09:30
 */
@Slf4j
@Service
public class SettlementService {

    @Resource
    private SettleConfigRepo configRepo;
    @Resource
    private SettleEventHandler eventHandler;

    @PostConstruct
    public void init() {
        log.info("SettlementService init");
    }

    public DetailId createDetailId(Long userId, Integer productType, Integer tradeType, Date transTime) {
        SummaryModel model = configRepo.getUserSummaryModel(userId);
        Date currentDate = new Date();
        Date recentStartAt = model.getStartAtTimeOfRecentPeriod(productType, currentDate);

        // 可能有重复的交易，需要根据场景判断
        // X天之前的交易，说明是补数据，把交易时间设置到当前时间
        if (transTime.before(recentStartAt)) {
            transTime = currentDate;
        }

        // X天之内的交易，但汇总详情单已经结算，把交易设置到当下，汇总到最新的详情单中
        SummaryInfo info = model.getDetailId(productType, tradeType, transTime);
        if (model.hasAttached2BillAndSettled(info)) {
            transTime = currentDate;
            info = model.getDetailId(productType, tradeType, transTime);
            if (model.hasAttached2BillAndSettled(info)) {
                throw new ErrorNoException(SettlementErrorNo.SUMMARY_DETAIL_HAS_SETTLED, "");
            }
        }

        if (info != null && info.detailId() != null) {
            return new DetailId("", info.detailId());
        }

        SummaryStarted summaryStarted = model.summaryInitialized(productType, tradeType, transTime);
        eventHandler.process(summaryStarted);
        return new DetailId("", summaryStarted.getDetailId());
    }
}
