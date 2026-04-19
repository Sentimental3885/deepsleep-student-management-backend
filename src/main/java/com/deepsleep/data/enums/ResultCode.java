package com.deepsleep.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "success",HttpStatus.OK),
    BAD_REQUEST(400, "请求参数错误", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "请先登录", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "权限不足", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "资源不存在", HttpStatus.NOT_FOUND),
    INTERVAL_SERVER_ERROR(500, "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR),

    USER_NOT_FOUND(1004, "用户不存在", HttpStatus.NOT_FOUND),
    PASSWORD_ERROR(1001, "用户名或密码错误", HttpStatus.UNAUTHORIZED),
    USER_DISABLED(1003, "账号已被禁用", HttpStatus.FORBIDDEN),
    ;

    private final Integer code;
    private final String msg;
    private final HttpStatus httpStatus;

}
