package com.deepsleep.data.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.deepsleep.exception.BusinessException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CourseStatus {

    //开课
    ON(true),
    //未开课
    OFF(false);

    @EnumValue
    private final Boolean value;

    public static CourseStatus fromValue(int value) {
        return switch (value) {
            case 0 -> OFF;
            case 1 -> ON;
            default -> throw new BusinessException(ResultCode.INVALID_COURSE_STATUS);
        };
    }

    public int getValue() {
        if (value == true) return 1;
        else return 0;
    }
}
