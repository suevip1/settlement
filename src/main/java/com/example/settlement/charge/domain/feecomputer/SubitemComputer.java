package com.example.settlement.charge.domain.feecomputer;

import com.example.settlement.charge.domain.feecomputer.feecomputer.IFeeComputer;
import com.example.settlement.charge.domain.feecomputer.feeitem.FeeItemConfig;
import com.example.settlement.charge.domain.feecomputer.match.TradeMatcher;

/**
 * 收费子项计费模型
 * @author yangwu_i
 * @date 2023/4/10 23:08
 */
public record SubitemComputer(
        FeeItemConfig feeConfig,
        TradeMatcher tradeMatcher,
        IFeeComputer feeComputer
) {
}
