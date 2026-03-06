package com.farmer.modules.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Long regionId;   // 所属行政区划
    private Long roleId;
    private Integer status;  // 0-禁用 1-启用
    private String avatar;
    private String remark;
}
