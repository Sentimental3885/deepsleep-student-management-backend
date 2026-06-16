package com.deepsleep.exception;

import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.vo.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public <T> ResponseEntity<Result<T>> handleBizException(BusinessException be) {
        log.warn("业务异常 | 业务码：{} | 信息：{}", be.getResultCode().getCode(), be.getResultCode().getMsg());
        return ResponseEntity
                .status(be.getResultCode().getHttpStatus())
                .body(
                        Result.error(be.getResultCode(), be.getMessage())
                );
    }

    /**
     * 处理上传超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public <T> ResponseEntity<Result<T>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "上传超限 | 路径：{} {}",
                request.getMethod(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(ResultCode.UPLOAD_SIZE_TOO_LARGE.getHttpStatus())
                .body(
                        Result.error(ResultCode.UPLOAD_SIZE_TOO_LARGE)
                );
    }

    /**
     * 请求体字段校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<Result<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.toString(error.getDefaultMessage(), "参数校验失败"),
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));

        log.warn(
                "请求体字段不合规范 | 信息：{} | 路径：{} {}",
                fieldErrors,
                request.getMethod(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(ResultCode.BAD_REQUEST.getHttpStatus())
                .body(
                        Result.error(ResultCode.BAD_REQUEST, fieldErrors, "请求体字段格式错误")
                );
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
