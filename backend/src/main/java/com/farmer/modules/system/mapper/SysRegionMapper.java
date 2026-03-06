package com.farmer.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmer.modules.system.entity.SysRegion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRegionMapper extends BaseMapper<SysRegion> {

    @Select("SELECT * FROM sys_region WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort")
    List<SysRegion> selectChildren(Long parentId);

    @Select("SELECT * FROM sys_region WHERE level = #{level} AND deleted = 0 ORDER BY sort")
    List<SysRegion> selectByLevel(Integer level);

    @Select("SELECT COUNT(*) FROM sys_region WHERE level = #{level} AND deleted = 0")
    Long countByLevel(Integer level);
}
