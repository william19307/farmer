package com.hainan.farmer.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 财务报表类型枚举
 * 对应 t_finance_summary.report_type
 */
@Getter
@AllArgsConstructor
public enum ReportTypeEnum {

    BALANCE_SHEET(1, "资产负债表"),
    INCOME_STATEMENT(2, "损益表");

    private final Integer code;
    private final String desc;
}
