package com.farmer.modules.map.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_village_income")
public class FinVillageIncome extends BaseEntity {

    private Long villageId;         // 村id（关联sys_region）
    private Long regionId;          // 所属区划id
    private String statMonth;       // 统计年月 yyyyMM
    private BigDecimal totalIncome;      // 总收入
    private BigDecimal businessIncome;   // 经营收入
    private BigDecimal subsidyIncome;    // 补助收入
    private BigDecimal investmentIncome; // 投资收益
    private BigDecimal otherIncome;      // 其他收入
}
