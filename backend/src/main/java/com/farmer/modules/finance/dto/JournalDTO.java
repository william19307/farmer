package com.farmer.modules.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class JournalDTO {

    private Long id;

    @NotNull(message = "账套id不能为空")
    private Long accountSetId;

    @NotNull(message = "账户id不能为空")
    private Long accountBookId;

    @NotNull(message = "类型不能为空")
    private Integer journalType;    // 1-银行 2-现金

    @NotNull(message = "日期不能为空")
    private LocalDate journalDate;

    private String summary;

    private BigDecimal incomeAmount = BigDecimal.ZERO;
    private BigDecimal expenseAmount = BigDecimal.ZERO;
}
