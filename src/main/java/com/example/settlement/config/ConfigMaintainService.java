package com.example.settlement.config;

import com.example.settlement.config.entity.SettlementConfigEntity;
import com.example.settlement.config.mapper.SettleConfigMapper;
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
    private SettleConfigMapper settleConfigMapper;

    public List<SettlementConfigEntity> getSettleConfig(Long userId) {
        return settleConfigMapper.selectByUserId(userId);
    }

    public List<Long> selectActiveUsers() {
       return settleConfigMapper.selectActiveUsers();
    }
}
