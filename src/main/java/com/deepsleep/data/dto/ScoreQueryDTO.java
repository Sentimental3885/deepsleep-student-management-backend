package com.deepsleep.data.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.deepsleep.data.dto.SelectionQueryDTO.DEFAULT_PAGE_NUM;
import static com.deepsleep.data.dto.SelectionQueryDTO.DEFAULT_PAGE_SIZE;

@AllArgsConstructor
@Data
public class ScoreQueryDTO {
    @NotBlank(message = "修业学期不能为空")
    private String semester;
    @Min(1)
    private Long pageNum;
    @Min(1)
    private Long pageSize;

    public void setDefaultValue() {
        pageNum = pageNum == null ? DEFAULT_PAGE_NUM : pageNum;
        pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
