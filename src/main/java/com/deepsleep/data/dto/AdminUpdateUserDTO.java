package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpdateUserDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "性别不能为空")
    private Integer gender;

    private String phone;
    private String email;
}
