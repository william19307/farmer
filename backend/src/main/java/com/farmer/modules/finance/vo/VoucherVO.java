package com.farmer.modules.finance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VoucherVO {

    private Long id;
    private Long accountSetId;
    private String voucherWord;
    private Integer voucherNo;
    private LocalDate makeDate;
    private String summary;
    private Integer status;
    private String statusName;
    private Integer attachCount;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private LocalDateTime createTime;
    private String createByName;
    private List<VoucherDetailVO> details;
}
