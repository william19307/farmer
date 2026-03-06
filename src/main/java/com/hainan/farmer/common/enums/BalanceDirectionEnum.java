package com.hainan.farmer.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 科目余额方向枚举
 * 对应 t_account_subject.balance_direction
 */
@Getter
@AllArgsConstructor
public enum BalanceDirectionEnum {

    DEBIT(1, "借方"),
    CREDIT(2, "贷方");

    private final Integer code;
    private final String desc;
}
