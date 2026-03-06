package com.farmer.modules.map.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmer.common.result.PageResult;
import com.farmer.common.result.Result;
import com.farmer.modules.map.finance.entity.FinIndicators;
import com.farmer.modules.map.finance.entity.FinVillageIncome;
import com.farmer.modules.map.finance.service.FinanceMapService;
import com.farmer.modules.map.finance.vo.FinanceMapOverviewVO;
import com.farmer.modules.map.finance.vo.IncomeThresholdVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "财务一张图")
@RestController
@RequestMapping("/api/map/finance")
@RequiredArgsConstructor
public class FinanceMapController {

    private final FinanceMapService financeMapService;

    @Operation(summary = "获取总览数据（区县数/乡镇数/村集体数/未结账数）")
    @GetMapping("/overview")
    public Result<FinanceMapOverviewVO> getOverview(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String statMonth) {
        return Result.success(financeMapService.getOverview(regionId, statMonth));
    }

    @Operation(summary = "收入分级统计（5万/10万/20万）")
    @GetMapping("/income/stats")
    public Result<List<IncomeThresholdVO>> getIncomeStats(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String statMonth) {
        return Result.success(financeMapService.getIncomeStats(regionId, statMonth));
    }

    @Operation(summary = "收入明细 - 高于阈值")
    @GetMapping("/income/detail/above")
    public Result<PageResult<FinVillageIncome>> getIncomeDetailAbove(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String statMonth,
            @RequestParam(defaultValue = "50000") BigDecimal threshold) {
        Page<FinVillageIncome> page = new Page<>(current, size);
        IPage<FinVillageIncome> result = financeMapService.getIncomeDetailAbove(page, regionId, statMonth, threshold);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "收入明细 - 低于阈值")
    @GetMapping("/income/detail/below")
    public Result<PageResult<FinVillageIncome>> getIncomeDetailBelow(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String statMonth,
            @RequestParam(defaultValue = "50000") BigDecimal threshold) {
        Page<FinVillageIncome> page = new Page<>(current, size);
        IPage<FinVillageIncome> result = financeMapService.getIncomeDetailBelow(page, regionId, statMonth, threshold);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "财务指标数据")
    @GetMapping("/indicators")
    public Result<List<FinIndicators>> getIndicators(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String statMonth) {
        return Result.success(financeMapService.getIndicators(regionId, statMonth));
    }
}
