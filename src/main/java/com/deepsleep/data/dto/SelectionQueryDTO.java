package com.deepsleep.data.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SelectionQueryDTO {
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    @Min(value = 1)
    private Integer pageNum;

    @Min(value = 1)
    private Integer pageSize;

    private String keyword;
    private String semester;
    private Integer weekday;
    private Double creditMin;
    private Double creditMax;
    private Boolean noConflictOnly;

    public void setDefaultValue() {
        pageNum = pageNum == null ? DEFAULT_PAGE_NUM : pageNum;
        pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
