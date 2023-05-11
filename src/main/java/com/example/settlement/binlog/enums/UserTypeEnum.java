package com.example.settlement.binlog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 00:53
 */
@AllArgsConstructor
@Getter
public enum UserTypeEnum {
    B(1, "B端用户");
    private int value;
    private String desc;
}
