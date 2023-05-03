package com.example.settlement.settle.model.event;

import com.example.settlement.common.event.ExpectedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.expression.spel.ast.Literal;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/3 21:50
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SettleBillBound implements ExpectedEvent {
    private Long userId;
    private String settleId;
    private List<String> detailIds;
    private Integer version; // 乐观锁
}
