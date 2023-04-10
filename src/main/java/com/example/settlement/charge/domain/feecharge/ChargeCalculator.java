package com.example.settlement.charge.domain.feecharge;

import com.example.settlement.charge.domain.feecomputer.SubitemComputer;
import com.example.settlement.charge.domain.feecomputer.match.TradeMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易收费模型
 * @author yangwu_i
 * @date 2023/4/8 21:10
 */
public class ChargeCalculator {

    private record GroupKey(Integer productType, Integer tradeType, Integer payType) {
        public static GroupKey of(TradeMatcher tradeMatcher) {
            return new GroupKey(tradeMatcher.productType(), tradeMatcher.tradeType(), tradeMatcher.payType());
        }
    }
    private final Map<GroupKey, List<SubitemComputer>> feeComputers = new HashMap<>();

    public void addFeeComputer(SubitemComputer subitemComputer) {
        GroupKey key = GroupKey.of(subitemComputer.tradeMatcher());
        if (!feeComputers.containsKey(key)) {
            feeComputers.put(key, new ArrayList<>());
        }
        feeComputers.get(key).add(subitemComputer);
    }

}
