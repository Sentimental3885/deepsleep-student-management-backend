package com.deepsleep.data.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AddTeacherDTO {
    @NotNull(message = "工号不能为空")
    private Long tsid;// 工号

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "性别不能为空")
    @Min(value = 0, message = "性别必须为0-2")
    @Max(value = 2, message = "性别必须为0-2")
    private Integer gender;

    @NotNull(message = "学院ID不能为空")
    private Long did;
    private String title;
    private LocalDate entryDate;
}
