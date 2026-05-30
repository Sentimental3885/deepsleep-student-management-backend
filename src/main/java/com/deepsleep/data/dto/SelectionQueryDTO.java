package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SelectionQueryDTO {
    @NotNull(message = "当前页数不能为空")
    private Integer current;
    @NotNull(message = "每页最大容量不能为空")
    private Integer size;
}
