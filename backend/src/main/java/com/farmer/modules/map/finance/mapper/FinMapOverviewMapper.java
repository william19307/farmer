package com.farmer.modules.map.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmer.modules.map.finance.entity.FinMapOverview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FinMapOverviewMapper extends BaseMapper<FinMapOverview> {

    @Select("SELECT * FROM fin_map_overview WHERE region_id = #{regionId} " +
            "AND stat_month = #{statMonth} AND deleted = 0 LIMIT 1")
    FinMapOverview selectByRegionAndMonth(@Param("regionId") Long regionId,
                                          @Param("statMonth") String statMonth);
}
