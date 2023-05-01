package com.example.settlement.clear.infra.db.mapper;

import com.example.settlement.clear.infra.db.entity.ClearIndexEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 15:51
 */
@Mapper
public interface IClearIndexMapper {
    List<ClearIndexEntity> selectBy(String tradeId, Integer tradeType);

    int insert(ClearIndexEntity idx);
}
