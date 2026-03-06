package com.farmer.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_voucher")
public class FinVoucher extends BaseEntity {

    private Long accountSetId;
    private String voucherWord;     // 凭证字（记、收、付、转）
    private Integer voucherNo;      // 凭证号
    private LocalDate makeDate;     // 制单日期
    private String summary;         // 摘要
    private Integer status;         // 0-草稿 1-已审核
    private Integer attachCount;    // 附件数量
    private Long auditBy;           // 审核人
}
