package com.farmer.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmer.modules.system.entity.SysRegion;
import com.farmer.modules.system.mapper.SysRegionMapper;
import com.farmer.modules.system.service.SysRegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRegionServiceImpl extends ServiceImpl<SysRegionMapper, SysRegion>
        implements SysRegionService {

    private final SysRegionMapper regionMapper;

    @Override
    public List<SysRegion> getChildren(Long parentId) {
        return regionMapper.selectChildren(parentId);
    }

    @Override
    public List<SysRegion> getByLevel(Integer level) {
        return regionMapper.selectByLevel(level);
    }

    @Override
    public Long countByLevel(Integer level) {
        return regionMapper.countByLevel(level);
    }

    @Override
    public List<SysRegion> getTree(Long rootId) {
        // Get all regions
        List<SysRegion> all = list();
        Map<Long, List<SysRegion>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(r -> r.getParentId() == null ? 0L : r.getParentId()));
        return buildTree(childrenMap, rootId == null ? 0L : rootId);
    }

    private List<SysRegion> buildTree(Map<Long, List<SysRegion>> childrenMap, Long parentId) {
        List<SysRegion> children = childrenMap.getOrDefault(parentId, new ArrayList<>());
        // Note: In a real tree, we'd add children to a field. Here simplified.
        return children;
    }
}
