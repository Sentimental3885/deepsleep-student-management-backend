package com.deepsleep.exception;

import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.vo.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
        log.warn("业务异常：code={},msg={}",e.getResultCode().getCode(),e.getMsg());
        return ResponseEntity.status(e.getResultCode().getHttpStatus())
                .body(Result.error(e.getResultCode(),e.getMsg()));
    }

    /**
     * 处理参数校验异常（@Valid 触发的）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidException(MethodArgumentNotValidException e) {
        // 取出第一条校验错误信息
        String msg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("参数错误");
        log.warn("参数校验失败: {}", msg);
        return ResponseEntity
                .status(ResultCode.BAD_REQUEST.getHttpStatus())
                .body(Result.error(ResultCode.BAD_REQUEST));
    }

    /**
     * 处理JWT过期异常
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Result<Void>> handleExpiredJwt(ExpiredJwtException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(ResultCode.UNAUTHORIZED));
    }

    /**
     * JWT异常
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Result<Void>> handleJwtException(JwtException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(ResultCode.UNAUTHORIZED));
    }

    /**
     * 处理未预料到的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e){
        log.error("未知异常:",e);//打印完整堆栈
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(ResultCode.INTERVAL_SERVER_ERROR));
    }



}
