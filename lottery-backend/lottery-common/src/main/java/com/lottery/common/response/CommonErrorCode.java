package com.lottery.common.response;

// 错误码体系：5位数字，前3位对应 HTTP 状态码，后2位为内部细分码
public enum CommonErrorCode {
    SUCCESS("00000", "Success"),
    BAD_REQUEST("40000", "Request validation failed"),
    UNAUTHORIZED("40100", "Authentication required"),
    FORBIDDEN("40300", "Access denied"),
    NOT_FOUND("40400", "Resource not found"),
    SYSTEM_ERROR("50000", "Internal server error");

    private final String code;
    private final String message;

    CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}

