package com.deepsleep.data.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SelectionStatus {

    //已选
    PICKED(1),
    //退选
    DROPPED(2),
    //结课
    OVER(3);

    //MybatisPlus提供的枚举类映射注解
    @EnumValue
    private final Integer value;

    @JsonValue
    public Integer getValue() {
        return value;
    }
}
