package com.farmer.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_account")
public class FinAccount extends BaseEntity {

    private Long accountSetId;
    private Long parentId;
    private String accountCode;     // 科目编码
    private String accountName;     // 科目名称
    private Integer accountType;    // 1-资产 2-负债 3-所有者权益 4-收入 5-支出
    private String balanceDirection; // debit/credit 借/贷
    private Integer level;          // 科目级次
    private Integer isLeaf;         // 是否末级 0/1
    private BigDecimal initBalance; // 期初余额
    private Integer status;
}
