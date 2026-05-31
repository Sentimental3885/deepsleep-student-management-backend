package com.deepsleep.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExamType {
    MIDTERM(1, "期中"),
    FINAL(2, "期末"),
    MAKEUP(3, "补考");

    private final Integer code;
    private final String desc;//description
}
