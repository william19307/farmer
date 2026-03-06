package com.farmer.modules.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetDTO {

    private Long id;

    @NotNull(message = "账套id不能为空")
    private Long accountSetId;

    @NotBlank(message = "资产编号不能为空")
    private String assetNo;

    @NotBlank(message = "资产名称不能为空")
    private String assetName;

    @NotNull(message = "资产类型不能为空")
    private Integer assetType;

    @NotNull(message = "入账日期不能为空")
    private LocalDate purchaseDate;

    @NotNull(message = "原值不能为空")
    private BigDecimal originalValue;

    private BigDecimal residualRate = new BigDecimal("0.05");
    private Integer usefulLifeMonths = 60;
    private Integer deprecMethod = 1;
    private String remark;
}
