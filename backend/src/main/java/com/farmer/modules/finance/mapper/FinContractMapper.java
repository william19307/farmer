package com.farmer.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmer.modules.finance.entity.FinContract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FinContractMapper extends BaseMapper<FinContract> {

    @Select("<script>" +
            "SELECT * FROM fin_contract WHERE deleted = 0" +
            "<if test='accountSetId != null'> AND account_set_id = #{accountSetId}</if>" +
            "<if test='contractType != null'> AND contract_type = #{contractType}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (contract_no LIKE CONCAT('%',#{keyword},'%') OR contract_name LIKE CONCAT('%',#{keyword},'%'))</if>" +
            " ORDER BY sign_date DESC" +
            "</script>")
    IPage<FinContract> selectPage(Page<FinContract> page,
                                   @Param("accountSetId") Long accountSetId,
                                   @Param("contractType") Integer contractType,
                                   @Param("status") Integer status,
                                   @Param("keyword") String keyword);
}
