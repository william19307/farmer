package com.farmer.modules.map.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_indicators")
public class FinIndicators extends BaseEntity {

    private Long villageId;
    private Long regionId;
    private String statMonth;
    private BigDecimal monetaryFunds;       // 货币资金
    private BigDecimal receivables;         // 应收账款
    private BigDecimal inventory;           // 存货
    private BigDecimal fixedAssets;         // 固定资产
    private BigDecimal payables;            // 应付账款
    private BigDecimal businessIncome;      // 经营收入
    private BigDecimal businessExpense;     // 经营支出
    private BigDecimal managementFee;       // 管理费用
    private BigDecimal currentYearProfit;   // 本年收益
    private BigDecimal undistributedProfit; // 未分配收益
}
