package com.example.settlement.config.mapper;

import com.example.settlement.config.entity.ChargeConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/29 17:22
 */
@Mapper
// @Master
public interface ChargeConfigMapper {
    List<ChargeConfigEntity> selectActiveConfigsByUserId(Long userId);

    void insertSelective(ChargeConfigEntity chargeConfig);
}
