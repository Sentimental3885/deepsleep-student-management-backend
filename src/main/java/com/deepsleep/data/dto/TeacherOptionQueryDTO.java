package com.deepsleep.data.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TeacherOptionQueryDTO {
    private String keyword;
    private Long deptId;

    @Min(value = 1)
    private Integer pageNum;

    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize;

    public void setDefaultValue() {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
    }
}
