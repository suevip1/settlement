package com.example.settlement.config;

import com.example.settlement.config.entity.SettlementConfigEntity;
import com.example.settlement.config.mapper.SettlementConfigMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 10:07
 */
@Slf4j
@Service
public class ConfigMaintainService {
    @Resource
    private SettlementConfigMapper settlementConfigMapper;

    public List<SettlementConfigEntity> getSettleConfig(Long userId) {
        return settlementConfigMapper.selectByUserId(userId);
    }
}
