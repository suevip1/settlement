package com.example.settlement.settle.infra.db.mapper;

import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 11:42
 */
@Mapper
// todo:
public interface SettleDetailMapper {

    List<SettleDetailEntity> selectRecentDetails(Long userId, @Param("summaryTime") Date startAtTimeOfRecentPeriod);

    int insertSelective(SettleDetailEntity entity);

    // 累计费项
    int updateFee(@Param("entity") SettleDetailEntity settleDetailEntity, @Param("state") int status);
}
