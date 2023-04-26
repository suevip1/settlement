package com.example.settlement.clear.infra.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 清分表实体
 * @author yangwu_i
 * @date 2023/4/26 19:57
 */
@Data
@AllArgsConstructor
public class ClearingBillEntity implements Serializable {

    private Date tradeFinishTime;
}
