package com.farmer.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 通用错误
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),

    // 认证错误
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    LOGIN_FAILED(1001, "用户名或密码错误"),
    USER_DISABLED(1002, "账号已被禁用"),
    TOKEN_INVALID(1003, "Token无效"),
    TOKEN_EXPIRED(1004, "Token已过期"),

    // 用户错误
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户名已存在"),
    OLD_PASSWORD_ERROR(2003, "原密码错误"),

    // 财务错误
    ACCOUNT_SET_NOT_FOUND(3001, "账套不存在"),
    VOUCHER_AUDITED(3002, "凭证已审核，不可修改"),
    VOUCHER_NOT_BALANCED(3003, "凭证借贷不平衡"),
    PERIOD_CLOSED(3004, "会计期间已结账"),
    ASSET_NOT_FOUND(3005, "资产不存在");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
