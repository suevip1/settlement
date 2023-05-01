package com.example.settlement.settle.model.valueobj;

import java.util.Date;

/**
 * （天，产品） -> 结算单
 * @author yangwu_i
 * @date 2023/5/1 11:03
 */
public record SettleId(
        Date date,
        Integer userProduct) {
}
