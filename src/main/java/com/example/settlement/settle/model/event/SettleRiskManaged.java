package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import com.example.settlement.settle.model.valueobj.DetailInfo;
import lombok.*;
import org.checkerframework.checker.signature.qual.BinaryName;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/3 21:41
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettleRiskManaged implements ExpectedEvent {
    private Long userId;
    private String settleId;
    private List<DetailInfo> details;
    private Integer version; // settle bill 记录版本号
}
