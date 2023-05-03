package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/3 17:17
 */
@Setter
@Getter
@NoArgsConstructor
public class SettleClearCompleted implements ExpectedEvent {
    private int detailCount; // 明细条数
    private Long userId;
    private String configId;
    private String settleId;
    private List<String> detailIds; // 已处理的汇总详情单号
    private Date settleTime;
    private Date liquidEntTime;
    private Long totalTradeAmount;
    private Long totalTradeCount;
    private Long totalTradeNetAmount;
    private Long totalTradeNetCount;
    private Long totalFeeAmount; // 总的手续费
    private Long totalFeeCount; // 手续费收费笔数

    private Long totalProcessedTradeFeeAmount;
    private Long totalUnProcessedTradeFeeAmount;
    private Long totalProcessedInstallmentFeeAmount;
    private Long totalUnProcessedInstallmentFeeAmount;
    private Long totalProcessedTaxFeeAmount;
    private Long totalUnProcessedTaxFeeAmount;
    private Long totalSettledNetAmount;
    private Long totalUnSettledNetAmount;

    private String currency;
    private Integer version;

}
