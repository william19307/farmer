package com.hainan.farmer.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账期状态枚举
 * 对应 t_account_period.status
 */
@Getter
@AllArgsConstructor
public enum PeriodStatusEnum {

    OPEN(0, "未结账"),
    CLOSED(1, "已结账"),
    LOCKED(2, "已关闭");

    private final Integer code;
    private final String desc;
}
