package com.deepsleep.data.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateTeacherDTO {
    private String title;
    private LocalDate entryDate;
}