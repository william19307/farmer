package com.farmer.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmer.modules.finance.entity.FinVoucherDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FinVoucherDetailMapper extends BaseMapper<FinVoucherDetail> {

    @Select("SELECT * FROM fin_voucher_detail WHERE voucher_id = #{voucherId} AND deleted = 0 ORDER BY sort_order")
    List<FinVoucherDetail> selectByVoucherId(@Param("voucherId") Long voucherId);

    @Delete("DELETE FROM fin_voucher_detail WHERE voucher_id = #{voucherId}")
    void deleteByVoucherId(@Param("voucherId") Long voucherId);
}
