package com.farmer.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_region")
public class SysRegion extends BaseEntity {

    private Long parentId;
    private String regionName;
    private String regionCode;  // 行政区划代码
    private Integer level;      // 1-省 2-市 3-县 4-乡镇 5-村
    private String regionType;  // province/city/county/township/village
    private Integer sort;
}
