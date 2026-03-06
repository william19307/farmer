package com.farmer.modules.map.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmer.modules.map.finance.entity.FinVillageIncome;
import com.farmer.modules.map.finance.vo.IncomeThresholdVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface FinVillageIncomeMapper extends BaseMapper<FinVillageIncome> {

    @Select("SELECT COUNT(*) FROM fin_village_income WHERE region_id = #{regionId} " +
            "AND stat_month = #{statMonth} AND total_income > #{threshold} AND deleted = 0")
    Long countAboveThreshold(@Param("regionId") Long regionId,
                             @Param("statMonth") String statMonth,
                             @Param("threshold") BigDecimal threshold);

    @Select("SELECT COUNT(*) FROM fin_village_income WHERE region_id = #{regionId} " +
            "AND stat_month = #{statMonth} AND total_income < #{threshold} AND deleted = 0")
    Long countBelowThreshold(@Param("regionId") Long regionId,
                             @Param("statMonth") String statMonth,
                             @Param("threshold") BigDecimal threshold);

    @Select("SELECT COUNT(*) FROM fin_village_income WHERE region_id = #{regionId} " +
            "AND stat_month = #{statMonth} AND deleted = 0")
    Long countByRegionAndMonth(@Param("regionId") Long regionId,
                               @Param("statMonth") String statMonth);

    @Select("SELECT v.*, r.region_name as village_name FROM fin_village_income v " +
            "LEFT JOIN sys_region r ON v.village_id = r.id " +
            "WHERE v.region_id = #{regionId} AND v.stat_month = #{statMonth} " +
            "AND v.total_income > #{threshold} AND v.deleted = 0")
    IPage<FinVillageIncome> selectAboveThreshold(Page<FinVillageIncome> page,
                                                  @Param("regionId") Long regionId,
                                                  @Param("statMonth") String statMonth,
                                                  @Param("threshold") BigDecimal threshold);

    @Select("SELECT v.*, r.region_name as village_name FROM fin_village_income v " +
            "LEFT JOIN sys_region r ON v.village_id = r.id " +
            "WHERE v.region_id = #{regionId} AND v.stat_month = #{statMonth} " +
            "AND v.total_income < #{threshold} AND v.deleted = 0")
    IPage<FinVillageIncome> selectBelowThreshold(Page<FinVillageIncome> page,
                                                  @Param("regionId") Long regionId,
                                                  @Param("statMonth") String statMonth,
                                                  @Param("threshold") BigDecimal threshold);
}
