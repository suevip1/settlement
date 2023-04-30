package com.example.settlement.config;

import com.example.settlement.config.entity.ChargeConfigEntity;
import com.example.settlement.config.mapper.ChargeConfigMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 17:12
 */
@Service
public class ConfigQueryService {

    @Resource
    private ChargeConfigMapper configMapper;

    public List<ChargeConfigEntity> selectActiveConfigsByUserId(Long userId) {
        return configMapper.selectActiveConfigsByUserId(userId);
    }
}
