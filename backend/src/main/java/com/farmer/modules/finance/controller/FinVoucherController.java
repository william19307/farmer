package com.farmer.modules.finance.controller;

import com.farmer.common.result.PageResult;
import com.farmer.common.result.Result;
import com.farmer.modules.finance.dto.VoucherDTO;
import com.farmer.modules.finance.service.FinVoucherService;
import com.farmer.modules.finance.vo.VoucherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "凭证管理")
@RestController
@RequestMapping("/api/finance/voucher")
@RequiredArgsConstructor
public class FinVoucherController {

    private final FinVoucherService voucherService;

    @Operation(summary = "分页查询凭证列表")
    @GetMapping
    public Result<PageResult<VoucherVO>> page(
            @RequestParam Long accountSetId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(voucherService.pageVouchers(accountSetId, status, startDate, endDate, pageNum, pageSize));
    }

    @Operation(summary = "查询凭证详情")
    @GetMapping("/{id}")
    public Result<VoucherVO> getById(@PathVariable Long id) {
        return Result.success(voucherService.getVoucherDetail(id));
    }

    @Operation(summary = "新增凭证")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody VoucherDTO dto) {
        return Result.success(voucherService.createVoucher(dto));
    }

    @Operation(summary = "修改凭证")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody VoucherDTO dto) {
        voucherService.updateVoucher(dto);
        return Result.success();
    }

    @Operation(summary = "删除凭证")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return Result.success();
    }

    @Operation(summary = "审核凭证")
    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id) {
        voucherService.auditVoucher(id);
        return Result.success();
    }

    @Operation(summary = "反审核凭证")
    @PostMapping("/{id}/unaudit")
    public Result<Void> unaudit(@PathVariable Long id) {
        voucherService.unauditVoucher(id);
        return Result.success();
    }

    @Operation(summary = "批量审核")
    @PostMapping("/batch-audit")
    public Result<Void> batchAudit(@RequestBody List<Long> ids) {
        voucherService.batchAudit(ids);
        return Result.success();
    }
}
