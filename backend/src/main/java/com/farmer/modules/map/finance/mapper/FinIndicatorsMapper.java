package com.farmer.modules.map.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmer.modules.map.finance.entity.FinIndicators;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FinIndicatorsMapper extends BaseMapper<FinIndicators> {

    @Select("SELECT * FROM fin_indicators WHERE region_id = #{regionId} " +
            "AND stat_month = #{statMonth} AND deleted = 0")
    List<FinIndicators> selectByRegionAndMonth(@Param("regionId") Long regionId,
                                               @Param("statMonth") String statMonth);
}
