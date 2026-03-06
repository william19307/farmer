package com.farmer.modules.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmer.common.result.PageResult;
import com.farmer.modules.finance.dto.JournalDTO;
import com.farmer.modules.finance.entity.FinJournal;

public interface FinJournalService extends IService<FinJournal> {

    PageResult<FinJournal> pageJournals(Long accountSetId, Long accountBookId, Integer journalType, String startDate, String endDate, Integer pageNum, Integer pageSize);

    Long createJournal(JournalDTO dto);

    void updateJournal(JournalDTO dto);

    void deleteJournal(Long id);

    Long generateVoucher(Long journalId);
}
