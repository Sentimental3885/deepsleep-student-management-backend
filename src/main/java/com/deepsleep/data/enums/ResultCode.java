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
    TEACHER_UNAUTHORIZED(2200, "非授课教师无权更改", HttpStatus.UNAUTHORIZED),
    INVALID_SCORE(2201, "分数非法", HttpStatus.BAD_REQUEST),
    SCORE_UNAVAILABLE(2202, "成绩未公布", HttpStatus.CONFLICT),

    TEACHER_NOT_FOUND(3000, "教师不存在", HttpStatus.NOT_FOUND),
    CLAZZ_NOT_FOUND(3001, "班级不存在", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_EXIST(3002, "课程已存在", HttpStatus.CONFLICT),
    INVALID_COURSE_STATUS(3003, "课程状态码非法", HttpStatus.BAD_REQUEST),
    CAPACITY_NOT_ENOUGH(3004,"所设容量过小", HttpStatus.CONFLICT),
    SCHEDULE_NOT_FOUND(3100, "课程不存在" , HttpStatus.NOT_FOUND),
    SCHEDULE_COURSE_MISMATCH(3101, "排课与课程不匹配", HttpStatus.CONFLICT),
    SCHEDULE_CONFLICT(3102, "排课冲突", HttpStatus.CONFLICT),


    CLASSROOM_NOT_FOUND(4000, "教室不存在", HttpStatus.NOT_FOUND),
    CLASSROOM_CONFLICT(4001, "教室名称已存在", HttpStatus.CONFLICT),
    CLASSROOM_HAS_REFERENCES(4002, "教室被排课或考试使用中，无法删除", HttpStatus.BAD_REQUEST),

    DEPT_CONFLICT(5001, "学院名称已存在", HttpStatus.CONFLICT),
    DEPT_HAS_REFERENCES(5002, "学院下还有专业/学生/教师，无法删除", HttpStatus.BAD_REQUEST),
    MAJOR_CONFLICT(5003, "该学院下专业名称已存在", HttpStatus.CONFLICT),
    MAJOR_HAS_REFERENCES(5004, "专业下还有班级/学生，无法删除", HttpStatus.BAD_REQUEST),
    CLAZZ_CONFLICT(5005, "班级名称已存在", HttpStatus.CONFLICT),
    CLAZZ_HAS_REFERENCES(5006, "班级下还有学生，无法删除", HttpStatus.BAD_REQUEST),

    AI_SERVICE_ERROR(6000, "AI服务异常", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_GRADES_TO_ANALYZE(6001, "暂无成绩可供分析", HttpStatus.BAD_REQUEST),

    FILE_UPLOAD_FAILED(7000, "文件上传失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED(7001, "文件删除失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_EMPTY(7002, "文件为空", HttpStatus.BAD_REQUEST),
    FILE_TYPE_NOT_SUPPORT(7003, "文件格式不支持", HttpStatus.BAD_REQUEST),
    UPLOAD_SIZE_TOO_LARGE(7004, "请求/请求文件体积过大", HttpStatus.BAD_REQUEST),

    ;

    private final Integer code;
    private final String msg;
    private final HttpStatus httpStatus;

}
