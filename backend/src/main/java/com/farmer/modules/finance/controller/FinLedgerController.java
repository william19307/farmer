package com.farmer.modules.finance.controller;

import com.farmer.common.result.Result;
import com.farmer.modules.finance.service.FinLedgerService;
import com.farmer.modules.finance.vo.SubjectBalanceVO;
import com.farmer.modules.finance.vo.VoucherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "账簿查询")
@RestController
@RequestMapping("/api/finance/ledger")
@RequiredArgsConstructor
public class FinLedgerController {

    private final FinLedgerService ledgerService;

    @Operation(summary = "科目余额表")
    @GetMapping("/subject-balance")
    public Result<List<SubjectBalanceVO>> subjectBalance(
            @RequestParam Long accountSetId,
            @RequestParam(required = false) String period) {
        return Result.success(ledgerService.getSubjectBalance(accountSetId, period));
    }

    @Operation(summary = "序时账")
    @GetMapping("/sequential")
    public Result<List<VoucherVO>> sequential(
            @RequestParam Long accountSetId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(ledgerService.getSequentialLedger(accountSetId, startDate, endDate));
    }

    @Operation(summary = "明细账")
    @GetMapping("/detail")
    public Result<List<VoucherVO>> detail(
            @RequestParam Long accountSetId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(ledgerService.getDetailLedger(accountSetId, accountId, startDate, endDate));
    }
}
