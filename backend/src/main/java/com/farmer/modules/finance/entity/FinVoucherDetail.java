package com.farmer.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_voucher_detail")
public class FinVoucherDetail extends BaseEntity {

    private Long voucherId;
    private Long accountId;         // 科目id
    private String accountCode;     // 科目编码（冗余）
    private String accountName;     // 科目名称（冗余）
    private String summary;         // 摘要
    private BigDecimal debitAmount; // 借方金额
    private BigDecimal creditAmount;// 贷方金额
    private Integer sortOrder;      // 排序
}
