package com.farmer.modules.finance.controller;

import com.farmer.common.result.PageResult;
import com.farmer.common.result.Result;
import com.farmer.modules.finance.dto.AssetDTO;
import com.farmer.modules.finance.entity.FinAsset;
import com.farmer.modules.finance.service.FinAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "固定资产管理")
@RestController
@RequestMapping("/api/finance/asset")
@RequiredArgsConstructor
public class FinAssetController {

    private final FinAssetService assetService;

    @Operation(summary = "分页查询固定资产")
    @GetMapping
    public Result<PageResult<FinAsset>> page(
            @RequestParam Long accountSetId,
            @RequestParam(required = false) Integer assetType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(assetService.pageAssets(accountSetId, assetType, status, keyword, pageNum, pageSize));
    }

    @Operation(summary = "新增固定资产")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody AssetDTO dto) {
        return Result.success(assetService.createAsset(dto));
    }

    @Operation(summary = "修改固定资产")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody AssetDTO dto) {
        assetService.updateAsset(dto);
        return Result.success();
    }

    @Operation(summary = "删除固定资产")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return Result.success();
    }

    @Operation(summary = "计提折旧")
    @PostMapping("/depreciate")
    public Result<Void> depreciate(
            @RequestParam Long accountSetId,
            @RequestParam String period) {
        assetService.depreciate(accountSetId, period);
        return Result.success();
    }

    @Operation(summary = "生成折旧凭证")
    @PostMapping("/generate-voucher")
    public Result<Long> generateVoucher(
            @RequestParam Long accountSetId,
            @RequestParam String period) {
        return Result.success(assetService.generateDepreciationVoucher(accountSetId, period));
    }
}
