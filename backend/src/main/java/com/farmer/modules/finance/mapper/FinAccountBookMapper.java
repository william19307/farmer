package com.farmer.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmer.modules.finance.entity.FinAccountBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FinAccountBookMapper extends BaseMapper<FinAccountBook> {

    @Select("SELECT * FROM fin_account_book WHERE account_set_id = #{accountSetId} AND deleted = 0 ORDER BY book_type, create_time")
    List<FinAccountBook> selectByAccountSetId(@Param("accountSetId") Long accountSetId);
}
