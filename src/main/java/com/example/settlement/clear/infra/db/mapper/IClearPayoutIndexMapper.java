package com.example.settlement.clear.infra.db.mapper;

import com.example.settlement.clear.infra.db.entity.ClearPayoutIndexEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 17:06
 */
@Mapper
public interface IClearPayoutIndexMapper {

    int insert(ClearPayoutIndexEntity idx);
}
