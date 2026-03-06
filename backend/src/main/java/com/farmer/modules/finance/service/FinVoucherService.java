package com.farmer.modules.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmer.common.result.PageResult;
import com.farmer.modules.finance.dto.VoucherDTO;
import com.farmer.modules.finance.entity.FinVoucher;
import com.farmer.modules.finance.vo.VoucherVO;

import java.util.List;

public interface FinVoucherService extends IService<FinVoucher> {

    PageResult<VoucherVO> pageVouchers(Long accountSetId, Integer status, String startDate, String endDate, Integer pageNum, Integer pageSize);

    VoucherVO getVoucherDetail(Long id);

    Long createVoucher(VoucherDTO dto);

    void updateVoucher(VoucherDTO dto);

    void deleteVoucher(Long id);

    void auditVoucher(Long id);

    void unauditVoucher(Long id);

    void batchAudit(List<Long> ids);
}
