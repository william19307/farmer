package com.farmer.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmer.modules.finance.entity.FinClosePeriod;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FinClosePeriodMapper extends BaseMapper<FinClosePeriod> {

    @Select("SELECT * FROM fin_close_period WHERE account_set_id = #{accountSetId} AND account_period = #{period} AND deleted = 0 LIMIT 1")
    FinClosePeriod selectByPeriod(@Param("accountSetId") Long accountSetId, @Param("period") String period);

    @Select("SELECT COUNT(*) FROM fin_close_period WHERE account_set_id = #{accountSetId} AND account_period = #{period} AND status = 1 AND deleted = 0")
    int isClosed(@Param("accountSetId") Long accountSetId, @Param("period") String period);
}
