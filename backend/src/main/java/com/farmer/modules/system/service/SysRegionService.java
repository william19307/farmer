package com.farmer.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmer.modules.system.entity.SysRegion;

import java.util.List;

public interface SysRegionService extends IService<SysRegion> {

    List<SysRegion> getChildren(Long parentId);

    List<SysRegion> getByLevel(Integer level);

    Long countByLevel(Integer level);

    List<SysRegion> getTree(Long rootId);
}
