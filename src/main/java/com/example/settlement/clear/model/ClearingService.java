package com.example.settlement.clear.model;

import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 清分服务
 * @author yangwu_i
 * @date 2023/4/26 21:02
 */
@Slf4j
@Service
public class ClearingService {

    public boolean handle(ClearingBillEntity entity) {
        return true;
    }
}
