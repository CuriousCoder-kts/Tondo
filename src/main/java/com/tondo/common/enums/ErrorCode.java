package com.tondo.common.enums;

import lombok.Getter;

/**
 * 统一业务错误码，便于前后端对齐与监控告警分类。
 */
@Getter
public enum ErrorCode {

    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "操作冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    INTERNAL_ERROR(500, "服务器繁忙");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
