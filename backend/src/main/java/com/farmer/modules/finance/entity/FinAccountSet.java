package com.farmer.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_account_set")
public class FinAccountSet extends BaseEntity {

    private Long villageId;
    private Long regionId;
    private String setName;          // 账套名称
    private Integer accountYear;     // 会计年度
    private String currency;         // 本位币（默认CNY）
    private Integer status;          // 0-未启用 1-启用
}
