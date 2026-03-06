package com.farmer.modules.system.controller;

import com.farmer.common.result.Result;
import com.farmer.modules.system.entity.SysRegion;
import com.farmer.modules.system.service.SysRegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "行政区划管理")
@RestController
@RequestMapping("/api/system/region")
@RequiredArgsConstructor
public class SysRegionController {

    private final SysRegionService regionService;

    @Operation(summary = "获取子区划列表")
    @GetMapping("/children/{parentId}")
    public Result<List<SysRegion>> getChildren(@PathVariable Long parentId) {
        return Result.success(regionService.getChildren(parentId));
    }

    @Operation(summary = "按级别获取区划")
    @GetMapping("/level/{level}")
    public Result<List<SysRegion>> getByLevel(@PathVariable Integer level) {
        return Result.success(regionService.getByLevel(level));
    }

    @Operation(summary = "获取区划树")
    @GetMapping("/tree")
    public Result<List<SysRegion>> getTree(@RequestParam(required = false) Long rootId) {
        return Result.success(regionService.getTree(rootId));
    }
}
