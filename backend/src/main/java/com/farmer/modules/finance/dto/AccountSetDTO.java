package com.farmer.modules.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountSetDTO {

    private Long id;

    @NotNull(message = "村id不能为空")
    private Long villageId;

    @NotNull(message = "区划id不能为空")
    private Long regionId;

    @NotBlank(message = "账套名称不能为空")
    private String setName;

    @NotNull(message = "会计年度不能为空")
    private Integer accountYear;

    private String currency = "CNY";
}
