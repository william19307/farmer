package com.farmer.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_account_book")
public class FinAccountBook extends BaseEntity {

    private Long accountSetId;
    private String bookName;        // 账户名称
    private Integer bookType;       // 1-银行 2-现金
    private String bankAccount;     // 账号
    private String bankName;        // 开户行
    private BigDecimal initBalance; // 期初余额
    private BigDecimal currentBalance; // 当前余额
    private Integer status;         // 0-停用 1-启用
}
