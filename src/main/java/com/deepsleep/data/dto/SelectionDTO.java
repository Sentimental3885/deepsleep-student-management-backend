package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SelectionDTO {

    @NotNull(message = "课序号不能为空")
    private Long cid;
}
