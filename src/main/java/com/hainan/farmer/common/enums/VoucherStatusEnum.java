package com.hainan.farmer.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 凭证状态枚举
 * 对应 t_voucher.status
 */
@Getter
@AllArgsConstructor
public enum VoucherStatusEnum {

    DRAFT(0, "草稿"),
    PENDING(1, "待审核"),
    AUDITED(2, "已审核"),
    POSTED(3, "已记账"),
    VOIDED(4, "已作废");

    private final Integer code;
    private final String desc;
}
