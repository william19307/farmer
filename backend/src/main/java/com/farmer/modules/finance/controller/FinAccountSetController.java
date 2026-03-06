package com.farmer.modules.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farmer.common.result.Result;
import com.farmer.modules.finance.dto.AccountSetDTO;
import com.farmer.modules.finance.entity.FinAccount;
import com.farmer.modules.finance.entity.FinAccountSet;
import com.farmer.modules.finance.mapper.FinAccountMapper;
import com.farmer.modules.finance.service.FinAccountSetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "账套管理")
@RestController
@RequestMapping("/api/finance/account-set")
@RequiredArgsConstructor
public class FinAccountSetController {

    private final FinAccountSetService accountSetService;
    private final FinAccountMapper accountMapper;

    @Operation(summary = "查询账套列表")
    @GetMapping
    public Result<List<FinAccountSet>> list(@RequestParam(required = false) Long regionId) {
        LambdaQueryWrapper<FinAccountSet> query = new LambdaQueryWrapper<FinAccountSet>()
                .eq(regionId != null, FinAccountSet::getRegionId, regionId)
                .orderByDesc(FinAccountSet::getAccountYear);
        return Result.success(accountSetService.list(query));
    }

    @Operation(summary = "新增账套")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody AccountSetDTO dto) {
        return Result.success(accountSetService.createAccountSet(dto));
    }

    @Operation(summary = "修改账套")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody AccountSetDTO dto) {
        accountSetService.updateAccountSet(dto);
        return Result.success();
    }

    @Operation(summary = "查询会计科目树")
    @GetMapping("/{accountSetId}/accounts")
    public Result<List<FinAccount>> accounts(@PathVariable Long accountSetId) {
        return Result.success(accountMapper.selectByAccountSetId(accountSetId));
    }
}
