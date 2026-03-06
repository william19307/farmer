package com.farmer.modules.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VoucherDetailDTO {

    private Long id;

    @NotNull(message = "科目id不能为空")
    private Long accountId;

    private String summary;

    private BigDecimal debitAmount = BigDecimal.ZERO;
    private BigDecimal creditAmount = BigDecimal.ZERO;
    private Integer sortOrder;
}
