package com.farmer.modules.finance.controller;

import com.farmer.common.result.PageResult;
import com.farmer.common.result.Result;
import com.farmer.modules.finance.dto.JournalDTO;
import com.farmer.modules.finance.entity.FinJournal;
import com.farmer.modules.finance.service.FinJournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "出纳管理-日记账")
@RestController
@RequestMapping("/api/finance/journal")
@RequiredArgsConstructor
public class FinJournalController {

    private final FinJournalService journalService;

    @Operation(summary = "分页查询日记账")
    @GetMapping
    public Result<PageResult<FinJournal>> page(
            @RequestParam Long accountSetId,
            @RequestParam(required = false) Long accountBookId,
            @RequestParam(required = false) Integer journalType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(journalService.pageJournals(accountSetId, accountBookId, journalType, startDate, endDate, pageNum, pageSize));
    }

    @Operation(summary = "新增日记账")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody JournalDTO dto) {
        return Result.success(journalService.createJournal(dto));
    }

    @Operation(summary = "修改日记账")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody JournalDTO dto) {
        journalService.updateJournal(dto);
        return Result.success();
    }

    @Operation(summary = "删除日记账")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        journalService.deleteJournal(id);
        return Result.success();
    }

    @Operation(summary = "生成凭证")
    @PostMapping("/{id}/generate-voucher")
    public Result<Long> generateVoucher(@PathVariable Long id) {
        return Result.success(journalService.generateVoucher(id));
    }
}
