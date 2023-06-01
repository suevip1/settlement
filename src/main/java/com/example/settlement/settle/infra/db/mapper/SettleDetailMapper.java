package com.example.settlement.settle.infra.db.mapper;

import com.example.settlement.settle.infra.db.entity.SettleDetailEntity;
import com.example.settlement.settle.model.valueobj.DetailInfo;
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

    List<SettleDetailEntity> selectDetails(Long userId, String settleId);

    List<SettleDetailEntity> selectUnbindDetails(Long userId, Integer userProduct);

    int updateSettleId(Long userId, String settleId, List<String> detailIds);

    int updateStateOfDetails(Long userId, String settleId, List<String> detailIds, int status);

    SettleDetailEntity selectOne(Long userId, Integer productType, Integer tradeType, Date summaryTime);
}
