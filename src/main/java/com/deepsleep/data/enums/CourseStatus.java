package com.deepsleep.data.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseStatus {

    //开课
    ON(true),
    //未开课
    OFF(false);

    @EnumValue
    private final Boolean value;
}
