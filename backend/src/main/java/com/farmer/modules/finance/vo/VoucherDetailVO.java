package com.farmer.modules.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VoucherDetailVO {

    private Long id;
    private Long voucherId;
    private Long accountId;
    private String accountCode;
    private String accountName;
    private String summary;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private Integer sortOrder;
}
