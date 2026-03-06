package com.farmer.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmer.modules.finance.entity.FinVoucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FinVoucherMapper extends BaseMapper<FinVoucher> {

    @Select("SELECT MAX(voucher_no) FROM fin_voucher WHERE account_set_id = #{accountSetId} AND voucher_word = #{voucherWord} AND deleted = 0")
    Integer selectMaxVoucherNo(@Param("accountSetId") Long accountSetId, @Param("voucherWord") String voucherWord);

    @Select("<script>" +
            "SELECT * FROM fin_voucher WHERE deleted = 0" +
            "<if test='accountSetId != null'> AND account_set_id = #{accountSetId}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='startDate != null'> AND make_date &gt;= #{startDate}</if>" +
            "<if test='endDate != null'> AND make_date &lt;= #{endDate}</if>" +
            " ORDER BY make_date DESC, voucher_no DESC" +
            "</script>")
    IPage<FinVoucher> selectPage(Page<FinVoucher> page,
                                  @Param("accountSetId") Long accountSetId,
                                  @Param("status") Integer status,
                                  @Param("startDate") String startDate,
                                  @Param("endDate") String endDate);
}
