package com.farmer.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_journal")
public class FinJournal extends BaseEntity {

    private Long accountSetId;
    private Long accountBookId;     // 资金账户id
    private Integer journalType;    // 1-银行 2-现金
    private LocalDate journalDate;  // 日期
    private String summary;         // 摘要
    private BigDecimal incomeAmount;  // 收入金额
    private BigDecimal expenseAmount; // 支出金额
    private BigDecimal balance;       // 余额
    private Long voucherId;           // 关联凭证id
    private String voucherNo;         // 凭证号（冗余）
}
