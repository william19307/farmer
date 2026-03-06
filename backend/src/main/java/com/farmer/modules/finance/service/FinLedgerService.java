package com.farmer.modules.finance.service;

import com.farmer.modules.finance.vo.SubjectBalanceVO;
import com.farmer.modules.finance.vo.VoucherVO;

import java.util.List;

public interface FinLedgerService {

    /**
     * 科目余额表
     */
    List<SubjectBalanceVO> getSubjectBalance(Long accountSetId, String period);

    /**
     * 序时账（按日期顺序列出所有凭证明细）
     */
    List<VoucherVO> getSequentialLedger(Long accountSetId, String startDate, String endDate);

    /**
     * 明细账（按科目查询）
     */
    List<VoucherVO> getDetailLedger(Long accountSetId, Long accountId, String startDate, String endDate);
}
