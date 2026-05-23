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
    CODE_EXPIRED(400, "验证码已过期", HttpStatus.BAD_REQUEST),
    CODE_ERROR(400, "验证码错误", HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND(1004, "用户不存在", HttpStatus.NOT_FOUND),
    PASSWORD_ERROR(1001, "用户名或密码错误", HttpStatus.UNAUTHORIZED),
    USER_DISABLED(1003, "账号已被禁用", HttpStatus.FORBIDDEN),
    PHONE_CONFLICTED(1005,"手机号已被占用",HttpStatus.CONFLICT),
    EMAIL_CONFLICT(1006, "邮箱已被占用", HttpStatus.CONFLICT),
    STUDENT_ALREADY_EXIST(1101, "学生已存在", HttpStatus.CONFLICT),
    TEACHER_ALREADY_EXIST(1102,"教师已存在",HttpStatus.CONFLICT),
    EMAIL_NOT_BOUND(1007, "当前账号未绑定邮箱", HttpStatus.BAD_REQUEST),

    STUDENT_NOT_FOUND(2000, "学生不存在", HttpStatus.NOT_FOUND),
    COURSE_NOT_FOUND(2001, "课程不存在", HttpStatus.NOT_FOUND),
    COURSE_UNPICKABLE(2002, "课程不可选", HttpStatus.CONFLICT),
    COURSE_FULL(2003, "课程已满员", HttpStatus.CONFLICT),
    SELECTION_NOT_FOUND(2100, "课程未被选", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_PICKED(2102, "课程已被选", HttpStatus.CONFLICT),
    COURSE_ALREADY_DROPPED(2103, "课程已退选", HttpStatus.CONFLICT),
    COURSE_ALREADY_OVER(2104, "课程已修完", HttpStatus.CONFLICT),
    TEACHER_UNAUTHORIZED(2200, "教师无权结课", HttpStatus.UNAUTHORIZED),
    INVALID_SCORE(2201, "分数非法", HttpStatus.BAD_REQUEST),
    ;

    private final Integer code;
    private final String msg;
    private final HttpStatus httpStatus;

}
