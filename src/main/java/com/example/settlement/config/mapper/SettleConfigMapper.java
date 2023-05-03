package com.example.settlement.config.mapper;

import com.example.settlement.config.entity.SettlementConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 10:13
 */
@Mapper
// @Master
public interface SettleConfigMapper {

    List<SettlementConfigEntity> selectByUserId(Long userId);

    List<Long> selectActiveUsers();

    SettlementConfigEntity selectConfig(Long userId, int productType);
}
