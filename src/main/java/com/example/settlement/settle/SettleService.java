package com.example.settlement.settle;

import com.example.settlement.common.event.UnexpectedEvent;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.settle.infra.SettleErrorNo;
import com.example.settlement.settle.model.domain.SettleBindModel;
import com.example.settlement.settle.model.domain.SettleModel;
import com.example.settlement.settle.model.domain.SummaryModel;
import com.example.settlement.settle.model.event.SettleBillInited;
import com.example.settlement.settle.model.event.SettleBindStarted;
import com.example.settlement.settle.model.event.SettleEventHandler;
import com.example.settlement.settle.model.event.SummaryStarted;
import com.example.settlement.settle.model.valueobj.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 09:30
 */
@Slf4j
@Service
public class SettleService {

    @Resource
    private SettleConfigRepo configRepo;
    @Resource
    private SettleEventHandler eventHandler;
    @Resource
    private SettleQueryRepo settleQueryRepo;

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
                throw new ErrorNoException(SettleErrorNo.SUMMARY_DETAIL_HAS_SETTLED, "");
            }
        }

        if (info != null && info.detailId() != null) {
            return new DetailId("", info.detailId());
        }

        SummaryStarted summaryStarted = model.summaryInitialized(productType, tradeType, transTime);
        eventHandler.process(summaryStarted);
        return new DetailId("", summaryStarted.getDetailId());
    }

    public SettleId createSettleId(Long userId, Integer userProduct, Date transTime) {
        SettleModel model = configRepo.getUserSettleModel(userId);
        // 从缓存中获取当前结算单
        String settleId = model.getSettleId(userProduct, transTime);
        if (settleId != null) {
            return new SettleId(settleId);
        }
        // 生成当前结算单
        Pair<SettleBillInited, UnexpectedEvent> result = model.billInited(userProduct, transTime);
        if (result.getLeft() != null) {
            eventHandler.process(result.getLeft());
            return new SettleId(result.getLeft().getSettleId());
        } else {
            log.error("createSettleId failed, userId={}, userProduct={}, transTime={}", userId, userProduct, transTime);
            return new SettleId(null);
        }
    }

    public Pair<Boolean, UnexpectedEvent> bindSummaryDetail(Long userId, String settleId) {
        SettleBindModel model = settleQueryRepo.getSettleBindModel(userId, settleId);
        List<DetailInfo> detailInfoList = settleQueryRepo.getUnbindDetails(userId, model.getEntity().getSettleType());
        detailInfoList = detailInfoList.stream()
                .filter(detailInfo -> detailInfo.getSummaryTime().before(model.getEntity().getLiquidEndTime())).toList();
        if (CollectionUtils.isNotEmpty(detailInfoList)) {
            Pair<SettleBindStarted, ? extends UnexpectedEvent> result = model.startBindDetails(detailInfoList);
            if (result.getLeft() == null) {
                return Pair.of(false, result.getRight());
            }
            eventHandler.process(result.getLeft());
            model.refresh(result.getLeft());
        }
        return Pair.of(true, null);
    }

}
