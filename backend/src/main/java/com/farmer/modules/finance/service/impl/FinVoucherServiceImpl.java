package com.farmer.modules.finance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmer.common.exception.BizException;
import com.farmer.common.exception.ErrorCode;
import com.farmer.common.result.PageResult;
import com.farmer.modules.finance.dto.VoucherDTO;
import com.farmer.modules.finance.dto.VoucherDetailDTO;
import com.farmer.modules.finance.entity.FinAccount;
import com.farmer.modules.finance.entity.FinVoucher;
import com.farmer.modules.finance.entity.FinVoucherDetail;
import com.farmer.modules.finance.mapper.FinAccountMapper;
import com.farmer.modules.finance.mapper.FinVoucherDetailMapper;
import com.farmer.modules.finance.mapper.FinVoucherMapper;
import com.farmer.modules.finance.service.FinVoucherService;
import com.farmer.modules.finance.vo.VoucherDetailVO;
import com.farmer.modules.finance.vo.VoucherVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinVoucherServiceImpl extends ServiceImpl<FinVoucherMapper, FinVoucher> implements FinVoucherService {

    private final FinVoucherDetailMapper detailMapper;
    private final FinAccountMapper accountMapper;

    @Override
    public PageResult<VoucherVO> pageVouchers(Long accountSetId, Integer status, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        Page<FinVoucher> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, accountSetId, status, startDate, endDate);
        List<VoucherVO> vos = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(vos, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public VoucherVO getVoucherDetail(Long id) {
        FinVoucher voucher = getById(id);
        if (voucher == null) throw new BizException(ErrorCode.VOUCHER_AUDITED);
        VoucherVO vo = toVO(voucher);
        List<FinVoucherDetail> details = detailMapper.selectByVoucherId(id);
        vo.setDetails(details.stream().map(this::toDetailVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional
    public Long createVoucher(VoucherDTO dto) {
        validateDetails(dto.getDetails());

        FinVoucher voucher = new FinVoucher();
        BeanUtils.copyProperties(dto, voucher);
        voucher.setStatus(0);

        Integer maxNo = baseMapper.selectMaxVoucherNo(dto.getAccountSetId(), dto.getVoucherWord());
        voucher.setVoucherNo(maxNo == null ? 1 : maxNo + 1);

        save(voucher);
        saveDetails(voucher.getId(), dto.getDetails());
        return voucher.getId();
    }

    @Override
    @Transactional
    public void updateVoucher(VoucherDTO dto) {
        FinVoucher voucher = getById(dto.getId());
        if (voucher == null) throw new BizException(ErrorCode.SYSTEM_ERROR, "凭证不存在");
        if (voucher.getStatus() == 1) throw new BizException(ErrorCode.VOUCHER_AUDITED);

        validateDetails(dto.getDetails());
        BeanUtils.copyProperties(dto, voucher, "id", "accountSetId", "voucherWord", "voucherNo", "status");
        updateById(voucher);

        detailMapper.deleteByVoucherId(dto.getId());
        saveDetails(dto.getId(), dto.getDetails());
    }

    @Override
    @Transactional
    public void deleteVoucher(Long id) {
        FinVoucher voucher = getById(id);
        if (voucher == null) return;
        if (voucher.getStatus() == 1) throw new BizException(ErrorCode.VOUCHER_AUDITED);
        removeById(id);
        detailMapper.deleteByVoucherId(id);
    }

    @Override
    public void auditVoucher(Long id) {
        FinVoucher voucher = getById(id);
        if (voucher == null) throw new BizException(ErrorCode.SYSTEM_ERROR, "凭证不存在");
        if (voucher.getStatus() == 1) throw new BizException(ErrorCode.VOUCHER_AUDITED);
        voucher.setStatus(1);
        updateById(voucher);
    }

    @Override
    public void unauditVoucher(Long id) {
        FinVoucher voucher = getById(id);
        if (voucher == null) throw new BizException(ErrorCode.SYSTEM_ERROR, "凭证不存在");
        voucher.setStatus(0);
        updateById(voucher);
    }

    @Override
    @Transactional
    public void batchAudit(List<Long> ids) {
        ids.forEach(this::auditVoucher);
    }

    private void validateDetails(List<VoucherDetailDTO> details) {
        if (details == null || details.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "凭证明细不能为空");
        }
        BigDecimal totalDebit = details.stream()
                .map(d -> d.getDebitAmount() == null ? BigDecimal.ZERO : d.getDebitAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = details.stream()
                .map(d -> d.getCreditAmount() == null ? BigDecimal.ZERO : d.getCreditAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new BizException(ErrorCode.VOUCHER_NOT_BALANCED);
        }
    }

    private void saveDetails(Long voucherId, List<VoucherDetailDTO> details) {
        for (int i = 0; i < details.size(); i++) {
            VoucherDetailDTO dto = details.get(i);
            FinVoucherDetail detail = new FinVoucherDetail();
            BeanUtils.copyProperties(dto, detail, "id");
            detail.setVoucherId(voucherId);
            detail.setSortOrder(i + 1);

            FinAccount account = accountMapper.selectById(dto.getAccountId());
            if (account != null) {
                detail.setAccountCode(account.getAccountCode());
                detail.setAccountName(account.getAccountName());
            }
            detailMapper.insert(detail);
        }
    }

    private VoucherVO toVO(FinVoucher voucher) {
        VoucherVO vo = new VoucherVO();
        BeanUtils.copyProperties(voucher, vo);
        vo.setStatusName(voucher.getStatus() == 1 ? "已审核" : "草稿");
        return vo;
    }

    private VoucherDetailVO toDetailVO(FinVoucherDetail detail) {
        VoucherDetailVO vo = new VoucherDetailVO();
        BeanUtils.copyProperties(detail, vo);
        return vo;
    }
}
