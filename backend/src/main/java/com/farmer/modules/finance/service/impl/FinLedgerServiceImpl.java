package com.farmer.modules.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farmer.modules.finance.entity.FinAccount;
import com.farmer.modules.finance.entity.FinVoucher;
import com.farmer.modules.finance.entity.FinVoucherDetail;
import com.farmer.modules.finance.mapper.FinAccountMapper;
import com.farmer.modules.finance.mapper.FinVoucherDetailMapper;
import com.farmer.modules.finance.mapper.FinVoucherMapper;
import com.farmer.modules.finance.service.FinLedgerService;
import com.farmer.modules.finance.vo.SubjectBalanceVO;
import com.farmer.modules.finance.vo.VoucherDetailVO;
import com.farmer.modules.finance.vo.VoucherVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinLedgerServiceImpl implements FinLedgerService {

    private final FinAccountMapper accountMapper;
    private final FinVoucherMapper voucherMapper;
    private final FinVoucherDetailMapper detailMapper;

    @Override
    public List<SubjectBalanceVO> getSubjectBalance(Long accountSetId, String period) {
        List<FinAccount> accounts = accountMapper.selectByAccountSetId(accountSetId);
        Map<Long, SubjectBalanceVO> voMap = new LinkedHashMap<>();

        for (FinAccount account : accounts) {
            SubjectBalanceVO vo = new SubjectBalanceVO();
            BeanUtils.copyProperties(account, vo);
            vo.setAccountId(account.getId());
            vo.setOpeningBalance(account.getInitBalance() == null ? BigDecimal.ZERO : account.getInitBalance());
            voMap.put(account.getId(), vo);
        }

        // 查询期间内的已审核凭证
        LambdaQueryWrapper<FinVoucher> voucherQuery = new LambdaQueryWrapper<FinVoucher>()
                .eq(FinVoucher::getAccountSetId, accountSetId)
                .eq(FinVoucher::getStatus, 1)
                .likeRight(period != null, FinVoucher::getMakeDate, period != null ? period.substring(0, 4) + "-" + period.substring(4) : "");

        List<FinVoucher> vouchers = voucherMapper.selectList(voucherQuery);
        for (FinVoucher voucher : vouchers) {
            List<FinVoucherDetail> details = detailMapper.selectByVoucherId(voucher.getId());
            for (FinVoucherDetail detail : details) {
                SubjectBalanceVO vo = voMap.get(detail.getAccountId());
                if (vo != null) {
                    BigDecimal debit = detail.getDebitAmount() == null ? BigDecimal.ZERO : detail.getDebitAmount();
                    BigDecimal credit = detail.getCreditAmount() == null ? BigDecimal.ZERO : detail.getCreditAmount();
                    vo.setPeriodDebit(vo.getPeriodDebit().add(debit));
                    vo.setPeriodCredit(vo.getPeriodCredit().add(credit));
                }
            }
        }

        // 计算期末余额
        for (SubjectBalanceVO vo : voMap.values()) {
            if ("debit".equals(vo.getBalanceDirection())) {
                vo.setClosingBalance(vo.getOpeningBalance().add(vo.getPeriodDebit()).subtract(vo.getPeriodCredit()));
            } else {
                vo.setClosingBalance(vo.getOpeningBalance().subtract(vo.getPeriodDebit()).add(vo.getPeriodCredit()));
            }
        }

        return new ArrayList<>(voMap.values());
    }

    @Override
    public List<VoucherVO> getSequentialLedger(Long accountSetId, String startDate, String endDate) {
        LambdaQueryWrapper<FinVoucher> query = new LambdaQueryWrapper<FinVoucher>()
                .eq(FinVoucher::getAccountSetId, accountSetId)
                .eq(FinVoucher::getStatus, 1)
                .ge(startDate != null, FinVoucher::getMakeDate, startDate)
                .le(endDate != null, FinVoucher::getMakeDate, endDate)
                .orderByAsc(FinVoucher::getMakeDate, FinVoucher::getVoucherNo);

        List<FinVoucher> vouchers = voucherMapper.selectList(query);
        return vouchers.stream().map(v -> {
            VoucherVO vo = new VoucherVO();
            BeanUtils.copyProperties(v, vo);
            vo.setStatusName(v.getStatus() == 1 ? "已审核" : "草稿");
            List<FinVoucherDetail> details = detailMapper.selectByVoucherId(v.getId());
            vo.setDetails(details.stream().map(d -> {
                VoucherDetailVO dvo = new VoucherDetailVO();
                BeanUtils.copyProperties(d, dvo);
                return dvo;
            }).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<VoucherVO> getDetailLedger(Long accountSetId, Long accountId, String startDate, String endDate) {
        LambdaQueryWrapper<FinVoucher> query = new LambdaQueryWrapper<FinVoucher>()
                .eq(FinVoucher::getAccountSetId, accountSetId)
                .eq(FinVoucher::getStatus, 1)
                .ge(startDate != null, FinVoucher::getMakeDate, startDate)
                .le(endDate != null, FinVoucher::getMakeDate, endDate)
                .orderByAsc(FinVoucher::getMakeDate, FinVoucher::getVoucherNo);

        List<FinVoucher> vouchers = voucherMapper.selectList(query);
        return vouchers.stream()
                .map(v -> {
                    List<FinVoucherDetail> details = detailMapper.selectByVoucherId(v.getId());
                    List<FinVoucherDetail> filtered = details.stream()
                            .filter(d -> accountId == null || accountId.equals(d.getAccountId()))
                            .collect(Collectors.toList());
                    if (filtered.isEmpty()) return null;
                    VoucherVO vo = new VoucherVO();
                    BeanUtils.copyProperties(v, vo);
                    vo.setDetails(filtered.stream().map(d -> {
                        VoucherDetailVO dvo = new VoucherDetailVO();
                        BeanUtils.copyProperties(d, dvo);
                        return dvo;
                    }).collect(Collectors.toList()));
                    return vo;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
