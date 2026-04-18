package com.deepsleep.exception;

import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 返回状态码+Result（业务码+message）
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e){
        log.warn("业务异常：code={},msg={}", e.getResultCode().getCode(), e.getMsg());
        return ResponseEntity.status(e.getResultCode().getHttpStatus())
                .body(Result.error(e.getResultCode(), e.getMsg()));
    }

    /**
     * 处理参数校验异常（@Valid 触发的）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidException(MethodArgumentNotValidException e) {
        FieldError fe = e.getBindingResult().getFieldErrors().getFirst();
        String msg = fe.getField() + ": " + fe.getDefaultMessage();
        log.warn("参数校验失败: {}", msg);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(ResultCode.BAD_REQUEST, msg));
    }

    /**
     * 处理未预料到的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e){
        log.error("未知异常:", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(ResultCode.INTERVAL_SERVER_ERROR));
    }



}
