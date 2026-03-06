package com.farmer.modules.finance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmer.common.exception.BizException;
import com.farmer.common.exception.ErrorCode;
import com.farmer.common.result.PageResult;
import com.farmer.modules.finance.dto.JournalDTO;
import com.farmer.modules.finance.dto.VoucherDTO;
import com.farmer.modules.finance.dto.VoucherDetailDTO;
import com.farmer.modules.finance.entity.FinAccountBook;
import com.farmer.modules.finance.entity.FinJournal;
import com.farmer.modules.finance.mapper.FinAccountBookMapper;
import com.farmer.modules.finance.mapper.FinJournalMapper;
import com.farmer.modules.finance.service.FinJournalService;
import com.farmer.modules.finance.service.FinVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinJournalServiceImpl extends ServiceImpl<FinJournalMapper, FinJournal> implements FinJournalService {

    private final FinAccountBookMapper accountBookMapper;
    private final FinVoucherService voucherService;

    @Override
    public PageResult<FinJournal> pageJournals(Long accountSetId, Long accountBookId, Integer journalType, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        Page<FinJournal> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, accountSetId, accountBookId, journalType, startDate, endDate);
        return PageResult.of(page);
    }

    @Override
    @Transactional
    public Long createJournal(JournalDTO dto) {
        FinAccountBook book = accountBookMapper.selectById(dto.getAccountBookId());
        if (book == null) throw new BizException(ErrorCode.SYSTEM_ERROR, "账户不存在");

        BigDecimal income = dto.getIncomeAmount() == null ? BigDecimal.ZERO : dto.getIncomeAmount();
        BigDecimal expense = dto.getExpenseAmount() == null ? BigDecimal.ZERO : dto.getExpenseAmount();

        BigDecimal newBalance = book.getCurrentBalance().add(income).subtract(expense);

        FinJournal journal = new FinJournal();
        BeanUtils.copyProperties(dto, journal);
        journal.setBalance(newBalance);
        save(journal);

        book.setCurrentBalance(newBalance);
        accountBookMapper.updateById(book);

        return journal.getId();
    }

    @Override
    @Transactional
    public void updateJournal(JournalDTO dto) {
        FinJournal existing = getById(dto.getId());
        if (existing == null) throw new BizException(ErrorCode.SYSTEM_ERROR, "日记账记录不存在");
        if (existing.getVoucherId() != null) throw new BizException(ErrorCode.SYSTEM_ERROR, "已生成凭证，不可修改");

        // 反向还原余额
        FinAccountBook book = accountBookMapper.selectById(existing.getAccountBookId());
        BigDecimal restoredBalance = book.getCurrentBalance()
                .subtract(existing.getIncomeAmount() == null ? BigDecimal.ZERO : existing.getIncomeAmount())
                .add(existing.getExpenseAmount() == null ? BigDecimal.ZERO : existing.getExpenseAmount());

        BigDecimal income = dto.getIncomeAmount() == null ? BigDecimal.ZERO : dto.getIncomeAmount();
        BigDecimal expense = dto.getExpenseAmount() == null ? BigDecimal.ZERO : dto.getExpenseAmount();
        BigDecimal newBalance = restoredBalance.add(income).subtract(expense);

        BeanUtils.copyProperties(dto, existing, "id", "accountSetId", "accountBookId", "journalType");
        existing.setBalance(newBalance);
        updateById(existing);

        book.setCurrentBalance(newBalance);
        accountBookMapper.updateById(book);
    }

    @Override
    @Transactional
    public void deleteJournal(Long id) {
        FinJournal journal = getById(id);
        if (journal == null) return;
        if (journal.getVoucherId() != null) throw new BizException(ErrorCode.SYSTEM_ERROR, "已生成凭证，不可删除");

        FinAccountBook book = accountBookMapper.selectById(journal.getAccountBookId());
        BigDecimal restoredBalance = book.getCurrentBalance()
                .subtract(journal.getIncomeAmount() == null ? BigDecimal.ZERO : journal.getIncomeAmount())
                .add(journal.getExpenseAmount() == null ? BigDecimal.ZERO : journal.getExpenseAmount());
        book.setCurrentBalance(restoredBalance);
        accountBookMapper.updateById(book);

        removeById(id);
    }

    @Override
    @Transactional
    public Long generateVoucher(Long journalId) {
        FinJournal journal = getById(journalId);
        if (journal == null) throw new BizException(ErrorCode.SYSTEM_ERROR, "日记账记录不存在");
        if (journal.getVoucherId() != null) throw new BizException(ErrorCode.SYSTEM_ERROR, "已生成凭证");

        FinAccountBook book = accountBookMapper.selectById(journal.getAccountBookId());

        VoucherDTO voucherDTO = new VoucherDTO();
        voucherDTO.setAccountSetId(journal.getAccountSetId());
        voucherDTO.setMakeDate(journal.getJournalDate());
        voucherDTO.setSummary(journal.getSummary());

        // 根据日记账类型生成对应的凭证明细（简化版，实际应配置科目映射）
        VoucherDetailDTO debit = new VoucherDetailDTO();
        debit.setSummary(journal.getSummary());

        VoucherDetailDTO credit = new VoucherDetailDTO();
        credit.setSummary(journal.getSummary());

        BigDecimal amount = journal.getIncomeAmount() != null && journal.getIncomeAmount().compareTo(BigDecimal.ZERO) > 0
                ? journal.getIncomeAmount() : journal.getExpenseAmount();

        debit.setDebitAmount(amount);
        debit.setCreditAmount(BigDecimal.ZERO);
        credit.setDebitAmount(BigDecimal.ZERO);
        credit.setCreditAmount(amount);

        voucherDTO.setDetails(List.of(debit, credit));
        Long voucherId = voucherService.createVoucher(voucherDTO);

        journal.setVoucherId(voucherId);
        journal.setVoucherNo(String.valueOf(voucherId));
        updateById(journal);

        return voucherId;
    }
}
