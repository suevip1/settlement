package com.example.settlement.charge.domain.feecomputer;

import com.example.settlement.charge.domain.feecharge.FeeCalItem;
import com.example.settlement.charge.domain.feecharge.TradeInfo;
import com.example.settlement.charge.domain.feecomputer.feecomputer.IFeeComputer;
import com.example.settlement.charge.domain.feecomputer.feeitem.FeeConfig;
import com.example.settlement.charge.domain.feecomputer.match.TradeMatcher;
import org.springframework.util.Assert;

/**
 * 收费子项计费模型
 * @author yangwu_i
 * @date 2023/4/10 23:08
 */
public record SubitemComputer(
        FeeConfig feeConfig,
        TradeMatcher tradeMatcher,
        IFeeComputer feeComputer
) {
    public boolean match(TradeInfo info) {
        return tradeMatcher.match(info);
    }

    public FeeCalItem charge(TradeInfo info) {
        Assert.notNull(info, "tradeInfo can not be null");
        Long amount = feeComputer.computeFee(info.tradeAmount());
        return new FeeCalItem(feeConfig, amount, info.tradeCurrency());
    }
}
