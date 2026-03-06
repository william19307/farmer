package com.farmer.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmer.modules.finance.entity.FinAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FinAccountMapper extends BaseMapper<FinAccount> {

    @Select("SELECT * FROM fin_account WHERE account_set_id = #{accountSetId} AND deleted = 0 ORDER BY account_code")
    List<FinAccount> selectByAccountSetId(@Param("accountSetId") Long accountSetId);

    @Select("SELECT * FROM fin_account WHERE account_set_id = #{accountSetId} AND is_leaf = 1 AND deleted = 0 ORDER BY account_code")
    List<FinAccount> selectLeafAccounts(@Param("accountSetId") Long accountSetId);
}
