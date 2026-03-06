package com.farmer.modules.finance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SubjectBalanceVO {

    private Long accountId;
    private String accountCode;
    private String accountName;
    private Integer accountType;
    private String balanceDirection;
    private Integer level;

    // 期初余额
    private BigDecimal openingDebit = BigDecimal.ZERO;
    private BigDecimal openingCredit = BigDecimal.ZERO;
    private BigDecimal openingBalance = BigDecimal.ZERO;

    // 本期发生额
    private BigDecimal periodDebit = BigDecimal.ZERO;
    private BigDecimal periodCredit = BigDecimal.ZERO;

    // 本年累计发生额
    private BigDecimal yearDebit = BigDecimal.ZERO;
    private BigDecimal yearCredit = BigDecimal.ZERO;

    // 期末余额
    private BigDecimal closingDebit = BigDecimal.ZERO;
    private BigDecimal closingCredit = BigDecimal.ZERO;
    private BigDecimal closingBalance = BigDecimal.ZERO;

    private List<SubjectBalanceVO> children;
}
