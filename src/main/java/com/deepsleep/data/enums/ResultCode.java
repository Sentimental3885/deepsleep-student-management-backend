package com.deepsleep.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "success",HttpStatus.OK),
    BAD_REQUEST(400, "invalid param", HttpStatus.BAD_REQUEST),
    INTERVAL_SERVER_ERROR(500, "interval error", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final Integer code;
    private final String msg;
    private final HttpStatus httpStatus;

}
