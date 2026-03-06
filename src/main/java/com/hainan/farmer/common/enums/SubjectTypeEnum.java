package com.hainan.farmer.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会计科目类型枚举
 * 对应 t_account_subject.subject_type
 */
@Getter
@AllArgsConstructor
public enum SubjectTypeEnum {

    ASSET(1, "资产"),
    LIABILITY(2, "负债"),
    EQUITY(3, "权益"),
    INCOME(4, "收入"),
    EXPENSE(5, "支出");

    private final Integer code;
    private final String desc;
}
