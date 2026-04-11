package com.deepsleep.data.vo;

import com.deepsleep.data.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Result<T> {

    private final Integer code; // 业务状态码
    private final T data;  // 数据
    private final String msg;   // 提示信息


    /**
     * 返回成功结果
     */
    public static <T> Result<T> success(T data, String msg) {
        return new Result<>(200, data, msg);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, data, "success");
    }

    /**
     * 返回成功（无数据）
     */
    public static Result<Void> success() {
        return new Result<>(200, null, "success");
    }


    /**
     * 返回错误结果
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, null, msg);
    }

    /**
     * 配合自定义异常枚举使用
     */
    public static <T> Result<T> error(ResultCode resultCode){
        return new Result<>(resultCode.getCode(),null,resultCode.getMsg());
    }

    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}