package com.example.settlement.clear.infra.db.mapper;

import com.example.settlement.clear.infra.db.entity.ClearingBillEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/26 19:32
 */
// @Master todo: 实现 mybatis 主从分离
@Mapper
public interface IClearingBillShardingMapper {

    Long getMaxId(@Param(("tableName")) String tableName);

    List<ClearingBillEntity> selectShardingPageByStatus(@Param("tableName") String tableName,
                                                        @Param("status") int status,
                                                        @Param("tradeFinishTime") Date tradeFinishTime,
                                                        @Param("id") Long id,
                                                        @Param("startIndex") int startIndex,
                                                        @Param("pageSize") int pageSize);
}
