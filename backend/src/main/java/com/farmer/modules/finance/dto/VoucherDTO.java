package com.farmer.modules.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class VoucherDTO {

    private Long id;

    @NotNull(message = "账套id不能为空")
    private Long accountSetId;

    private String voucherWord = "记";

    @NotNull(message = "制单日期不能为空")
    private LocalDate makeDate;

    private String summary;
    private Integer attachCount = 0;

    @NotNull(message = "凭证明细不能为空")
    private List<VoucherDetailDTO> details;
}
