package com.hainan.farmer.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 凭证类型枚举
 * 对应 t_voucher.voucher_type
 */
@Getter
@AllArgsConstructor
public enum VoucherTypeEnum {

    RECEIPT(1, "收款凭证"),
    PAYMENT(2, "付款凭证"),
    TRANSFER(3, "转账凭证"),
    MEMO(4, "记账凭证");

    private final Integer code;
    private final String desc;
}
