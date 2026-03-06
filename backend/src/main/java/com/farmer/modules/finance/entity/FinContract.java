package com.farmer.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_contract")
public class FinContract extends BaseEntity {

    private Long accountSetId;
    private String contractNo;      // 合同编号
    private String contractName;    // 合同名称
    private Integer contractType;   // 合同类型（1-承包 2-租赁 3-工程 4-其他）
    private String counterparty;    // 对方当事人
    private LocalDate signDate;     // 签订日期
    private BigDecimal amount;      // 合同金额
    private LocalDate startDate;    // 起始日期
    private LocalDate endDate;      // 终止日期
    private Integer status;         // 0-未生效 1-履行中 2-已完成 3-已终止
    private String remark;
}
