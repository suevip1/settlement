package com.example.settlement.charge.domain.feecharge;

import com.example.settlement.charge.domain.feecomputer.SubitemComputer;
import com.example.settlement.charge.domain.feecomputer.match.TradeMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 交易收费模型
 * @author yangwu_i
 * @date 2023/4/8 21:10
 */
public class ChargeCalculator {

    // 前置收费，只收取交易手续费，税费和净额等依赖交易手续费的费项在上层调用函数内计算
    public List<FeeCalItem> calculate(TradeInfo info) {
        GroupKey groupKey = new GroupKey(info.userProduct(), info.userTradeType(), info.userPayType());
        List<SubitemComputer> computers = feeComputers.get(groupKey);
        if (computers == null) {
            return new ArrayList<>();
        }
        // 计算分项
        List<FeeCalItem> feeList = computers.stream().filter(e -> e.match(info)).map(c -> c.charge(info)).collect(Collectors.toList());

        // 验证
        feeList.stream().collect(Collectors.groupingBy(e -> e.config().feeCode())).forEach((k, v) -> {
            if (v.size() > 1) {
                throw new RuntimeException("重复的费用类型：" + k);
            }
        });
        return feeList;
    }

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
