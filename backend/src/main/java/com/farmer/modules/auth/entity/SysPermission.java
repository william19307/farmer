package com.farmer.modules.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    private Long parentId;
    private String permName;
    private String permCode;
    private String path;
    private String component;
    private String icon;
    private Integer type;    // 0-目录 1-菜单 2-按钮
    private Integer sort;
    private Integer status;
}
